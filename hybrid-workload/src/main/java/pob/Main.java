package pob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.*;

public class Main {
    public static void generate(String datasetDir) {
        long beforeGen = System.nanoTime();
        Generator generator = new Generator();
        generator.configuration = Generator.scale2ReadHeavy();
        generator.readDataset(datasetDir);
        generator.generateOperationSequence("./output/sf2-r.pseq");
        long afterGen = System.nanoTime();

        System.out.println("Generation time: " + (afterGen - beforeGen) / 1000000 + " ms");

        try {
            Scanner scanner = new Scanner(new File("output/sf2-r.pseq"));
            HashMap<String, Integer> typeAmount = new HashMap<>();
            String line;
            String typeName;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                typeName = line.split("\\|")[0];
                typeAmount.put(typeName, typeAmount.getOrDefault(typeName, 0) + 1);
            }
            for (Map.Entry<String, Integer> entry : typeAmount.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private static Logger logger = Logger.getGlobal();

    public static void main(String[] args) {
        String func = args[0];
        switch (func) {
            case "generate":
                String datasetDir = args[1];
                char workloadSpec = args[2];
                Generator generator = new Generator();

                generator.configuration = workloadSpec == 'w'
                                        ? Generator.scale2WriteHeavy()
                                        : Generator.scale2ReadHeavy();
                generator.readDataset(datasetDir);
                generator.generateOperationSequence("./output/sf2-" + workloadSpec + ".pseq");
                break;
            case "split":
                int nWorkers = Integer.parseInt(args[1]);
                String seqFilepath = args[2];
                String outputFilepath = args[3];
                new Generator().splitSequence(seqFilepath, outputFilepath, nWorkers);
                break;
            default:
                break;
        }
    }
}