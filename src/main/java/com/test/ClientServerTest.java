package com.test;

import com.interfaces.PeerServerInterface;
import com.models.Peer;
import com.models.PeerFile;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientServerTest {
    private final static String PORT = "3000";
    private final static String CENTRAL_INDEXING_SERVER = "rmi://localhost:" + PORT + "/centralIndexingServer";
    private final static String PEER_SERVER = "rmi://localhost:" + PORT + "/peerServer";
    private final static String FILE_NOT_FOUND_ERROR = "Error: None of the peer server contains the file";
    private final static String WRONG_PEER_SELECTION_ERROR = "Error: Peer id selected does not exist";

    public static List<String> readSharedDirectory(String directory) {
        System.out.println("Reading the shared directory: " + directory);
        File folder = new File(directory);

        if (!folder.isDirectory() || !folder.exists()) {
            System.err.println("ERROR: The folder you entered \"" + directory + "\" is not a valid directory.");
            System.exit(0);
        }

        // Read all the files in the directory and return
        List<String> filenames = new ArrayList<>();

        for (File file : folder.listFiles()) {
            if (!file.isDirectory() && file.length() <= 1024 * 1024) {
                System.out.println("Sharing: " + file.getName());
                filenames.add(file.getName());
            }
        }
        return filenames;
    }


    @Test
    public static void clientServerTest() {
        String serverDirectory = "./shared/0";
        String clientDirectory = "./shared/1";
        try {
            CentralIndexingServer centralIndexingServer = new CentralIndexingServer(Integer.parseInt(PORT), CENTRAL_INDEXING_SERVER);

            // Peer Server: from here we are going to copy our files.
            List<String> serverFileNames = readSharedDirectory(serverDirectory);
            Peer peerServer = centralIndexingServer.registry("", PEER_SERVER, serverFileNames);
            PeerServerInterface peerServerInterface = new PeerServer(peerServer.getId(), PEER_SERVER, serverDirectory);

            // Peer Client:  here we are going to paste our files.
            List<String> clientFileNames = readSharedDirectory(clientDirectory);
            Peer peerClient = centralIndexingServer.registry("", PEER_SERVER, clientFileNames);
            new PeerServer(peerClient.getId(), PEER_SERVER, clientDirectory);

            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the fileName you want to download");
                String fileName = scanner.nextLine();

                if (fileName != null) {
                    fileName = fileName.trim();
                    List<String> peerServerIds = centralIndexingServer.search(fileName);
                    if (CollectionUtils.isEmpty(peerServerIds)) {
                        System.err.println(FILE_NOT_FOUND_ERROR);
                        continue;
                    }

                    System.out.println("Peer containing files:");
                    for (String peerServerId : peerServerIds) {
                        System.out.println("Peer id : " + peerServerId);
                    }
                    System.out.println("Choose one peerServer to download the file!");

                    String peerServerIdInput = scanner.nextLine();
                    if (!peerServerIds.contains(peerServerIdInput)) {
                        System.err.println(WRONG_PEER_SELECTION_ERROR);
                        continue;
                    }

                    System.out.println("Connecting to the peerServer server " + peerServerIdInput);
                    PeerServerInterface peerClientInterface = (PeerServerInterface)
                            Naming.lookup(peerServerIdInput);
                    retrieveFile(peerServerIdInput, fileName, clientDirectory, peerClientInterface);
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

    public static void retrieveFile(final String peerId, final String filename,
                                    final String clientDirectory,
                                    final PeerServerInterface peerServerInterface) {
        System.out.println("Retrieving file " + filename + " from peer " + peerId + "'. You'll be notified when it finishes.");

        try {
            Thread t_retrieve = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Peer " + peerId + ": Retrieve start time =" + System.currentTimeMillis());
                        PeerFile peerFile = peerServerInterface.retrieve(peerId, filename);
                        System.out.println("Peer " + peerId + ": Retrieve end time =" + System.currentTimeMillis());
                        File file = new File(clientDirectory, peerFile.getFileName());
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file, true);

                        if (peerFile.getData().length > 0) {
                            out.write(peerFile.getData(), 0, peerFile.getData().length);
                        } else {
                            out.write(peerFile.getData(), 0, 0);
                        }
                        out.flush();
                        out.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    System.out.println("[####################] 100% : Download '" + filename + "'' complete!");
                    return;
                }
            });
            t_retrieve.start();
        } catch (Exception exception) {
            System.err.println("Client Exception while CONNECTING to peer client: " + exception);
            exception.printStackTrace();
        }
    }
}
