package com.test;

import com.interfaces.SuperPeerServerInterface;
import com.interfaces.PeerServerInterface;
import com.models.MessageID;
import com.models.Query;
import com.models.QueryHit;
import com.utility.FileUtil;
import com.utility.ConstantsUtil;
import org.json.JSONArray;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class PeerServerTest {


    public static void peerServerTest(int numberOfPeers, int bufferSize, int ttl, JSONArray superPeerToSuperPeerNeighbours, JSONArray superPeerToPeerNeighbours) {

        try {
            // Need to do the initial setup of the connection.
            CentralIndexingServerTest.clientIndexingServer(bufferSize, ttl, superPeerToSuperPeerNeighbours, superPeerToPeerNeighbours);
            while (true) {
                Scanner scanner = new Scanner(System.in);
                int peerId;
                do {
                    System.out.println("Enter your peerId between 0 and " + (numberOfPeers - 1));
                    peerId = scanner.nextInt();
                } while(peerId < 0 && peerId >= numberOfPeers);

                System.out.println("You have the following files present in your system.");
                FileUtil.readSharedDirectory(true, ConstantsUtil.shared + "/" + peerId);

                System.out.println("All files that you can download are as following: ");
                Set<String> allFilesToDownload = FileUtil.getAllFiles(peerId, numberOfPeers);
                List<String> files = new ArrayList<>(allFilesToDownload);
                for(int i = 0; i < files.size(); i++) {
                    System.out.println((i + 1) + " " + files.get(i));
                }

                int fileSelected;
                do {
                    System.out.println("Enter the file name you want to download between 1 and " + files.size());
                    fileSelected = scanner.nextInt();
                } while (fileSelected < 1 && fileSelected > files.size());

                String fileName = files.get(fileSelected - 1);
                System.out.println("You selected the following file | FileName: " + fileName);

                SuperPeerServerInterface serverInterface = (SuperPeerServerInterface) Naming.lookup
                        (ConstantsUtil.CENTRAL_INDEXING_SERVER + "-" + 0);

                if (fileName != null) {
                    String clientDirectory = ConstantsUtil.shared + "/" + peerId;
                    PeerServerInterface peerServer = (PeerServerInterface) Naming.lookup(ConstantsUtil.PEER_SERVER + "-" + peerId);
                    FileUtil.startDirectoryLogging(peerId, peerServer.getSuperPeerId());

                    fileName = fileName.trim();
                    Query query = new Query(new MessageID(peerId, peerServer.getSuperPeerId(), 1), ttl, fileName);
                    QueryHit queryHit = serverInterface.forward(query);

                    if(queryHit.getPeerIds() == null || queryHit.getPeerIds().size() == 0){
                        System.err.println("ERROR: OOPS! None of the clients have file " + fileName);
                        continue;
                    }


                    System.out.println("Showing all peers available to download files.");
                    for (int i = 0; i < queryHit.getPeerIds().size(); i++) {
                        Integer serverPeerId = queryHit.getPeerIds().get(i);
                        System.out.println((i + 1) + ". " + (ConstantsUtil.PEER_SERVER + "-" + serverPeerId));
                    }

                    int serverPeerSelected;
                    do {
                        System.out.println("Select Peer from which you want to download file.");
                        serverPeerSelected = scanner.nextInt();
                    } while (serverPeerSelected < 1 && serverPeerSelected > queryHit.getPeerIds().size());


                    System.out.println("You selected the following peer " + queryHit.getPeerIds().get(serverPeerSelected - 1));
                    FileUtil.retrieveFile(peerId, queryHit.getPeerIds().get(serverPeerSelected - 1), fileName, clientDirectory);
                    break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
