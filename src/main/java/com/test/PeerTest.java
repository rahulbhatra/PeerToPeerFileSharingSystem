
package com.test;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.models.PeerFile;
import com.models.TestResults;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import com.threads.DeRegistryThread;
import com.threads.DirectoryLogsThread;
import com.threads.PeerTestThread;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

public class PeerTest extends Thread {

    public static void parallelPeerTesting() {
        String sharedDirectory = ConstantsUtil.shared;
        int numberOfPeers = 3;
        int totalFileCounts = 0;
        System.out.println("Initializing " + numberOfPeers + " number of peers for multithreaded environment.");
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
                totalFileCounts += sharedFiles.size();
                Peer peer = centralIndexingServerInterface.registry("", ConstantsUtil.PEER_SERVER, sharedFiles);
                peers[i] = peer;
                firstPeer = firstPeer == null ? peer : firstPeer;
                testResults.put(peer.getPeerNumber(), new TestResults());
                peerTestThreads[i] = new PeerTestThread(directory, peer, firstPeer, sharedFiles, testResults, centralIndexingServerInterface);
                new PeerServer(peer.getId(), ConstantsUtil.PEER_SERVER, directory);
                System.out.println("Peer is is registered with central indexing server and is ready | PeerId: "
                        + peer.getId() + " | Peer Number: " + peer.getPeerNumber());

            }

            for (int i = 0; i < numberOfPeers; i++) {
                peerTestThreads[i].start();
            }
            for (int i = 0; i < numberOfPeers; i++) {
                peerTestThreads[i].join();
            }
            fetchResults(numberOfPeers, totalFileCounts, peers, testResults);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public static void sequentialTesting() {
        String sharedDirectory = "./shared";
        int numberOfPeers = 3;
        System.out.println("Initializing " + numberOfPeers + " number of peers for sequential environment.");
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
                System.out.println("Peer is is registered with central indexing server and is ready | PeerId: "
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

                    long retrieveStartTime = System.currentTimeMillis();
                    testResults.get(peer.getPeerNumber()).setRetrieveStartTime(retrieveStartTime);
                    System.out.println("Peer " + peer.getPeerNumber() + ": Retrieve start time = " + retrieveStartTime);
                    for(int j = 0; j < 500; j ++) {
                        for (String searchFile: firstPeer.getFiles()) {
                            peerServerInterface.retrieve(peer.getId(), directory, searchFile);
                        }
                    }
                    long retrieveEndTime = System.currentTimeMillis();
                    testResults.get(peer.getPeerNumber()).setRetrieveEndTime(System.currentTimeMillis());
                    System.out.println("Peer " + peer.getPeerNumber() + ": Retrieve end time = " + retrieveEndTime);

                    System.out.println("Exiting Peer " + peer.getPeerNumber());
                    directoryWatcher.endLogging();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fetchResults(numberOfPeers, totalFileCounts, peers, testResults);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void fetchResults(int numberOfPeers, int totalFileCount, Peer[] peers, Map<Integer, TestResults> testResults) {
        Double averageTime = 0.0;
        System.out.println(ConstantsUtil.STARTING_RESULTS);
        for (int i = 0; i < numberOfPeers; i++) {
            Peer peer = peers[i];
            averageTime = averageTime + (testResults.get(peer.getPeerNumber()).getRetrieveEndTime() - testResults.get(peer.getPeerNumber()).getRetrieveStartTime());
            System.out.println("Peer " + i + ": start = " + testResults.get(peer.getPeerNumber()).getRetrieveStartTime() +
                    ", end = " + testResults.get(peer.getPeerNumber()).getRetrieveEndTime());
        }
        averageTime = averageTime / (500 * totalFileCount * numberOfPeers);
        System.out.println("Average time for one peer = " + averageTime);
        System.out.println("File size = 10K");
        System.out.println(ConstantsUtil.Ending_RESULTS);
        System.out.println("Test is ending");
    }
}