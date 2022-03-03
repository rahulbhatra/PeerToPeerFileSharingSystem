
package com.threads;

import com.interfaces.SuperPeerServerInterface;
import com.interfaces.LeafNodeServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.models.TestResults;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import com.utility.TestResultsUtil;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.rmi.Naming;

public class LeafNodeTestThread extends Thread {

    private int peerId;
    private int numberOfPeers;
    private int ttl;
    private Peer peer;
    private String directory;
    private DirectoryWatcher directoryWatcher;
    private List<String> sharedFiles;
    private Map<Integer, TestResults> testResults;
    private LeafNodeServerInterface leafNodeServerInterface;
    private SuperPeerServerInterface superPeerServerInterface;

    public LeafNodeTestThread(int peerId, int numberOfPeers, int ttl, Map<Integer,  TestResults> testResults) {
        try {
            this.peerId = peerId;
            this.numberOfPeers = numberOfPeers;
            this.ttl = ttl;
            this.directory = ConstantsUtil.shared + "/" + peerId;
            this.sharedFiles = FileUtil.readSharedDirectory(true, directory);;
            this.testResults = testResults;
            this.leafNodeServerInterface = (LeafNodeServerInterface) Naming.lookup(ConstantsUtil.PEER_SERVER + "-" + peerId);
            this.peer = leafNodeServerInterface.getPeer();
            this.superPeerServerInterface = (SuperPeerServerInterface) Naming.lookup(ConstantsUtil.SUPER_PEER_SERVER + "-" + peer.getSuperPeerId());
            this.directoryWatcher = new DirectoryWatcher(directory);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Thread is Starting for Peer Id: " + peer.getPeerLookUpId() + " Peer Number: " + peer.getPeerId());
        FileUtil.startDirectoryLogging(peer);
        Runtime.getRuntime().addShutdownHook(new DeRegistryThread(peer.getPeerId(), sharedFiles,
                superPeerServerInterface));

        try {
            TestResultsUtil.getForwardResults(numberOfPeers, ttl, peer, testResults.get(peerId), superPeerServerInterface);
            TestResultsUtil.getObtainResults(numberOfPeers, ttl, peer, testResults.get(peerId), superPeerServerInterface);
            System.out.println("Exiting Peer " + peer.getPeerId());
            directoryWatcher.endWatchingChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}