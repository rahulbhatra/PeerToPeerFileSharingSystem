package com.interfaces;

import com.models.Peer;
import com.models.PeerFile;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LeafNodeServerInterface extends Remote {
    void obtain(Integer clientPeerId, String fileName) throws RemoteException;
    String getPeerLookUpId() throws RemoteException;
    Integer getId() throws RemoteException;
    Integer getSuperPeerId() throws RemoteException;
    Peer getPeer() throws RemoteException;
}
