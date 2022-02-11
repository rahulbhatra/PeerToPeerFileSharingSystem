package com.server;

import com.interfaces.PeerServerInterface;
import com.models.PeerFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class PeerServer extends UnicastRemoteObject implements PeerServerInterface {
    protected PeerServer() throws RemoteException {
    }

    protected PeerServer(int port) throws RemoteException {
        super(port);
    }

    protected PeerServer(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public PeerFile retrieve(Integer peerId, String fileName) throws RemoteException {
        System.out.println("Peer" +  peerId + "is asking to get the file info of " + fileName);
        try {
            PeerFile peerFile = new PeerFile(Files.readAllBytes(Paths.get(fileName)), fileName);
            return peerFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}