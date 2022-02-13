package com.interfaces;

import com.models.Peer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CentralIndexingServerInterface extends Remote {
    public Peer registry(String id, String lookUpName, List<String> fileNames) throws RemoteException;
    public List<String> search(String fileName) throws RemoteException;
    public String deRegistry(Integer id, List<String> fileNames) throws RemoteException;
}
