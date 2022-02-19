
package com.threads;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.models.PeerFile;
import com.models.TestResults;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;

import java.util.*;
import java.rmi.Naming;
import java.io.File;
import java.io.FileOutputStream;

public class PeerTestThread extends Thread {

    private String directory;
    private Peer peer;
    private DirectoryWatcher directoryWatcher;
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
        this.directoryWatcher = new DirectoryWatcher(directory);
    }

    @Override
    public void run() {
        System.out.println("First peer is: " + firstPeer.getId() + "First Peer Number: " + firstPeer.getPeerNumber());
        System.out.println("Thread is Starting for Peer Id: " + peer.getId() + " Peer Number: " + peer.getPeerNumber());
        FileUtil.startDirectoryLogging(peer, directory, sharedFiles, directoryWatcher, centralIndexingServerInterface);
        Runtime.getRuntime().addShutdownHook(new DeRegistryThread(peer.getId(), sharedFiles,
                centralIndexingServerInterface));

        try {
            PeerServerInterface peerServerInterface = (PeerServerInterface) Naming.lookup(firstPeer.getId());

            long retrieveStartTime = System.currentTimeMillis();
            testResults.get(peer.getPeerNumber()).setRetrieveStartTime(retrieveStartTime);
            System.out.println("Peer " + peer.getPeerNumber() + ": Retrieve start time = " + retrieveStartTime);
            for(int i = 0; i < 500; i ++) {
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
}