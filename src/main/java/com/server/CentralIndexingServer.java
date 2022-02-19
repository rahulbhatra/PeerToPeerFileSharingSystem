package com.server;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.models.Peer;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CentralIndexingServer extends UnicastRemoteObject implements CentralIndexingServerInterface {

    private Map<String, List<String>> fileNamePeerIdsMap;
    private Map<String, Peer> peerIdObjectMap;

    public CentralIndexingServer(Integer port, String centralIndexingServer) throws RemoteException {
        super();

        fileNamePeerIdsMap = new ConcurrentHashMap<>();
        peerIdObjectMap = new ConcurrentHashMap<>();

        try {
            LocateRegistry.createRegistry(port);
            Naming.bind(centralIndexingServer, this);
            Naming.lookup(centralIndexingServer);
            System.out.println("Success: Successfully Created the Registry");
        } catch (MalformedURLException | AlreadyBoundException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    protected CentralIndexingServer(int port) throws RemoteException {
        super(port);
    }

    protected CentralIndexingServer(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public synchronized Peer registry(String id, String lookUpName, List<String> fileNames) throws RemoteException {
        System.out.println("registry method invoked");

        if (id == null || "".equalsIgnoreCase(id.trim())) {
            Random random = new Random();
            Integer peerNumber = random.nextInt(10000);
            id = lookUpName + "-" + peerNumber;
            Peer peer = new Peer(id, peerNumber, fileNames);
            peerIdObjectMap.put(id, peer);
        }

        for (String fileName : fileNames) {
            if (!fileNamePeerIdsMap.containsKey(fileName)) {
                fileNamePeerIdsMap.put(fileName, new ArrayList<>(Arrays.asList(id)));
            } else if (!fileNamePeerIdsMap.get(fileName).contains(id)) {
                fileNamePeerIdsMap.get(fileName).add(id);
            }
        }
        return peerIdObjectMap.get(id);
    }

    @Override
    public synchronized List<PeerServerInterface> search(String fileName) throws RemoteException {
        List<String> peerIds = fileNamePeerIdsMap.get(fileName);
        List<PeerServerInterface> peerServerInterfaces = new ArrayList<>();
        for(String peerId: peerIds) {
            PeerServerInterface peerServerInterface = null;
            try {
                 peerServerInterface = (PeerServerInterface) Naming.lookup(peerId);
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            peerServerInterfaces.add(peerServerInterface);
        }
        return peerServerInterfaces;
    }

    @Override
    public synchronized String deRegistry(String id, List<String> fileNames) throws RemoteException {
        System.out.println("DeRegistry Started for PeerId: " + id);
        if (peerIdObjectMap.containsKey(id)) {
            Peer peer = peerIdObjectMap.get(id);
            List<String> updatedPeerFiles = new ArrayList<>(peer.getFiles());
            for (String fileName : fileNames) {
                if (updatedPeerFiles.contains(fileName)) {
                    updatedPeerFiles.remove(fileName);
                }
                List<String> peerIds = fileNamePeerIdsMap.get(fileName);
                peerIds.remove(peerIds.indexOf(id));
                System.out.println("File got deregister | FileName: " + fileName);
            }

            if (peer.getFiles().size() == 0) {
                peerIdObjectMap.remove(id); //completely remove from the system as no files are remaining
            }
        }
        return "Success";
    }
}
