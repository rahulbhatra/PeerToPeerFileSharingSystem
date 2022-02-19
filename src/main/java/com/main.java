package com;

import com.test.CentralIndexingServerTest;
import com.test.PeerServerTest;
import com.test.PeerTest;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;

import java.util.Scanner;

public class main {
    public static void main(String[] args) {

        FileUtil.createFile(1, "abcd", ConstantsUtil.shared, 5);

        System.out.println("Enter what kind of test do you want to run?");
        System.out.println("1. Central Indexing Server methods verification.");
        System.out.println("2. Peer Server test.");
        System.out.println("3. Client sequential calls test.");
        System.out.println("4. Client parallel calls test.");

        boolean isCorrectInput = false;
        while (!isCorrectInput) {
            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    CentralIndexingServerTest.clientIndexingServer();
                    isCorrectInput = true;
                    break;
                case 2:
                    PeerServerTest.peerServerTest();
                    isCorrectInput = true;
                    break;
                case 3:
                    PeerTest.sequentialTesting();
                    isCorrectInput = true;
                    break;
                case 4:
                    PeerTest.parallelPeerTesting();
                    isCorrectInput = true;
                    break;
                default:
                    System.out.println("Selected Wrong Input !");
            }
        }
    }
}
