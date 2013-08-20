package emeatraps;

/**
 *
 * @author Radoslav Tsvetkov
 */
import java.io.BufferedReader;
import java.io.FileReader;
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

public class EMEATraps {

    private static final String MODEL_FILE = "/export/Development/DataMining/TrapsEMEA/Model_50k_ac.model";
    private static final String DATA_TEST_FILE = "/export/Development/DataMining/TrapsEMEA/TrapsEMEA_50k_ad.arff";

    public static void main(String[] args) throws Exception {
        // Read Train Data
        Instances dataTest = new Instances(new BufferedReader(new FileReader(DATA_TEST_FILE)));
        Remove remFilter = new Remove();
        remFilter.setAttributeIndices("8");
        remFilter.setInputFormat(dataTest);
        Instances data = Filter.useFilter(dataTest, remFilter);
        data.setClassIndex(data.numAttributes() - 1);

        // String to Vector
        StringToWordVector str2VecFlt = new StringToWordVector();
        str2VecFlt.setInputFormat(data);
        // Instances vetorData = Filter.useFilter(data, str2Vec);

        FilteredClassifier cls;

        // Make Model
        J48 j48Class = new J48();
        j48Class.setUnpruned(true);

        cls = new FilteredClassifier();
        cls.setFilter(str2VecFlt);
        cls.setClassifier(j48Class);
        // train 
        cls.buildClassifier(data);

        weka.core.SerializationHelper.write(MODEL_FILE, cls);

        // Load Model
        //cls = (Classifier) weka.core.SerializationHelper.read(MODEL_FILE);

        // Evaluate
        //Evaluation eval = new Evaluation(vetorData);
        //eval.evaluateModel(cls, vetorData);
        //System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    }
}
