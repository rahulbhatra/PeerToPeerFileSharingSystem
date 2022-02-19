package com.interfaces;

import com.models.PeerFile;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerServerInterface extends Remote {
    void retrieve(String clientPeerId, String clientPeerDirectory, String fileName) throws RemoteException;
}
