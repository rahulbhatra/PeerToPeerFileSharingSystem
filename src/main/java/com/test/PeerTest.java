
package com.test;

import com.interfaces.CentralIndexingServerInterface;
import com.models.Peer;
import com.models.TestResults;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import com.threads.PeerTestThread;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import java.rmi.RemoteException;
import java.util.*;

public class PeerTest extends Thread {
    private static Map<Integer, TestResults> testResults;

    public static void parallelPeerTesting() {
        String sharedDirectory = "./shared";
        int numberOfPeers = 3;
        System.out.println("Initializing " + numberOfPeers + " number of peers.");
        PeerTestThread[] peerTestThreads = new PeerTestThread[numberOfPeers];
        Peer[] peers = new Peer[numberOfPeers];
        testResults = new HashMap<>();

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
                System.out.println("Peer is is registered with central indexing server and is ready | PeerId: "
                        + peer.getId() + " | Peer Number: " + peer.getPeerNumber());

            }

            for (int i = 0; i < numberOfPeers; i++) {
                peerTestThreads[i].start();
            }

            for (int i = 0; i < numberOfPeers; i++) {
                peerTestThreads[i].join();
            }

            Long averageTime = 0L;
            System.out.println(ConstantsUtil.STARTING_RESULTS);
            for (int i = 0; i < numberOfPeers; i++) {
                Peer peer = peers[i];
                averageTime = averageTime + (testResults.get(peer.getPeerNumber()).getRetrieveEndTime() - testResults.get(peer.getPeerNumber()).getRetrieveStartTime());
                System.out.println("Peer " + i + ": start = " + testResults.get(peer.getPeerNumber()).getRetrieveStartTime() +
                        ", end = " + testResults.get(peer.getPeerNumber()).getRetrieveEndTime());
            }
            averageTime = averageTime / (500 * numberOfPeers);
            System.out.println("Average time for one peer = " + averageTime);
            System.out.println("File size = 10K");
            System.out.println(ConstantsUtil.Ending_RESULTS);
            System.out.println("Test is ending");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}