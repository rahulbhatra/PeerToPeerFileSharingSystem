package com.test;

import com.interfaces.PeerServerInterface;
import com.models.Peer;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import com.utility.FileUtil;
import com.utility.ConstantsUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class PeerServerTest {


    public static void peerServerTest() {
        String serverDirectory = ConstantsUtil.PEER1_DIRECTORY;
        String clientDirectory = ConstantsUtil.PEER2_DIRECTORY;
        try {
            CentralIndexingServer centralIndexingServer = new CentralIndexingServer(Integer.parseInt(ConstantsUtil.PORT),
                    ConstantsUtil.CENTRAL_INDEXING_SERVER);

            // Peer Server: from here we are going to copy our files.
            List<String> serverFileNames = FileUtil.readSharedDirectory(true, serverDirectory);
            Peer serverPeer = centralIndexingServer.registry("", ConstantsUtil.PEER_SERVER, serverFileNames);
            PeerServerInterface peerServerInterface = new PeerServer(serverPeer.getId(), ConstantsUtil.PEER_SERVER, serverDirectory);

            // Peer Client:  here we are going to paste our files.
            List<String> clientFileNames = FileUtil.readSharedDirectory(false, clientDirectory);
            Peer clientPeer = centralIndexingServer.registry("", ConstantsUtil.PEER_SERVER, clientFileNames);
            new PeerServer(clientPeer.getId(), ConstantsUtil.PEER_SERVER, clientDirectory);

            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the fileName you want to download");
                String fileName = scanner.nextLine();

                if (fileName != null) {
                    fileName = fileName.trim();
                    List<String> peerServerIds = centralIndexingServer.search(fileName);
                    if (CollectionUtils.isEmpty(peerServerIds)) {
                        System.err.println(ConstantsUtil.FILE_NOT_FOUND_ERROR);
                        continue;
                    }

                    System.out.println("Peer containing files:");
                    for (String peerServerId : peerServerIds) {
                        System.out.println("Peer id : " + peerServerId);
                    }
                    System.out.println("Choose one peerServer to download the file!");

                    String peerServerIdInput = scanner.nextLine();
                    if (!peerServerIds.contains(peerServerIdInput)) {
                        System.err.println(ConstantsUtil.WRONG_PEER_SELECTION_ERROR);
                        continue;
                    }
                    System.out.println("Connecting to the peerServer server " + peerServerIdInput);
                    PeerServerInterface chosenPeerServerInterface = (PeerServerInterface)
                            Naming.lookup(peerServerIdInput);
                    FileUtil.retrieveFile(clientPeer, serverPeer, fileName, clientDirectory, chosenPeerServerInterface);
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
