package com.threads;

import com.interfaces.CentralIndexingServerInterface;
import com.logging.DirectoryWatcher;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;

import java.util.List;
import java.util.concurrent.Callable;

public class DirectoryLogsThread extends Thread {

    private DirectoryWatcher directoryLogs;
    private String directory;
    private String peerId;
    private List<String> sharedFiles;
    private CentralIndexingServerInterface centralIndexingServerInterface;

    public DirectoryLogsThread(DirectoryWatcher directoryWatcher, String directory, String peerId, List<String> sharedFiles,
                               CentralIndexingServerInterface centralIndexingServerInterface) {
        this.directoryLogs = directoryWatcher;
        this.directory = directory;
        this.peerId = peerId;
        this.sharedFiles = sharedFiles;
        this.centralIndexingServerInterface = centralIndexingServerInterface;
    }

    @Override
    public void run() {
        System.out.println("Directory logging thread is getting started for peer id: " + peerId);
        directoryLogs.beginLogging(new Callable<Void>() {
            public Void call() {
                try {
                    // If any change is detected, the peer needs to read the directory again
                    System.out.println("Change detected in the shared directory. | Shared Directory : " + directory);
                    sharedFiles = FileUtil.readSharedDirectory(false, directory);
                    centralIndexingServerInterface.registry(peerId, ConstantsUtil.PEER_SERVER, sharedFiles);
                } catch (Exception ex) {
                    System.err.println("EXCEPTION: Client Exception while RE-REGISTERING files: " + ex.toString());
                    ex.printStackTrace();
                }
                return null;
            }
        });
    }
}
