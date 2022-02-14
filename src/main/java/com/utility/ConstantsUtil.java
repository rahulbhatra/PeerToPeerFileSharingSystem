package com.utility;

public class ConstantsUtil {
    public final static String PORT = "3000";
    public final static String CENTRAL_INDEXING_SERVER = "rmi://localhost:" + PORT + "/centralIndexingServer";
    public final static String PEER_SERVER = "rmi://localhost:" + PORT + "/peerServer";
    public final static String FILE_NOT_FOUND_ERROR = "Error: None of the peer server contains the file. | FileName:";
    public final static String WRONG_PEER_SELECTION_ERROR = "Error: Peer id selected does not exist. | PeerId:";
    public final static String DOWNLOAD_COMPLETED = "Success: Downloading of the file has been completed. | FileName:";
    public final static String READING_SHARED_DIRECTORY = "Reading the shared directory: ";
    public final static String WRONG_DIRECTORY = "ERROR: The folder you entered is not a valid directory. | DirectoryName: ";
}
