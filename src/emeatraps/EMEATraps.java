package emeatraps;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Radoslav Tsvetkov
 */
public class EMEATraps {

    public static void main(String[] args) throws Exception {

        if (args.length < 2 || args.length > 3) {
            printUsage();
        }

        Properties prop = new Properties();
        prop.load(new FileInputStream("config.properties"));
        
        Engine engin = new Engine(prop);
        String result = "";
        engin.setLimit(Integer.parseInt(args[1]));
        engin.setOffset(Integer.parseInt(args[2]));
        engin.installData();

        if (args[0].equalsIgnoreCase("test")) {
            result = engin.testData();
        } else if (args[0].equalsIgnoreCase("retrain")) {
            result = engin.retrainModel();
        } else if (args[0].equalsIgnoreCase("train")) {
            result = engin.trainModel();
        }
        System.out.println(result);
    }

    private static void printUsage() {
        System.out.println("java -jar EMEATraps.jar test LIMIT OFFSET ");
        System.out.println("java -jar EMEATraps.jar train LIMIT OFFSET");
    }
}
