
package com.threads;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.models.PeerFile;
import com.test.PeerTest;
import com.utility.ConstantsUtil;

import java.util.*;
import java.rmi.Naming;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PeerTestThread extends Thread {

    private String directory;
    private Peer peer;
    private DirectoryWatcher directoryLogs;
    private List<String> sharedFiles;
    private Map<Integer, PeerTest.TestResult> testResults;
    private int numberOfPeers;
    private Peer firstPeer;
    private CentralIndexingServerInterface centralIndexingServerInterface;

    public PeerTestThread(String directory, Peer peer, Peer firstPeer, List<String> sharedFiles,
                          int numberOfPeers, Map<Integer,  PeerTest.TestResult> testResults,
                          CentralIndexingServerInterface centralIndexingServerInterface) {
        this.directory = directory;
        this.peer = peer;
        this.sharedFiles = sharedFiles;
        this.numberOfPeers = numberOfPeers;
        this.testResults = testResults;
        this.firstPeer = firstPeer;
        this.centralIndexingServerInterface = centralIndexingServerInterface;
    }

    @Override
    public void run() {
        System.out.println("First peer is: " + firstPeer.getId() + "First Peer Number: " + firstPeer.getPeerNumber());
        System.out.println("Thread is Starting for Peer Id: " + peer.getId() + " Peer Number: " + peer.getPeerNumber());
        startDirectoryLogging();
        Runtime.getRuntime().addShutdownHook(new DeRegistryThread(peer.getId(), sharedFiles,
                centralIndexingServerInterface));

        if (peer.getId().equalsIgnoreCase(firstPeer.getId())) {
            System.out.println("Peer Thread started to get the result after execution of other two threads");
//            System.out.println("Peer " + peer.getPeerNumber() + ": thread_end_count = " +
//                    thread_end_count + ", num_peers = " + numberOfPeers);

//            while (thread_end_count < (numberOfPeers - 1)) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            Long averageTime = 0L;

            System.out.println("======================== Test Results ================================");
            for (int i = 1; i < numberOfPeers; i++) {
                averageTime = averageTime + (testResults.get(peer.getPeerNumber()).retrieveEndTime - testResults.get(peer.getPeerNumber()).retrieveStartTime);
                System.out.println("Peer " + i + ": start = " + testResults.get(peer.getPeerNumber()).retrieveStartTime +
                        ", end = " + testResults.get(peer.getPeerNumber()).retrieveEndTime);
            }
            averageTime = averageTime / (numberOfPeers - 1);
            System.out.println("Average time but for peer = " + averageTime);
            System.out.println("File size = 10K");
            System.out.println("=======================================================================");
            System.out.println("Exiting Peer " + peer.getPeerNumber());

            System.out.println("Directory Logging is getting closed for PeerId: " + peer.getId());
            directoryLogs.endLogging();
        }

        try {
            String searchFile = ConstantsUtil.FILE_NAME_IN_PARALLEL_SEARCH;
            List<String> clientList = centralIndexingServerInterface.search(searchFile);
            PeerServerInterface peerServerInterface = (PeerServerInterface) Naming.lookup(firstPeer.getId());

            // long retrieveStartTime = System.currentTimeMillis();
            testResults.get(peer.getPeerNumber()).retrieveStartTime = System.currentTimeMillis();
            System.out.println("Peer " + peer.getPeerNumber() + ": Retrieve start time = " + testResults.get(peer.getPeerNumber()).retrieveStartTime);
            PeerFile peerFile = peerServerInterface.retrieve(firstPeer.getId(), searchFile);
            testResults.get(peer.getPeerNumber()).retrieveEndTime = System.currentTimeMillis();
            System.out.println("Peer " + peer.getPeerNumber() + ": Retrieve end time = " + testResults.get(peer.getPeerNumber()).retrieveEndTime);

            File file = new File(directory, peerFile.getFileName());
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file, true);

            if (peerFile.getData().length > 0) {
                out.write(peerFile.getData(), 0, peerFile.getData().length);
            } else {
                out.write(peerFile.getData(), 0, 0);
            }
            out.flush();
            out.close();
            System.out.println("Exiting Peer " + peer.getPeerNumber());
            directoryLogs.endLogging();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDirectoryLogging() {
        Path path = Paths.get(directory);
        directoryLogs = new DirectoryWatcher(path);
        DirectoryLogsThread directoryLogsThread = new DirectoryLogsThread(directoryLogs, directory, peer.getId(),
                sharedFiles,
                centralIndexingServerInterface);
        directoryLogsThread.start();
    }
}