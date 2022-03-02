
package com.threads;

import com.interfaces.SuperPeerServerInterface;
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
    private SuperPeerServerInterface superPeerServerInterface;

    public PeerTestThread(String directory, Peer peer, Peer firstPeer, List<String> sharedFiles,
                          Map<Integer,  TestResults> testResults,
                          SuperPeerServerInterface superPeerServerInterface) {
        this.directory = directory;
        this.peer = peer;
        this.sharedFiles = sharedFiles;
        this.testResults = testResults;
        this.firstPeer = firstPeer;
        this.superPeerServerInterface = superPeerServerInterface;
        this.directoryWatcher = new DirectoryWatcher(directory);
    }

    @Override
    public void run() {
        System.out.println("First peer is: " + firstPeer.getPeerId() + "First Peer Number: " + firstPeer.getId());
        System.out.println("Thread is Starting for Peer Id: " + peer.getPeerId() + " Peer Number: " + peer.getId());
        FileUtil.startDirectoryLogging(peer.getId(), peer.getSuperPeerId());
        Runtime.getRuntime().addShutdownHook(new DeRegistryThread(peer.getId(), sharedFiles,
                superPeerServerInterface));

        try {
            PeerServerInterface peerServerInterface = (PeerServerInterface) Naming.lookup(firstPeer.getPeerId());
            TestResultsUtil.getRetrievalResults(firstPeer, peer, directory, testResults.get(peer.getId()), peerServerInterface);
            TestResultsUtil.getSearchResults(firstPeer, peer, testResults.get(peer.getId()), superPeerServerInterface);
            System.out.println("Exiting Peer " + peer.getId());
            directoryWatcher.endWatchingChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}