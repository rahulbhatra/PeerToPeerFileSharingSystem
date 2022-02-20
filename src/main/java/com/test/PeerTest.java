
package com.test;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.models.TestResults;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import com.threads.DeRegistryThread;
import com.threads.PeerTestThread;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import com.utility.TestResultsUtil;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

public class PeerTest extends Thread {

    public static void parallelPeerTesting(int numberOfPeers) {
        String sharedDirectory = ConstantsUtil.shared;
        System.out.println("Initializing " + numberOfPeers + " number of peers for multithreaded testing.");
        PeerTestThread[] peerTestThreads = new PeerTestThread[numberOfPeers];
        Peer[] peers = new Peer[numberOfPeers];
        Map<Integer, TestResults> testResults = new HashMap<>();

        try {
            // Connecting all the clients with the server.
            CentralIndexingServerInterface centralIndexingServerInterface = new CentralIndexingServer(Integer.parseInt(ConstantsUtil.PORT),
                    ConstantsUtil.CENTRAL_INDEXING_SERVER);
//            CentralIndexingServerThread centralIndexingServerThread = new CentralIndexingServerThread();
//            centralIndexingServerThread.start();
//            Thread.sleep(4000);
//            CentralIndexingServerInterface centralIndexingServerInterface = (CentralIndexingServerInterface) Naming.lookup(ConstantsUtil.PEER_SERVER);

            Peer firstPeer = null;
            for (int i = 0; i < numberOfPeers; i++) {
                String directory = sharedDirectory + "/" + i;
                List<String> sharedFiles = FileUtil.readSharedDirectory(true, directory);
                Peer peer = centralIndexingServerInterface.registry("", ConstantsUtil.PEER_SERVER, sharedFiles);
                peers[i] = peer;
                firstPeer = firstPeer == null ? peer : firstPeer;
                testResults.put(peer.getPeerNumber(), new TestResults());
                peerTestThreads[i] = new PeerTestThread(directory, peer, firstPeer, sharedFiles, testResults, centralIndexingServerInterface);
                new PeerServer(peer.getId(), ConstantsUtil.PEER_SERVER, directory);
                System.out.println(ConstantsUtil.PEER_REGISTRATION_DONE
                        + peer.getId() + " | Peer Number: " + peer.getPeerNumber());

            }

            for (int i = 0; i < numberOfPeers; i++) {
                peerTestThreads[i].start();
            }
            for (int i = 0; i < numberOfPeers; i++) {
                peerTestThreads[i].join();
            }
            fetchResults(numberOfPeers, peers[0].getFiles().size(), peers, testResults);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public static void sequentialTesting(int numberOfPeers) {
        String sharedDirectory = "./shared";
        System.out.println("Initializing " + numberOfPeers + " number of peers for sequential testing.");
        Peer[] peers = new Peer[numberOfPeers];
        Map<Integer, TestResults> testResults = new HashMap<>();
        int totalFileCounts = 0;

        try {
            CentralIndexingServerInterface centralIndexingServerInterface = new CentralIndexingServer(Integer.parseInt(ConstantsUtil.PORT),
                    ConstantsUtil.CENTRAL_INDEXING_SERVER);
            Peer firstPeer = null;
            for (int i = 0; i < numberOfPeers; i++) {
                String directory = sharedDirectory + "/" + i;
                List<String> sharedFiles = FileUtil.readSharedDirectory(true, directory);
                totalFileCounts += sharedFiles.size();
                Peer peer = centralIndexingServerInterface.registry("", ConstantsUtil.PEER_SERVER, sharedFiles);
                new PeerServer(peer.getId(), ConstantsUtil.PEER_SERVER, directory);
                System.out.println(ConstantsUtil.PEER_REGISTRATION_DONE
                        + peer.getId() + " | Peer Number: " + peer.getPeerNumber());

                peers[i] = peer;
                firstPeer = firstPeer == null ? peer : firstPeer;
                testResults.put(peer.getPeerNumber(), new TestResults());

                DirectoryWatcher directoryWatcher = new DirectoryWatcher(directory);
                FileUtil.startDirectoryLogging(peer, directory, sharedFiles, directoryWatcher, centralIndexingServerInterface);
                Runtime.getRuntime().addShutdownHook(new DeRegistryThread(peer.getId(), sharedFiles,
                        centralIndexingServerInterface));

                try {
                    PeerServerInterface peerServerInterface = (PeerServerInterface) Naming.lookup(firstPeer.getId());
                    TestResultsUtil.getRetrievalResults(firstPeer, peer, directory, testResults.get(peer.getPeerNumber()), peerServerInterface);
                    TestResultsUtil.getSearchResults(firstPeer, peer, testResults.get(peer.getPeerNumber()), centralIndexingServerInterface);
                    directoryWatcher.endLogging();
                    System.out.println("Exiting Peer " + peer.getPeerNumber());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fetchResults(numberOfPeers, totalFileCounts, peers, testResults);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void fetchResults(int numberOfPeers, int filesUsedInOperations, Peer[] peers, Map<Integer, TestResults> testResults) {
        int numberOfRequest = 500 * filesUsedInOperations * numberOfPeers;
        Double totalRetrievalTime = 0.0;

        System.out.println(ConstantsUtil.STARTING_RESULTS);
        for (int i = 0; i < numberOfPeers; i++) {
            Peer peer = peers[i];
            TestResults tr = testResults.get(peer.getPeerNumber());
            totalRetrievalTime = totalRetrievalTime + tr.getRetrieveEndTime() - tr.getRetrieveStartTime();
            System.out.println("Peer " + i + ": retrieval start = " + tr.getRetrieveStartTime() +
                    ", retrieval end = " + tr.getRetrieveEndTime());
        }
        System.out.println("Total retrieval time for all peers = " + totalRetrievalTime + " Number of request made: " + numberOfRequest);
        Double averageRetrievalTime = totalRetrievalTime / numberOfRequest;
        System.out.println("Average retrieval time for one peer = " + averageRetrievalTime);
        System.out.println(ConstantsUtil.ENDING_RESULTS);

        System.out.println(ConstantsUtil.STARTING_RESULTS);
        Double totalSearchTime = 0.0;
        for (int i = 0; i < numberOfPeers; i++) {
            Peer peer = peers[i];
            TestResults tr = testResults.get(peer.getPeerNumber());
            totalSearchTime = totalSearchTime + tr.getSearchEndTime() - tr.getSearchStartTime();
            System.out.println("Peer " + i + ": searching start = " + tr.getSearchStartTime() +
                    ", searching end = " + tr.getSearchEndTime());
        }

        System.out.println("Total search time for all peers = " + totalSearchTime + " Number of request made: " + numberOfRequest);
        Double averageSearchTime = totalSearchTime / numberOfRequest;
        System.out.println("Average search time for one peer = " + averageSearchTime);

        System.out.println(ConstantsUtil.ENDING_RESULTS);
        System.out.println("Test is ending");
    }
}