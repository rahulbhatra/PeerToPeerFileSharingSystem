package com.threads;

import com.interfaces.CentralIndexingServerInterface;
import com.utility.ConstantsUtil;

import java.util.List;

public class DeRegistryThread extends Thread {

    private String peerId;
    private List<String> sharedFiles;
    private CentralIndexingServerInterface centralIndexingServerInterface;

    public DeRegistryThread(String peerId, List<String> sharedFiles,
                            CentralIndexingServerInterface centralIndexingServerInterface) {
        this.peerId = peerId;
        this.sharedFiles = sharedFiles;
        this.centralIndexingServerInterface = centralIndexingServerInterface;
    }

    @Override
    public void run() {
        System.out.println("De registering peer from the central indexing server.");
        try {
            synchronized (centralIndexingServerInterface) {
                centralIndexingServerInterface.deRegistry(peerId, sharedFiles);
            }
        } catch (Exception exception) {
            System.err.println(ConstantsUtil.DE_REGISTERING_ERROR);
            exception.printStackTrace();
        }
    }
}
