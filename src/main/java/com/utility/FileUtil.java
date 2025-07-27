package com.utility;

import com.interfaces.LeafNodeServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.threads.DirectoryLogsThread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileUtil {

    public static void retrieveFile(
            final Integer peerId,
            final Integer serverPeerId,
            final String filename) throws RemoteException {

        try {
            final LeafNodeServerInterface leafNodeServerInterface = (LeafNodeServerInterface) Naming.lookup(ConstantsUtil.PEER_SERVER + "-" + serverPeerId);
            System.out.println("Retrieving file " + filename + " from peer " + serverPeerId + "'. You'll be notified when it finishes.");
            Thread t_retrieve = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        System.out.println("File retrieval started from peerId: " + serverPeerId + " | Time:" + System.currentTimeMillis());
                        leafNodeServerInterface.obtain(peerId, filename);
                        System.out.println("File retrieval done from peerId: " + serverPeerId + " | Time:" + System.currentTimeMillis());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    System.out.println(ConstantsUtil.DOWNLOAD_COMPLETED + filename);
                    return;
                }
            });
            t_retrieve.start();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readSharedDirectory(boolean showFileNames, String directory) {
        System.out.println(ConstantsUtil.READING_SHARED_DIRECTORY + directory);
        File folder = new File(directory);

        System.out.println("Current directory: " + System.getProperty("user.dir"));

        if (!folder.isDirectory() || !folder.exists()) {
            System.err.println(ConstantsUtil.WRONG_DIRECTORY + directory);
            System.exit(0);
        }

        List<String> filenames = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (!file.isDirectory() && file.length() <= 10 * 1024 * 1024) {
                if(showFileNames) {
                    System.out.println("Sharing : " + file.getName());
                }
                filenames.add(file.getName());
            }
        }
        return filenames;
    }

    public static void startDirectoryLogging(Peer peer) {
        Integer peerId = peer.getPeerId();
        Integer superPeerId = peer.getSuperPeerId();
        String directory = ConstantsUtil.shared + "/" + peerId;
        List<String> sharedFiles = FileUtil.readSharedDirectory(true, directory);
        DirectoryWatcher directoryWatcher = new DirectoryWatcher(directory);
        DirectoryLogsThread directoryLogsThread = new DirectoryLogsThread(peerId, superPeerId, directoryWatcher, directory,
                sharedFiles);
        directoryLogsThread.start();
    }

    public static void createFile(String filename, String directoryName, int size_in_kb  ){
        try {
            File directory = new File(directoryName);
            if (!directory.exists()) {
                directory.mkdir();
            }
            File newFile = new File(directoryName+ "/" +filename);
            if (newFile.createNewFile()) {
                System.out.println("File created: " + newFile.getName());
                FileWriter myWriter = new FileWriter(newFile);
                for(int i = 0 ; i < size_in_kb * 1024 / filename.length(); i++){
                    myWriter.write(filename);
                }
                myWriter.close();
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void createFiles(int numberOfPeers) {
        for(int i = 0; i < numberOfPeers; i++) {
            for(int j = 0; j < 10; j++) {
                createFile("P" + i + "-" + (j + 1) + "KB", ConstantsUtil.shared + "/" + i, j + 1);
            }
        }

        if (numberOfPeers > 8) {
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < 5; j++) {
                    createFile("P" + 0 + "-" + (j + 1) + "KB", ConstantsUtil.shared + "/" + i, j + 1);
                }
            }

            for(int i = 2; i < 4; i++) {
                for(int j = 5; j < 10; j++) {
                    createFile("P" + 0 + "-" + (j + 1) + "KB", ConstantsUtil.shared + "/" + i, j + 1);
                }
            }
        }
    }

    public static Set<String> getAllFiles(int notIncludePeerId, int numberOfPeers) {
        Set<String> totalFiles = new HashSet<>();
        for( int i = 0; i < numberOfPeers; i++) {
            if (notIncludePeerId == i) {
                continue;
            }
            List<String> directoryFiles = FileUtil.readSharedDirectory(false, ConstantsUtil.shared + "/" + i);
            totalFiles.addAll(new HashSet<>(directoryFiles));
        }
        return totalFiles;
    }
}
