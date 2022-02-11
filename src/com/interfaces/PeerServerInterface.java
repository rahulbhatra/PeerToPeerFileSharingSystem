package com.interfaces;

import com.models.PeerFile;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerServerInterface extends Remote {
    PeerFile retrieve(Integer peerId, String fileName) throws RemoteException;
}
