package com.utility;

import com.interfaces.SuperPeerServerInterface;
import com.interfaces.PeerServerInterface;
import com.models.Peer;
import com.models.TestResults;

import java.rmi.RemoteException;

public class TestResultsUtil {
    public static void getRetrievalResults(Peer firstPeer, Peer peer, String directory, TestResults testResults, PeerServerInterface peerServerInterface) throws RemoteException {
        long retrieveStartTime = System.currentTimeMillis();
        testResults.setRetrieveStartTime(retrieveStartTime);
        System.out.println("Peer " + peer.getId() + ": Retrieve start time = " + retrieveStartTime);
        for(int j = 0; j < 500; j ++) {
            for (String searchFile: firstPeer.getFiles()) {
                peerServerInterface.retrieve(peer.getId(), directory, searchFile);
            }
        }
        long retrieveEndTime = System.currentTimeMillis();
        testResults.setRetrieveEndTime(System.currentTimeMillis());
        System.out.println("Peer " + peer.getId() + ": Retrieve end time = " + retrieveEndTime);
    }

    public static void getSearchResults(Peer firstPeer, Peer peer, TestResults testResults, SuperPeerServerInterface superPeerServerInterface) throws RemoteException {
        long searchStartTime = System.currentTimeMillis();
        testResults.setSearchStartTime(searchStartTime);
        System.out.println("Peer " + peer.getId() + ": Search start time = " + searchStartTime);
        for(int j = 0; j < 500; j ++) {
            for (String searchFile: firstPeer.getFiles()) {
                superPeerServerInterface.search(searchFile);
            }
        }
        long searchEndTime = System.currentTimeMillis();
        testResults.setSearchEndTime(searchEndTime);
        System.out.println("Peer " + peer.getId() + ": Search end time = " + searchEndTime);
    }
}
