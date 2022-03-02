package com.threads;
import com.interfaces.SuperPeerServerInterface;
import com.logging.DirectoryWatcher;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;

import java.rmi.Naming;
import java.util.List;
import java.util.concurrent.Callable;

public class DirectoryLogsThread extends Thread {

    private DirectoryWatcher directoryLogs;
    private String directory;
    private Integer peerId;
    private Integer superPeerId;
    private List<String> sharedFiles;

    public DirectoryLogsThread(Integer peerId, Integer superPeerId, DirectoryWatcher directoryWatcher, String directory, List<String> sharedFiles) {
        this.peerId = peerId;
        this.superPeerId = superPeerId;
        this.directoryLogs = directoryWatcher;
        this.directory = directory;
        this.sharedFiles = sharedFiles;
    }

    @Override
    public void run() {
        System.out.println("Directory logging thread is getting started for peer id: " + peerId);
        directoryLogs.beginWatchingChanges(new Callable<Void>() {
            public Void call() {
                try {
                    // If any change is detected, the peer needs to read the directory again
                    System.out.println("Change detected in the shared directory. | Shared Directory : " + directory);
                    sharedFiles = FileUtil.readSharedDirectory(false, directory);
                    SuperPeerServerInterface superPeerServerInterface = (SuperPeerServerInterface) Naming.lookup(ConstantsUtil.CENTRAL_INDEXING_SERVER + "-" + superPeerId);
                    superPeerServerInterface.registry(peerId, sharedFiles);
                } catch (Exception ex) {
                    System.err.println("EXCEPTION: Client Exception while RE-REGISTERING files: " + ex.toString());
                    ex.printStackTrace();
                }
                return null;
            }
        });
    }
}
