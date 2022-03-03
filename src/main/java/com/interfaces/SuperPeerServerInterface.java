package com.interfaces;

import com.models.Peer;
import com.models.QueryHit;

import com.models.Query;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SuperPeerServerInterface extends Remote {
    void registry(Peer peer, List<String> fileNames) throws RemoteException;
    List<LeafNodeServerInterface> search(String fileName) throws RemoteException;
    String deRegistry(Integer id, List<String> fileNames) throws RemoteException;
    void getSuperPeerLookUpId() throws RemoteException;
    QueryHit forward(Query query) throws RemoteException;
}
