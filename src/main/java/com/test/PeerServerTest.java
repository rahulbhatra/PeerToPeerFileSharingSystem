package com.test;

import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import com.utility.FileUtil;
import com.utility.ConstantsUtil;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class PeerServerTest {


    public static void peerServerTest(int numberOfPeers) {

        String clientDirectory = ConstantsUtil.shared + "/" + (numberOfPeers - 1);
        try {
            CentralIndexingServer centralIndexingServer = new CentralIndexingServer(Integer.parseInt(ConstantsUtil.PORT),
                    ConstantsUtil.CENTRAL_INDEXING_SERVER);

            // Peer Server: from here we are going to copy our files.
            for(int i = 0; i < numberOfPeers - 1; i ++) {
                List<String> serverFileNames = FileUtil.readSharedDirectory(true, ConstantsUtil.shared + "/" + i);
                Peer serverPeer = centralIndexingServer.registry("", ConstantsUtil.PEER_SERVER, serverFileNames);
                PeerServerInterface peerServerInterface = new PeerServer(serverPeer.getId(), ConstantsUtil.PEER_SERVER, ConstantsUtil.shared + "/" + i);
            }


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
                    List<PeerServerInterface> peerServerInterfaces = centralIndexingServer.search(fileName);
                    if (peerServerInterfaces == null || peerServerInterfaces.size() == 0) {
                        System.err.println(ConstantsUtil.FILE_NOT_FOUND_ERROR);
                        continue;
                    }
                    DirectoryWatcher directoryWatcher = new DirectoryWatcher(clientDirectory);
                    FileUtil.startDirectoryLogging(clientPeer, clientDirectory, clientFileNames, directoryWatcher, centralIndexingServer);

                    System.out.println("Select Peer from which you want to download file.");
                    for (int i = 0; i < peerServerInterfaces.size(); i++) {
                        String peerId = peerServerInterfaces.get(i).getPeerId();
                        System.out.println((i + 1) + ". " + peerId);
                    }

                    int peerSelection = scanner.nextInt();
                    if (peerSelection < 1 || peerSelection > peerServerInterfaces.size()) {
                        System.out.println("Please select value between " + 1 + " and " + peerServerInterfaces.size());
                        continue;
                    }
                    FileUtil.retrieveFile(clientPeer, fileName, clientDirectory, peerServerInterfaces.get(peerSelection - 1));
                    break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
