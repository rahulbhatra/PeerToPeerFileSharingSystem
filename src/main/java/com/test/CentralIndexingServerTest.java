package com.test;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.models.Peer;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import com.sun.deploy.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class CentralIndexingServerTest {
    final static String PORT = "3000";
    final static String CENTRAL_INDEXING_SERVER = "rmi://localhost:" + PORT + "/centralIndexingServer";
    final static String PEER_SERVER = "rmi://localhost:" + PORT + "/peerServer";
    final static String FILE_NOT_FOUND_ERROR = "Error: None of the peer server contains the file";
    final static String WRONG_PEER_SELECTION_ERROR = "Error: Peer id selected does not exist";

    @Test
    public void clientIndexingServer() {
        boolean exception = false;
        try{
            CentralIndexingServerInterface centralIndexingServerInterface = new CentralIndexingServer(Integer.parseInt(PORT), CENTRAL_INDEXING_SERVER);
        }
        catch (Exception ex) {
            exception = true;
            System.err.println("EXCEPTION: CentralServer Exception while creating server: " + ex.toString());
            ex.printStackTrace();
        }
        assertEquals(false, exception);

    }

    /*@Test
    public void clientIndexingServerAll() {
        String directory = "./";
        try {
            CentralIndexingServer centralIndexingServer = new CentralIndexingServer(CENTRAL_INDEXING_SERVER);
            List<String> files = new ArrayList<>(Arrays.asList("first", "second", "third"));
            Peer peer = centralIndexingServer.registry("", PEER_SERVER, files);
            PeerServerInterface peerClientInterface = new PeerServer(peer.getId(), PEER_SERVER, directory);

            while(true) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter the fileName you want to download");
                String input = scanner.nextLine();

                if(input != null) {
                    input = input.trim();
                    List<String> peerServerIds = centralIndexingServer.search(input);
                    if(CollectionUtils.isEmpty(peerServerIds)) {
                        System.err.println(FILE_NOT_FOUND_ERROR);
                        continue;
                    }

                    System.out.println("Peer containing files:");
                    for(String peerServerId : peerServerIds) {
                        System.out.println("Peer id : " + peerServerId);
                    }
                    System.out.println("Choose one peer to download the file!");

                    String peerServerIdInput = scanner.nextLine();
                    if(!peerServerIds.contains(peerServerIdInput)) {
                        System.err.println(WRONG_PEER_SELECTION_ERROR);
                        continue;
                    }

                    System.out.println("Connecting to the peer server " + peerServerIdInput);
                    PeerServerInterface peerServerInterface = (PeerServerInterface)
                            Naming.lookup(PEER_SERVER + peerServerIdInput);
                    peerServerInterface.retrieve(peerServerIdInput, input);
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }*/
}
