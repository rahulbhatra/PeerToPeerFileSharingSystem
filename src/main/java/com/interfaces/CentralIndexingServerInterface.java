package com.interfaces;

import com.models.Peer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CentralIndexingServerInterface extends Remote {
    Peer registry(String id, String lookUpName, List<String> fileNames) throws RemoteException;
    List<PeerServerInterface> search(String fileName) throws RemoteException;
    String deRegistry(String id, List<String> fileNames) throws RemoteException;
}
