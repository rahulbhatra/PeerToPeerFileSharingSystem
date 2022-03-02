package com.server;

import com.interfaces.PeerServerInterface;
import com.models.Peer;
import com.models.PeerFile;
import com.utility.ConstantsUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class PeerServer extends UnicastRemoteObject implements PeerServerInterface {

    private Integer id;
    private Integer superPeerId;
    private String directory;
    private String peerLookUpId;

    public PeerServer(Integer id, Integer superPeerId) throws RemoteException {
        this.id = id;
        this.superPeerId = superPeerId;
        this.directory = ConstantsUtil.shared + "/" + id;
        this.peerLookUpId = ConstantsUtil.PEER_SERVER + "-" + id;

        try {

            Naming.rebind(this.peerLookUpId, this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPeerLookUpId() throws RemoteException {
        return this.peerLookUpId;
    }

    @Override
    public Integer getId() throws RemoteException {
        return this.id;
    }

    @Override
    public Integer getSuperPeerId() throws RemoteException {
        return this.superPeerId;
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
    public synchronized void retrieve(Integer id, String clientPeerDirectory, String fileName) throws RemoteException {
        String clientPeerId = ConstantsUtil.PEER_SERVER + "-" + id;
        System.out.println(Peer.class.getName() + " " + clientPeerId + " is asking to get the file info of " + fileName);
        try {
            PeerFile peerFile =  new PeerFile(Files.readAllBytes(Paths.get(this.directory + "/" + fileName)), fileName);
            File file = new File(clientPeerDirectory, peerFile.getFileName());
            if(!file.exists()) {
                System.out.println("Creating a New File | Directory: " + clientPeerDirectory + " | FileName : " + fileName);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file, true);

                if (peerFile.getData().length > 0) {
                    out.write(peerFile.getData(), 0, peerFile.getData().length);
                } else {
                    out.write(peerFile.getData(), 0, 0);
                }
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}