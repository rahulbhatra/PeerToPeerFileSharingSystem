package com.test;

import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
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
                    List<PeerServerInterface> peerServerInterfaces = centralIndexingServer.search(fileName);
                    if (CollectionUtils.isEmpty(peerServerInterfaces)) {
                        System.err.println(ConstantsUtil.FILE_NOT_FOUND_ERROR);
                        continue;
                    }
                    DirectoryWatcher directoryWatcher = new DirectoryWatcher(clientDirectory);
                    FileUtil.startDirectoryLogging(clientPeer, clientDirectory, clientFileNames, directoryWatcher, centralIndexingServer);
                    FileUtil.retrieveFile(clientPeer, serverPeer, fileName, clientDirectory, peerServerInterfaces.get(0));
                    break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
