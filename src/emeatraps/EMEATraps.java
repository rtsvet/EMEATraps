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

    public static void main(String[] args) throws Exception {

        if (args.length < 2 ) {
            printUsage();
        }

        Engine engin = new Engine();
        String result = "";
        if (args.length >= 3) {
            engin.setModelfile(args[2]);
        }

        engin.setDataFile(args[1]);

        if (args[0].equalsIgnoreCase("test")) {
            engin.setDataTrainFile(args[3]);
            result = engin.testData();
        } else if (args[0].equalsIgnoreCase("retrain")) {
            result = engin.retrainModel();
        } else if (args[0].equalsIgnoreCase("train")) {
            result = engin.trainModel();
        }
        System.out.println(result);
    }

    private static void printUsage() {
        System.out.println(" EMEATraps test DATA_FILE [MODEL_FILE MODEL_DATA] ");
        System.out.println(" EMEATraps retrain DATA_FILE [MODEL_FILE]");
        System.out.println(" EMEATraps train DATA_FILE [MODEL_FILE] ");
    }
}
