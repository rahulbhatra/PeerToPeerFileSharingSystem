package com.utility;

public class ConstantsUtil {
    public final static String PORT = "3000";
    public final static String CENTRAL_INDEXING_SERVER = "rmi://localhost:" + PORT + "/centralIndexingServer";
    public final static String PEER_SERVER = "rmi://localhost:" + PORT + "/peerServer";
    public final static String FILE_NOT_FOUND_ERROR = "Error: None of the peer server contains the file. | FileName:";
    public final static String WRONG_PEER_SELECTION_ERROR = "Error: Peer id selected does not exist. | PeerId:";
    public final static String DOWNLOAD_COMPLETED = "Success: File download has been completed. | FileName:";
    public final static String READING_SHARED_DIRECTORY = "Reading the shared directory: ";
    public final static String WRONG_DIRECTORY = "ERROR: Directory entered is invalid. | DirectoryName: ";
    public final static String DE_REGISTERING_ERROR = "During De Registering got Exception";
    public final static String FILE_NAME_IN_PARALLEL_SEARCH = "peer0_small_file1";
    public final static String shared = "./shared";
    public final static String peer1Directory = "./shared/0";
    public final static String peer2Directory = "./shared/1";
    public final static String peer3Directory = "./shared/2";
    public final static String STARTING_RESULTS = "******************** Starting to Get Results ************************";
    public final static String Ending_RESULTS = "*********************** Ending to Get Results ************************";
}
