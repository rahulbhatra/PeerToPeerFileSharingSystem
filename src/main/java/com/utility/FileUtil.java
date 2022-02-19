package com.utility;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.logging.DirectoryWatcher;
import com.models.Peer;
import com.models.PeerFile;
import com.threads.DirectoryLogsThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static void retrieveFile(
            final Peer clientPeer,
            final Peer serverPeer,
            final String filename,
            final String clientDirectory,
            final PeerServerInterface peerServerInterface) {
        System.out.println("Retrieving file " + filename + " from peer " + serverPeer.getId() + "'. You'll be notified when it finishes.");
        Thread t_retrieve = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("File retrieval started from peerId: " + serverPeer.getId() + " | Time:" + System.currentTimeMillis());
                    peerServerInterface.retrieve(serverPeer.getId(), clientDirectory, filename);
                    System.out.println("File retrieval done from peerId: " + serverPeer.getId() + " | Time:" + System.currentTimeMillis());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                System.out.println(ConstantsUtil.DOWNLOAD_COMPLETED + filename);
                return;
            }
        });
        t_retrieve.start();
    }

    public static List<String> readSharedDirectory(boolean showFileNames, String directory) {
        System.out.println(ConstantsUtil.READING_SHARED_DIRECTORY + directory);
        File folder = new File(directory);

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

    public static void startDirectoryLogging(Peer peer, String directory, List<String> sharedFiles,
                                      DirectoryWatcher directoryWatcher,
                                      CentralIndexingServerInterface centralIndexingServerInterface) {
        directoryWatcher = new DirectoryWatcher(directory);
        DirectoryLogsThread directoryLogsThread = new DirectoryLogsThread(directoryWatcher, directory, peer.getId(),
                sharedFiles,
                centralIndexingServerInterface);
        directoryLogsThread.start();
    }
    public static void createFile( int peerId, String filename, String directory, int size_in_kb  ){
        try {
            File newFile = new File(directory+"\\"+peerId+"\\"+filename);
            if (newFile.createNewFile()) {
                System.out.println("File created: " + newFile.getName());
                FileWriter myWriter = new FileWriter(newFile);
                for(int i = 0 ; i < size_in_kb*10; i++){
                    myWriter.write("Files in Java might be tricky, but it is fun enough!");
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
}
