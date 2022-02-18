
package com.threads;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.models.PeerFile;
import com.models.TestResults;
import com.test.PeerTest;
import com.utility.ConstantsUtil;
import junit.framework.TestResult;

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
    private Map<Integer, TestResults> testResults;
    private Peer firstPeer;
    private CentralIndexingServerInterface centralIndexingServerInterface;

    public PeerTestThread(String directory, Peer peer, Peer firstPeer, List<String> sharedFiles,
                          Map<Integer,  TestResults> testResults,
                          CentralIndexingServerInterface centralIndexingServerInterface) {
        this.directory = directory;
        this.peer = peer;
        this.sharedFiles = sharedFiles;
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

        try {
            String searchFile = ConstantsUtil.FILE_NAME_IN_PARALLEL_SEARCH;
            List<String> clientList = centralIndexingServerInterface.search(searchFile);
            PeerServerInterface peerServerInterface = (PeerServerInterface) Naming.lookup(firstPeer.getId());

            long retrieveStartTime = System.currentTimeMillis();
            testResults.get(peer.getPeerNumber()).setRetrieveStartTime(retrieveStartTime);
            System.out.println("Peer " + peer.getPeerNumber() + ": Retrieve start time = " + retrieveStartTime);
            PeerFile peerFile = null;
            for(int i = 0; i < 500; i ++) {
                peerFile = peerServerInterface.retrieve(peer.getId(), searchFile);
            }
            long retrieveEndTime = System.currentTimeMillis();
            testResults.get(peer.getPeerNumber()).setRetrieveEndTime(System.currentTimeMillis());
            System.out.println("Peer " + peer.getPeerNumber() + ": Retrieve end time = " + retrieveEndTime);

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