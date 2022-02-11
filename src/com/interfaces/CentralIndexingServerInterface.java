package com.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CentralIndexingServerInterface extends Remote {
    public String registry(Integer id, List<String> fileNames) throws RemoteException;
    public List<Integer> search(String fileName) throws RemoteException;
    public String deRegistry(Integer id, List<String> fileNames) throws RemoteException;
}
