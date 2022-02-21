package com;

import com.test.CentralIndexingServerTest;
import com.test.PeerServerTest;
import com.test.PeerTest;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * To Compile using command line use the following command line instructions.
 *  1. run cd src/main/java/com
 *  2. javac -cp jars/commons-io-2.8.0.jar models/*.java interfaces/*.java logging/*.java server/*.java utility/*.java threads/*.java test/*.java PeerToPeerFileSharing.java
 *  3. run cd ..
 *  4. java -cp com/jars/commons-io-2.8.0.jar:. com.PeerToPeerFileSharing
 */
public class PeerToPeerFileSharing {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        try {
            FileUtils.cleanDirectory(new File(ConstantsUtil.shared));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numberOfPeers;
        do {
            System.out.println("Enter number of peers you want to test for between 3 to 10.");
            numberOfPeers = scanner.nextInt();

            if( numberOfPeers >= 3 && numberOfPeers <= 10) {
                FileUtil.createFiles(numberOfPeers);
            } else {
                System.out.println("Entered value is incorrect!");
            }
        } while (numberOfPeers < 3 || numberOfPeers > 10);



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
                    CentralIndexingServerTest.clientIndexingServer();
                    isCorrectInput = true;
                    break;
                case 2:
                    PeerServerTest.peerServerTest(numberOfPeers);
                    isCorrectInput = true;
                    break;
                case 3:
                    PeerTest.sequentialTesting(numberOfPeers);
                    isCorrectInput = true;
                    break;
                case 4:
                    PeerTest.parallelPeerTesting(numberOfPeers);
                    isCorrectInput = true;
                    break;
                default:
                    System.out.println("Selected Wrong Input !");
            }
        }
    }
}
