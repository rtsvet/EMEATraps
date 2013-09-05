package emeatraps;

import weka.classifiers.meta.FilteredClassifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Debug;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.lazy.IBk;
import weka.core.converters.DatabaseLoader;
import weka.core.converters.DatabaseSaver;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.StringToNominal;

/**
 *
 * @author Radoslav Tsvetkov
 */
public class Engine {

    private String modelFile = "/export/Development/DataMining/TrapsEMEA/Short/Model_Traps.model";
    private FilteredClassifier cls;
    private Instances dataInst;
    private int offset;
    private int limit;

    public Engine() {
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private Instances removeLastRow(Instances di) throws Exception {
        Remove remFilter = new Remove();
        remFilter.setAttributeIndices("1,9");
        remFilter.setInputFormat(di);
        return Filter.useFilter(di, remFilter);
    }

    private Instances convertNominalCollumns(Instances di) throws Exception {
        StringToNominal str2nom = new StringToNominal();
        str2nom.setAttributeRange("1,2,5,6,7");
        str2nom.setInputFormat(di);
        return Filter.useFilter(di, str2nom);
    }

    private Instances getData() throws Exception {
        DatabaseLoader dload = new DatabaseLoader();
        dload.setUrl("jdbc:hsqldb:hsql://localhost:9001/outagesdb");
        dload.setUser("sa");
        dload.setPassword("");
        dload.setCustomPropsFile(new File("/home/cs8170/DatabaseUtils.props"));
        dload.connectToDatabase();
        dload.setQuery("SELECT * FROM PUBLIC.OUTAGES LIMIT " + limit + " OFFSET " + offset + " ;");
        Instances dbData = dload.getDataSet();
        //System.out.println(dbData);
        return dbData;
    }

    void installData() throws Exception {
        dataInst = convertNominalCollumns(removeLastRow(getData()));
        dataInst.setClassIndex(dataInst.numAttributes() - 1);
        //System.out.println(dataInst);
    }

    /**
     * Makes and Trains Model if needed
     *
     * @return String value indicating result
     * @throws Exception
     */
    public String trainModel() throws Exception {
        // Classifier
        J48 j48Class = new J48();
        j48Class.setUnpruned(true);

        // String to Vector
        //MultiFilter mf = new MultiFilter();

        StringToWordVector str2VecFlt = new StringToWordVector();
        str2VecFlt.setUseStoplist(true);
        str2VecFlt.setInputFormat(dataInst);
        cls = new FilteredClassifier();
        cls.setFilter(str2VecFlt);
        cls.setClassifier(j48Class);
        // train 
        cls.buildClassifier(dataInst);

        Object[] objPack = {cls, dataInst};
        weka.core.SerializationHelper.writeAll(modelFile, objPack);
        return "OK";
    }

    //<editor-fold defaultstate="collapsed" desc="retrainModel">
    public String retrainModel() throws Exception {
        // Load Model
        Object[] objectPack = weka.core.SerializationHelper.readAll(modelFile);
        Instances oldDataInst = (Instances) objectPack[1];
        for (Instance instance : oldDataInst) {
        }
        dataInst.addAll(0, oldDataInst);
        //dataInst.setClassIndex(dataInst.numAttributes() - 1);
        return trainModel();
    }
    //</editor-fold>

    public String testData() throws Exception {
        // Load Model
        Object[] objectPack = weka.core.SerializationHelper.readAll(modelFile);
        cls = (FilteredClassifier) objectPack[0];
        Instances dataTrainedInst = (Instances) objectPack[1];

        // Evalutor
        //<editor-fold defaultstate="collapsed" desc="comment">
        /*
         * Evaluation eval = new Evaluation(dataTrain);
         * eval.evaluateModel(cls, data);
         * System.out.println(eval.toSummaryString("\nResults\n======\n", false));
         */
        //</editor-fold>

        int incorrect = 0;
        for (Instance i : dataInst) {
            try {
                int pred = (int) cls.classifyInstance(i);
                String actual = dataInst.classAttribute().value((int) i.classValue());
                String predicted = dataTrainedInst.classAttribute().value(pred);
                if (!actual.equals(predicted)) {
                    incorrect++;
                    System.out.print("actual: " + actual);
                    System.out.println(", predicted: " + predicted);
                }
                //System.out.print((actual.equals(predicted) ? "MATCH " : "NOT"));
                //System.out.print("actual: " + actual);
                //System.out.println(", predicted: " + predicted);
            } catch (Exception e) {
                System.out.println("Instance= " + i + " attrib_7=" + i.stringValue(7));
            }
        }
        System.out.println("incorrect= " + incorrect + " sample size=" + dataInst.size());
        System.out.println("\n=> Incorrect Pct= " + (double) incorrect / (double) dataInst.size());
        return "OK";
    }
}
