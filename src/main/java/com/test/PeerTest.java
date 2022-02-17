
package com.test;

import com.interfaces.CentralIndexingServerInterface;
import com.models.Peer;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import com.threads.PeerTestThread;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import java.rmi.RemoteException;
import java.util.*;

public class PeerTest extends Thread {
    private static Map<Integer, TestResult> testResults;

    public static class TestResult {
        static public long retrieveStartTime;
        static public long retrieveEndTime;
    }

    public static void parallelPeerTesting() {
        String sharedDirectory = "./shared";
        int numberOfPeers = 3;

//        CentralIndexingServerThread centralIndexingServerThread = new CentralIndexingServerThread();
//        centralIndexingServerThread.start();

        System.out.println("Initializing " + numberOfPeers + " number of peers.");
        PeerTestThread[] peerTestThreads = new PeerTestThread[numberOfPeers];
        testResults = new HashMap<>();

        try {
            // Connecting all the clients with the server.
            CentralIndexingServerInterface centralIndexingServerInterface = new CentralIndexingServer(Integer.parseInt(ConstantsUtil.PORT),
                    ConstantsUtil.CENTRAL_INDEXING_SERVER);

            Peer firstPeer = null;
            for (int i = 0; i < numberOfPeers; i++) {
                String directory = sharedDirectory + "/" + i;
                List<String> sharedFiles = FileUtil.readSharedDirectory(true, directory);
                Peer peer = centralIndexingServerInterface.registry("", ConstantsUtil.PEER_SERVER, sharedFiles);
                firstPeer = firstPeer == null ? peer : firstPeer;
                peerTestThreads[i] = new PeerTestThread(directory, peer, firstPeer, sharedFiles, numberOfPeers, testResults, centralIndexingServerInterface);
                new PeerServer(peer.getId(), ConstantsUtil.PEER_SERVER, directory);
                System.out.println("Peer is is registered with central indexing server and is ready | PeerId: "
                        + peer.getId() + " | Peer Number: " + peer.getPeerNumber());

            }

            for (int i = 1; i < numberOfPeers; i++) {
                peerTestThreads[i].start();
            }

            for (int i = 1; i < numberOfPeers; i++) {
                peerTestThreads[i].join();
            }
            peerTestThreads[0].start();
            peerTestThreads[0].join();

            System.out.println("Test is ending");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}