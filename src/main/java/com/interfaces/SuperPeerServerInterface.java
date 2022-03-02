package com.interfaces;

import com.models.Peer;
import com.models.QueryHit;

import com.models.Query;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SuperPeerServerInterface extends Remote {
    Peer registry(Integer id, List<String> fileNames) throws RemoteException;
    List<PeerServerInterface> search(String fileName) throws RemoteException;
    String deRegistry(Integer id, List<String> fileNames) throws RemoteException;
    void getSuperPeerLookUpId() throws RemoteException;
    QueryHit forward(Query query) throws RemoteException;
}
