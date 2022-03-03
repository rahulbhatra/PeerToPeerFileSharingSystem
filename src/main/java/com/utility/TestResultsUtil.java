package com.utility;

import com.interfaces.SuperPeerServerInterface;
import com.models.*;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.Set;

public class TestResultsUtil {
    public static void getObtainResults(int numberOfPeers, int ttl, Peer peer, TestResults testResults, SuperPeerServerInterface superPeerServerInterface) throws RemoteException {
        Set<String> allFilesExceptSelf = FileUtil.getAllFiles(peer.getPeerId(), numberOfPeers);
        int numberOfRetrievalRequest = 0;
        long obtainStartTime = System.currentTimeMillis();
        testResults.setObtainStartTime(obtainStartTime);
        System.out.println("Peer " + peer.getPeerId() + ": Obtain start time = " + obtainStartTime);

        for (String searchFile: allFilesExceptSelf) {
            Query query = new Query(new MessageID(peer.getPeerId(), peer.getSuperPeerId(), testResults.getNumberOfForwardRequest() + numberOfRetrievalRequest++), ttl, searchFile);
            QueryHit queryHit = superPeerServerInterface.forward(query);

            if(queryHit == null || queryHit.getPeerIds() == null || queryHit.getPeerIds().size() == 0){
                System.err.println("ERROR: OOPS! None of the clients have file " + searchFile);
                continue;
            }

            Random random = new Random();
            int randomServerPeerId = random.nextInt(queryHit.getPeerIds().size());
            System.out.println("You selected the following peer " + queryHit.getPeerIds().get(randomServerPeerId));
            FileUtil.retrieveFile(peer.getPeerId(), queryHit.getPeerIds().get(randomServerPeerId), searchFile);
        }

        long obtainEndTime = System.currentTimeMillis();
        testResults.setObtainEndTime(System.currentTimeMillis());
        testResults.setNumberOfObtainRequest(numberOfRetrievalRequest);
        System.out.println("Peer " + peer.getPeerId() + ": Obtain end time = " + obtainEndTime);
    }

    public static void getForwardResults(int numberOfPeers, int ttl, Peer peer, TestResults testResults, SuperPeerServerInterface superPeerServerInterface) throws RemoteException {
        Set<String> allFilesExceptSelf = FileUtil.getAllFiles(peer.getPeerId(), numberOfPeers);
        int numberOfForwardRequest = 0;
        long forwardStartTime = System.currentTimeMillis();
        testResults.setForwardStartTime(forwardStartTime);

        System.out.println("Peer " + peer.getPeerId() + ": Forward start time = " + forwardStartTime);
        for (String searchFile: allFilesExceptSelf) {
            Query query = new Query(new MessageID(peer.getPeerId(), peer.getSuperPeerId(), testResults.getNumberOfObtainRequest() + numberOfForwardRequest++), ttl, searchFile);
            QueryHit queryHit = superPeerServerInterface.forward(query);
            if(queryHit == null || queryHit.getPeerIds() == null || queryHit.getPeerIds().size() == 0){
                System.err.println("ERROR: OOPS! None of the clients have file " + searchFile);
                continue;
            }
        }

        long forwardEndTime = System.currentTimeMillis();
        testResults.setForwardEndTime(System.currentTimeMillis());
        testResults.setNumberOfForwardRequest(numberOfForwardRequest);
        System.out.println("Peer " + peer.getPeerId() + ": Forward end time = " + forwardEndTime);
    }
}
