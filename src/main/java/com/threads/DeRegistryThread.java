package com.threads;

import com.interfaces.SuperPeerServerInterface;
import com.utility.ConstantsUtil;

import java.util.List;

public class DeRegistryThread extends Thread {

    private Integer id;
    private List<String> sharedFiles;
    private SuperPeerServerInterface superPeerServerInterface;

    public DeRegistryThread(Integer id, List<String> sharedFiles,
                            SuperPeerServerInterface superPeerServerInterface) {
        this.id = id;
        this.sharedFiles = sharedFiles;
        this.superPeerServerInterface = superPeerServerInterface;
    }

    @Override
    public void run() {
        System.out.println("De registering peer from the central indexing server.");
        try {
            synchronized (superPeerServerInterface) {
                superPeerServerInterface.deRegistry(id, sharedFiles);
            }
        } catch (Exception exception) {
            System.err.println(ConstantsUtil.DE_REGISTERING_ERROR);
            exception.printStackTrace();
        }
    }
}
