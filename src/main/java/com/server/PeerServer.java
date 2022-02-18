package com.server;

import com.interfaces.PeerServerInterface;
import com.models.PeerFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class PeerServer extends UnicastRemoteObject implements PeerServerInterface {

    private String id;
    private String directory;

    public PeerServer(String peerId, String peerServer, String directory) throws RemoteException {
        this.id = peerId;
        this.directory = directory;

        try {
            Naming.bind(peerId, this);
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected PeerServer() throws RemoteException {
    }

    protected PeerServer(int port) throws RemoteException {
        super(port);
    }

    protected PeerServer(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public synchronized PeerFile retrieve(String peerId, String fileName) throws RemoteException {
        System.out.println("Peer" +  peerId + " is asking to get the file info of " + fileName);
        try {
            return new PeerFile(Files.readAllBytes(Paths.get(this.directory + "/" + fileName)), fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}