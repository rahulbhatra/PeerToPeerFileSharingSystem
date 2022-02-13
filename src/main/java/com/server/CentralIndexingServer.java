package com.server;

import com.interfaces.CentralIndexingServerInterface;
import com.models.Peer;
import com.sun.istack.internal.NotNull;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CentralIndexingServer extends UnicastRemoteObject implements CentralIndexingServerInterface {

    private Map<String, List<String>> fileNamePeerIdsMap;
    private Map<String, Peer> peerIdObjectMap;
    private Integer peerConnections;

    public CentralIndexingServer(String centralIndexingServer) throws RemoteException {
        fileNamePeerIdsMap = new HashMap<>();
        peerIdObjectMap = new HashMap<>();
        peerConnections = 0;

        try {
            Naming.bind(centralIndexingServer, this);
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
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
    public Peer registry(String id, String lookUpName, List<String> fileNames) throws RemoteException {
        System.out.println("registry method invoked");
        if(!peerIdObjectMap.containsKey(id)) {
            peerIdObjectMap.put(id, new Peer(id, fileNames));
        }

        for(String fileName: fileNames) {
            if (!fileNamePeerIdsMap.containsKey(fileName)) {
                fileNamePeerIdsMap.put(fileName, new ArrayList<>(Arrays.asList(id)));
            } else if (!fileNamePeerIdsMap.get(fileName).contains(id)) {
                fileNamePeerIdsMap.get(fileName).add(id);
            }
        }
        return peerIdObjectMap.get(id);
    }

    @Override
    public List<String> search(String fileName) throws RemoteException {
        return fileNamePeerIdsMap.get(fileName);
    }

    @Override
    public String deRegistry(@NotNull Integer id, List<String> fileNames) throws RemoteException {
        System.out.println("deRegistry method invoked");
        if (peerIdObjectMap.containsKey(id)) {
            Peer peer = peerIdObjectMap.get(id);
            for(String fileName: fileNames) {
                peer.getFiles().remove(fileName);
                List<String> peerIds = fileNamePeerIdsMap.get(fileName);
                peerIds.remove(peerIds.indexOf(id));
            }

            if (peer.getFiles().size() == 0) {
                peerIdObjectMap.remove(id); //completely remove from the system as no files are remaining
            }
        }
        return "Success";
    }
}
