package com;

import com.test.CentralIndexingServerTest;
import com.test.PeerServerTest;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

/**
 * To Compile using command line use the following command line instructions.
 * 1. run cd src/main/java/com
 * 2. javac -cp jars/commons-io-2.8.0.jar models/*.java interfaces/*.java logging/*.java server/*.java utility/*.java threads/*.java test/*.java PeerToPeerFileSharing.java
 * 3. run cd ..
 * 4. java -cp com/jars/commons-io-2.8.0.jar:. com.PeerToPeerFileSharing
 */
public class PeerToPeerFileSharing {

    public static void main(String[] args) {

        try {
            Scanner scanner = new Scanner(System.in);
            LocateRegistry.createRegistry(Integer.parseInt(ConstantsUtil.PORT));
            FileUtils.cleanDirectory(new File(ConstantsUtil.shared));
            System.out.println("Which config file to use: 1. simple 2. linear 3. all");

            int configOption = scanner.nextInt();
            String configFile;
            switch (configOption) {
                case 2:
                    configFile = "./json/linear.json";
                    break;
                case 3:
                    configFile = "./json/all.json";
                    break;
                default:
                    configFile = "./json/simple.json";
            }
            System.out.println("User chose the following file | Filename: " + configFile);
            String jsonString = new String(Files.readAllBytes(Paths.get(configFile)));
            JSONObject configObject = new JSONObject(jsonString);
            int bufferSize = configObject.getInt("bufferSize");
            int timeToLive = configObject.getInt("ttl");
            String topology = configObject.getString("topology");
            System.out.println("Topology Used " + topology);

            JSONArray superPeersToSuperPeerNeighboursArray = configObject.getJSONArray("superPeerToSuperPeerNeighbours");
            int numberOfPeers = 0;
            JSONArray superPeersToPeerNeighboursArray = configObject.getJSONArray("superPeerToPeerNeighbours");
            for (int i = 0; i < superPeersToPeerNeighboursArray.length(); i++) {
                numberOfPeers += superPeersToPeerNeighboursArray.getJSONArray(i).length();
            }
            System.out.println("Total Number of peers are :" + numberOfPeers);
            FileUtil.createFiles(numberOfPeers);


            System.out.println("Enter what kind of test do you want to run?");
            System.out.println("1. Central Indexing Server methods verification.");
            System.out.println("2. Peer Server test.");
            System.out.println("3. Client sequential calls test.");
            System.out.println("4. Client parallel calls test.");

            boolean isCorrectInput = false;
            while (!isCorrectInput) {
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        CentralIndexingServerTest.clientIndexingServer(
                                bufferSize, timeToLive, superPeersToSuperPeerNeighboursArray, superPeersToPeerNeighboursArray);
                        isCorrectInput = true;
                        break;
                    case 2:
                        PeerServerTest.peerServerTest(numberOfPeers, bufferSize, timeToLive, superPeersToSuperPeerNeighboursArray, superPeersToPeerNeighboursArray);
                        isCorrectInput = true;
                        break;
                    case 3:
//                    PeerTest.sequentialTesting(numberOfPeers);
                        isCorrectInput = true;
                        break;
                    case 4:
//                    PeerTest.parallelPeerTesting(numberOfPeers);
                        isCorrectInput = true;
                        break;
                    default:
                        System.out.println("Selected Wrong Input !");
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
