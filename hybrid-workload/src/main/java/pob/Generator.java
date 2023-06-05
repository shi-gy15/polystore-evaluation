package pob;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonValue;

import java.io.*;
import java.util.*;

public class Generator {
    public BsonDocument configuration;
    static BsonDocument scale2WriteHeavy() {
        BsonDocument conf = new BsonDocument();
        conf.put("createUserPrev", new BsonInt32(10000));
        conf.put("createUser", new BsonInt32(90000));
        conf.put("createFollow", new BsonInt32(450000));
        conf.put("createSubscribe", new BsonInt32(100000));
        conf.put("createRate", new BsonInt32(100000));
        conf.put("createTrack", new BsonInt32(7610));
        conf.put("findUser", new BsonInt32(120000));
        conf.put("findArtist", new BsonInt32(120000));
        conf.put("findFollow", new BsonInt32(120000));
        conf.put("findSubscribe", new BsonInt32(120000));
        conf.put("findRate", new BsonInt32(120000));
        conf.put("findTrack", new BsonInt32(100000));
        conf.put("task1", new BsonInt32(1000));
        conf.put("task2", new BsonInt32(1000));
        conf.put("task4", new BsonInt32(1000));
        return conf;
    }

    static BsonDocument scale2ReadHeavy() {
        BsonDocument conf = new BsonDocument();
        conf.put("createUserPrev", new BsonInt32(10000));
        conf.put("createUser", new BsonInt32(90000));
        conf.put("createFollow", new BsonInt32(450000));
        conf.put("createSubscribe", new BsonInt32(100000));
        conf.put("createRate", new BsonInt32(100000));
        conf.put("createTrack", new BsonInt32(7610));
        conf.put("findUser", new BsonInt32(1200000));
        conf.put("findArtist", new BsonInt32(1200000));
        conf.put("findFollow", new BsonInt32(1200000));
        conf.put("findSubscribe", new BsonInt32(1200000));
        conf.put("findRate", new BsonInt32(1200000));
        conf.put("findTrack", new BsonInt32(1000000));
        return conf;
    }

    Queue<Long> users;
    ArrayList<String> userData;
    PriorityQueue<Long> subscribes;
    PriorityQueue<Long> rates;
    PriorityQueue<Long> follows;
    ArrayList<String> followData;
    ArrayList<String> subscribeData;
    ArrayList<String> rateData;

    Queue<Long> tracks;

    public void readFile(String filepath, Queue<Long> q, boolean readUser) {
        try {
            Scanner scanner = new Scanner(new File(filepath));
            int recordID = -1;
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (recordID == -1) {
                    recordID = 0;
                    continue;
                }
                long userID = 0;
                long key;
                if (readUser) {
                    for (int i = 0; i < line.length(); ++i) {
                        if (line.charAt(i) == ',')
                            break;
                        userID *= 10;
                        userID += line.charAt(i) - '0';
                    }
                    key = (userID << 20) | recordID;
                } else {
                    String[] splits = line.split(",");
                    long sourceUserID = 0, targetUserID = 0;
                    for (int i = 0; i < splits[0].length(); ++i) {
                        sourceUserID *= 10;
                        sourceUserID += splits[0].charAt(i) - '0';
                    }
                    for (int i = 0; i < splits[1].length(); ++i) {
                        targetUserID *= 10;
                        targetUserID += splits[1].charAt(i) - '0';
                    }
                    userID = Math.max(sourceUserID, targetUserID);
                    key = (userID << 20) | recordID;
                }
                q.add(key);

                recordID++;
            }
            System.out.println("Read " + recordID + " records from " + filepath + ".");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    public void readDataset(String datasetDir) {
        // user
        long nCreateUserPrev = configuration.getInt32("createUserPrev").intValue();
        long nCreateUser = configuration.getInt32("createUser").intValue();
        users = new LinkedList<>();
        for (long i = 0; i < nCreateUser; ++i) {
            users.add(nCreateUserPrev + i);
        }

        // follow
        follows = new PriorityQueue<>();
        readFile(datasetDir + "/follow/user_user.A00001", follows, false);

        // subscribe
        subscribes = new PriorityQueue<>();
        readFile(datasetDir + "/subscribe.csv00001", subscribes, true);

        // rate
        rates = new PriorityQueue<>();
        readFile(datasetDir + "/rate.csv00001", rates, true);

        // track
        long nCreateTrack = configuration.getInt32("createTrack").intValue();
        tracks = new LinkedList<>();
        for (long i = 0; i < nCreateTrack; ++i) {
            tracks.add(i);
        }
    }

    public void generateOperationSequence(String outputFilepath) {
        if (configuration == null) {
            System.out.println("Not configured");
            return;
        }
        
        final String endl = "\n";

        int nRecords = 0;
        int nTypes = configuration.containsKey("createUserPrev") ? (configuration.size() - 1) : configuration.size();

        String[] typeNames = new String[nTypes];
        int[] typeAmount = new int[nTypes];
        double[] typePercentile = new double[nTypes];

        int j = 0;
        for (Map.Entry<String, BsonValue> entry: configuration.entrySet()) {
            if (entry.getKey().equals("createUserPrev"))
                continue;
            typeNames[j] = entry.getKey();
            typeAmount[j] = entry.getValue().asInt32().getValue();
            typePercentile[j] = (double) typeAmount[j];
            nRecords += typeAmount[j];
            ++j;
        }

        for (int i = 0; i < nTypes; ++i) {
            if (i == 0)
                typePercentile[i] = typePercentile[i] / nRecords;
            else
                typePercentile[i] = typePercentile[i] / nRecords + typePercentile[i - 1];
            System.out.println(typeNames[i] + ": " + typePercentile[i]);
        }

        Random random = new Random();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilepath, true), 1024 * 1024);
            // 0.1. artist
            int nCreateArtist = 20030;
            for (int i = 0; i < nCreateArtist; ++i) {
                writer.write("createArtistPrev|" + i + endl);
            }

            // 0.2. aria
            int nCreateAria= 21060;
            for (int i = 0; i < nCreateAria; ++i) {
                writer.write("createAriaPrev|" + i + endl);
            }

            // 1. user prev
            int nCreateUserPrev = configuration.getInt32("createUserPrev").intValue();
            for (int i = 0; i < nCreateUserPrev; ++i) {
                writer.write("createUserPrev|" + i + endl);
            }
            //
            long key;
            long mask = (1 << 20) - 1;
            long userID;
            long artistID;
            long trackID;
            long edgeID;
            long ub;

            for (int i = 0; i < nRecords; ++i) {
                if (i % 50000 == 0) {
                    System.out.println("Generating " + i);
                }
                double x = random.nextDouble();
                for (j = 0; j < nTypes; ++j) {
                    if (x < typePercentile[j]) {
                        // generate for type j
                        switch (typeNames[j]) {
                            case "createUser":
                                if (users.isEmpty()) {
                                    i--;
                                    break;
                                }
                                writer.write("createUser|" + users.poll() + endl);
                                break;
                            case "createFollow":
                                if (follows.isEmpty())
                                    break;
                                key = follows.peek();
                                userID = key >> 20;
                                edgeID = key & mask;
                                if (!users.isEmpty() && userID >= users.peek()) {
                                    writer.write("createUser|" + users.poll() + endl);
                                } else {
                                    writer.write("createFollow|" + edgeID + endl);
                                    follows.poll();
                                }
                                break;
                            case "createSubscribe":
                                if (subscribes.isEmpty())
                                    break;
                                key = subscribes.peek();
                                userID = key >> 20;
                                edgeID = key & mask;
                                if (!users.isEmpty() && userID >= users.peek()) {
                                    writer.write("createUser|" + users.poll() + endl);
                                } else {
                                    writer.write("createSubscribe|" + edgeID + endl);
                                    subscribes.poll();
                                }
                                break;
                            case "createRate":
                                if (rates.isEmpty())
                                    break;
                                key = rates.peek();
                                userID = key >> 20;
                                edgeID = key & mask;
                                if (!users.isEmpty() && userID >= users.peek()) {
                                    writer.write("createUser|" + users.poll() + endl);
                                } else {
                                    writer.write("createRate|" + edgeID + endl);
                                    rates.poll();
                                }
                                break;
                            case "createTrack":
                                if (tracks.isEmpty())
                                    break;
                                writer.write("createTrack|" + tracks.poll() + endl);
                                break;
                            case "findUser":
                            case "findFollow":
                            case "findSubscribe":
                            case "findRate":
                                ub = users.isEmpty() ? 100000 : users.peek();
                                userID = (long) (ub * random.nextDouble());
                                writer.write(typeNames[j] + "|" + userID + endl);
                                break;
                            case "findArtist":
                                artistID = (long) (20030 * random.nextDouble());
                                writer.write("findArtist|" + artistID + endl);
                                break;
                            case "findTrack":
                                trackID = (long) (7610 * random.nextDouble());
                                writer.write("findTrack|" + trackID + endl);
                                break;
                            case "task1":
                            case "task2":
                            case "task4":
                            default:
                                writer.write(typeNames[j] + '|' + endl);
                                break;
                        }
                        break;
                    }
                }
            }

            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
    }

    public void splitSequence(String sequenceFilepath, String outputDir, int nWorkers) {
        BufferedWriter[] writers = new BufferedWriter[nWorkers];
        try {
            for (int i = 0; i < nWorkers; ++i) {
                String filepath = outputDir + "/" + i + ".seq";
                writers[i] = new BufferedWriter(new FileWriter(filepath, true), 1024 * 1024);
            }

            String s;
            int j = 0;
            Scanner scanner = new Scanner(new File(sequenceFilepath));
            while (scanner.hasNextLine()) {
                s = scanner.nextLine();
                writers[j % nWorkers].write(s);
                writers[j % nWorkers].write('\n');
                j++;
            }

            for (int i = 0; i < nWorkers; ++i) {
                writers[i].close();
            }
        } catch (IOException ioe) {

        }
    }
}
