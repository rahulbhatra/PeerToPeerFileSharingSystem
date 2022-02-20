
package com.threads;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.models.TestResults;
import com.utility.FileUtil;
import com.utility.TestResultsUtil;

import java.util.*;
import java.rmi.Naming;

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
            TestResultsUtil.getRetrievalResults(firstPeer, peer, directory, testResults.get(peer.getPeerNumber()), peerServerInterface);
            TestResultsUtil.getSearchResults(firstPeer, peer, testResults.get(peer.getPeerNumber()), centralIndexingServerInterface);
            System.out.println("Exiting Peer " + peer.getPeerNumber());
            directoryWatcher.endWatchingChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}