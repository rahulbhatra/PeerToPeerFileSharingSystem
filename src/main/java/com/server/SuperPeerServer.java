package com.server;

import com.interfaces.SuperPeerServerInterface;
import com.interfaces.LeafNodeServerInterface;
import com.models.MessageID;
import com.models.Peer;
import com.models.QueryHit;

import com.models.Query;
import com.utility.ConstantsUtil;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SuperPeerServer extends UnicastRemoteObject implements SuperPeerServerInterface {

    private Integer superPeerNumber;
    private String superPeerLookUpId;
    private Map<String, List<String>> fileNamePeerIdsMap;
    private Map<String, List<String>> peerIdFilesMap;
    private Map<MessageID, Integer> queries;
    private Set<Integer> neighbourSuperPeerIds = new HashSet<>();;

    public SuperPeerServer(Integer superPeerNumber, String centralIndexingServer, Integer bufferSize, List<Integer> neighbourSuperPeerIds) throws RemoteException {
        super();

        fileNamePeerIdsMap = new ConcurrentHashMap<>();
        peerIdFilesMap = new ConcurrentHashMap<>();

        try {
            Random random = new Random();
            this.superPeerNumber = superPeerNumber;
            this.superPeerLookUpId = centralIndexingServer + "-" + superPeerNumber;
            queries = new LinkedHashMap<MessageID, Integer>() {
                @Override
                protected boolean removeEldestEntry(final Map.Entry eldest) {
                    return size() > bufferSize;
                }
            };

            System.out.println("Binding the Central Indexing Server | Name: " + this.superPeerLookUpId);

            //might have to do this only once.
//            LocateRegistry.createRegistry(port);
            Naming.rebind(this.superPeerLookUpId, this);
            Naming.lookup(this.superPeerLookUpId);
            connectNeighbours(neighbourSuperPeerIds);
        } catch (MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }


    private void connectNeighbours(List<Integer> neighbourSuperPeerIds) {
        for (Integer neighbourSuperPeerId : neighbourSuperPeerIds) {
            this.neighbourSuperPeerIds.add(neighbourSuperPeerId);
            System.out.println("Successfully connected to neighbour Super Peer | Name: " + neighbourSuperPeerId);
        }
    }

    protected SuperPeerServer(int port) throws RemoteException {
        super(port);
    }

    protected SuperPeerServer(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public synchronized void registry(Peer peer, List<String> fileNames) throws RemoteException {
        System.out.println("registry method invoked");

        String peerLookUpId = peer.getPeerLookUpId();
        if(!peerIdFilesMap.containsKey(peer.getPeerLookUpId())) {
            peerIdFilesMap.put(peerLookUpId, fileNames);
        }

        for (String fileName : fileNames) {
            if (!fileNamePeerIdsMap.containsKey(fileName)) {
                fileNamePeerIdsMap.put(fileName, new ArrayList<>(Arrays.asList(peerLookUpId)));
            } else if (!fileNamePeerIdsMap.get(fileName).contains(peerLookUpId)) {
                fileNamePeerIdsMap.get(fileName).add(peerLookUpId);
            }
            System.out.println("File got register | FileName: " + fileName);
        }
    }

    @Override
    public synchronized List<LeafNodeServerInterface> search(String fileName) throws RemoteException {
        List<String> peerIds = fileNamePeerIdsMap.get(fileName);
        List<LeafNodeServerInterface> leafNodeServerInterfaces = new ArrayList<>();
        if(peerIds == null) {
            return leafNodeServerInterfaces;
        }
        for (String peerId : peerIds) {
            LeafNodeServerInterface leafNodeServerInterface = null;
            try {
                leafNodeServerInterface = (LeafNodeServerInterface) Naming.lookup(peerId);
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            leafNodeServerInterfaces.add(leafNodeServerInterface);
        }
        return leafNodeServerInterfaces;
    }

    @Override
    public synchronized String deRegistry(Integer id, List<String> fileNames) throws RemoteException {
        String peerId = ConstantsUtil.PORT + "-" + id;
        System.out.println("DeRegistry Started for PeerId: " + id);
        if (peerIdFilesMap.containsKey(peerId)) {
            List<String> oldFiles = peerIdFilesMap.get(peerId);
            List<String> updatedPeerFiles = new ArrayList<>(oldFiles);
            for (String fileName : fileNames) {
                if (updatedPeerFiles.contains(fileName)) {
                    updatedPeerFiles.remove(fileName);
                }
                List<String> peerIds = fileNamePeerIdsMap.get(fileName);
                peerIds.remove(peerIds.indexOf(peerId));
                System.out.println("File got deregister | FileName: " + fileName);
            }

            peerIdFilesMap.put(peerId, updatedPeerFiles);
            if (updatedPeerFiles.size() == 0) {
                peerIdFilesMap.remove(peerId); //completely remove from the system as no files are remaining
            }
        }
        return "Success";
    }

    @Override
    public void getSuperPeerLookUpId() throws RemoteException {
        System.out.println("Connected to SuperPeer | Name: " + this.superPeerLookUpId);
    }

    @Override
    public QueryHit forward(Query query) throws RemoteException {
        if (queries.containsKey(query.getMessageId())) {
            return null;
        } else {
            System.out.println("Searching for file inside SuperPeer | SuperPeerLookUpName: " + this.superPeerLookUpId);
            System.out.println("Connected Neighbour Super Peers");
            System.out.println(this.neighbourSuperPeerIds.toArray().toString());


            this.queries.put(query.getMessageId(), 0);
            QueryHit queryHit = new QueryHit();

            List<LeafNodeServerInterface> leafNodeServerInterfaces = search(query.getFilename());
            if (leafNodeServerInterfaces != null) {
                for (LeafNodeServerInterface peerServer : leafNodeServerInterfaces) {
                    queryHit.getSuperPeerIds().add(this.superPeerNumber);
                    queryHit.getPeerIds().add(peerServer.getId());
                }
            }

            if (query.getTtl() > 0) {
                for (Integer neighbourSuperPeerId : this.neighbourSuperPeerIds) {
                    try {
                        SuperPeerServerInterface neighbourSuperPeer = (SuperPeerServerInterface) Naming.lookup
                                (ConstantsUtil.SUPER_PEER_SERVER + "-" + neighbourSuperPeerId);
                        neighbourSuperPeer.getSuperPeerLookUpId();
                        System.out.println("Forwarding Query | MessageID :" + query.getMessageId() + " | FileName: " +
                                query.getFilename() + " | TimeToLive: " + query.getTtl());
                        QueryHit queryHitRes = neighbourSuperPeer.forward(query);
                        if (queryHitRes != null) {
                            queryHit.getSuperPeerIds().addAll(queryHitRes.getSuperPeerIds());
                            queryHit.getPeerIds().addAll(queryHitRes.getPeerIds());
                        }
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return queryHit;
        }
    }
}
