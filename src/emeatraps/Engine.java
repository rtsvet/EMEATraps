package emeatraps;

import weka.classifiers.meta.FilteredClassifier;
import java.io.BufferedReader;
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
import weka.gui.beans.ClassifierPerformanceEvaluator;

/**
 *
 * @author Radoslav Tsvetkov
 */
public class Engine {

    private String modelFile = "/export/Development/DataMining/TrapsEMEA/Short/Model_Traps_Short_aa.model";
    private String dataFile = "/export/Development/DataMining/TrapsEMEA/Short/Traps_Short_ab.arff";
    private String dataTrainFile = "/export/Development/DataMining/TrapsEMEA/Short/Traps_Short_aa.arff";
    private FilteredClassifier cls;
    private Instances dataInst;
    private Instances dataTrainInst;

    public Engine() {
    }

    public void setModelfile(String modelfile) {
        this.modelFile = modelfile;
    }

    public void setDataTrainFile(String dataTrainFile) throws Exception {
        this.dataTrainFile = dataTrainFile;
        Instances rowTrainData = new Instances(new BufferedReader(new FileReader(dataTrainFile)));
        Remove remFilter = new Remove();
        remFilter.setAttributeIndices("8");
        remFilter.setInputFormat(rowTrainData);
        dataTrainInst = Filter.useFilter(rowTrainData, remFilter);
        dataTrainInst.setClassIndex(dataTrainInst.numAttributes() - 1);
    }

    public void setDataFile(String df) throws Exception {
        this.dataFile = df;
        Instances rowData = new Instances(new BufferedReader(new FileReader(df)));
        Remove remFilter = new Remove();
        remFilter.setAttributeIndices("8");
        remFilter.setInputFormat(rowData);
        dataInst = Filter.useFilter(rowData, remFilter);
        dataInst.setClassIndex(dataInst.numAttributes() - 1);
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
        StringToWordVector str2VecFlt = new StringToWordVector();
        str2VecFlt.setInputFormat(dataInst);
        cls = new FilteredClassifier();
        cls.setFilter(str2VecFlt);
        cls.setClassifier(j48Class);
        // train 
        cls.buildClassifier(dataInst);

        weka.core.SerializationHelper.write(modelFile, cls);
        return "OK";
    }

    public String retrainModel() throws Exception {
        // Load Model
        cls = (FilteredClassifier) weka.core.SerializationHelper.read(modelFile);
        return "OK";
    }

    public String testData() throws Exception {
        // Load Model
        cls = (FilteredClassifier) weka.core.SerializationHelper.read(modelFile);

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
                String predicted = dataTrainInst.classAttribute().value(pred);
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
