package com.interfaces;

import com.models.PeerFile;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerServerInterface extends Remote {
    void retrieve(Integer clientId, String clientPeerDirectory, String fileName) throws RemoteException;
    String getPeerLookUpId() throws RemoteException;
    Integer getId() throws RemoteException;
    Integer getSuperPeerId() throws RemoteException;
}
