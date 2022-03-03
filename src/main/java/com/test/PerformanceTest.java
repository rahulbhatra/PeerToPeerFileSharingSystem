
package com.test;

import com.models.TestResults;
import com.threads.LeafNodeTestThread;
import com.utility.ConstantsUtil;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;

public class PerformanceTest {

    public static void performanceTesting(int numberOfPeers, int bufferSize, int ttl,
                                          JSONArray superPeerToSuperPeerNeighbours,
                                          JSONArray superPeerToPeerNeighbours) {

        System.out.println("Initializing testing for Number of Peers : " + numberOfPeers +
                " Number of SuperPeers: " + superPeerToSuperPeerNeighbours.length());
        SuperPeerServerTest.clientIndexingServer(bufferSize, ttl, superPeerToSuperPeerNeighbours, superPeerToPeerNeighbours);
        LeafNodeTestThread[] peerTestThreads = new LeafNodeTestThread[numberOfPeers];
        Map<Integer, TestResults> performanceResults = new HashMap<>();

        try {
            for (int i = 0; i < numberOfPeers; i++) {
                performanceResults.put(i, new TestResults());
                peerTestThreads[i] = new LeafNodeTestThread(i, numberOfPeers, ttl, performanceResults);
                System.out.println(ConstantsUtil.PEER_REGISTRATION_DONE + i);

            }
            for (int i = 0; i < numberOfPeers; i++) {
                peerTestThreads[i].start();
            }
            for (int i = 0; i < numberOfPeers; i++) {
                peerTestThreads[i].join();
            }
            fetchResults(numberOfPeers, performanceResults);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public static void fetchResults(int numberOfPeers, Map<Integer, TestResults> testResults) {
        Double totalRetrievalTime = 0.0;
        int numberOfRetrievalRequest = 0;
        System.out.println(ConstantsUtil.STARTING_RESULTS);
        for (int i = 0; i < numberOfPeers; i++) {
            TestResults tr = testResults.get(i);
            totalRetrievalTime = totalRetrievalTime + tr.getObtainEndTime() - tr.getObtainStartTime();
            numberOfRetrievalRequest += tr.getNumberOfObtainRequest();
            System.out.println("Peer " + i + ": obtain start = " + tr.getObtainStartTime() +
                    ", obtain end = " + tr.getObtainEndTime());
        }
        System.out.println("Total retrieval time for all peers = " + totalRetrievalTime + " Number of request made: " + numberOfRetrievalRequest);
        Double averageRetrievalTime = totalRetrievalTime / numberOfRetrievalRequest;
        System.out.println("Average obtain time for one peer = " + averageRetrievalTime);
        System.out.println(ConstantsUtil.ENDING_RESULTS);

        System.out.println(ConstantsUtil.STARTING_RESULTS);
        Double totalSearchTime = 0.0;
        int numberOfSearchRequest = 0;
        for (int i = 0; i < numberOfPeers; i++) {
            TestResults tr = testResults.get(i);
            totalSearchTime = totalSearchTime + tr.getForwardEndTime() - tr.getForwardStartTime();
            numberOfSearchRequest += tr.getNumberOfForwardRequest();
            System.out.println("Peer " + i + ": forwarding start = " + tr.getForwardStartTime() +
                    ", forwarding end = " + tr.getForwardEndTime());
        }

        System.out.println("Total search time for all peers = " + totalSearchTime + " Number of request made: " + numberOfSearchRequest);
        Double averageSearchTime = totalSearchTime / numberOfSearchRequest;
        System.out.println("Average search time for one peer = " + averageSearchTime);

        System.out.println(ConstantsUtil.ENDING_RESULTS);
        System.out.println("Test is ending");
    }
}