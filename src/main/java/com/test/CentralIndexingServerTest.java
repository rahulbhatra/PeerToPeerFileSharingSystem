package com.test;

import com.interfaces.CentralIndexingServerInterface;
import com.interfaces.PeerServerInterface;
import com.models.Peer;
import com.server.CentralIndexingServer;
import com.server.PeerServer;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CentralIndexingServerTest {
    @Test
    public static void clientIndexingServer() {
        try{
            String peer1Directory = "./shared/0";
            String peer2Directory = "./shared/1";
            String peer3Directory = "./shared/2";

            CentralIndexingServerInterface centralIndexingServerInterface = new CentralIndexingServer(Integer.
                    parseInt(ConstantsUtil.PORT), ConstantsUtil.CENTRAL_INDEXING_SERVER);

            List<String> peer1Files = FileUtil.readSharedDirectory(true, peer1Directory);
            Peer peer1 = centralIndexingServerInterface.registry("", ConstantsUtil.PEER_SERVER, peer1Files);
            PeerServerInterface peer1ServerInterface = new PeerServer(peer1.getId(), ConstantsUtil.PEER_SERVER, peer1Directory);

            List<String> peer2Files = FileUtil.readSharedDirectory(true, peer2Directory);
            Peer peer2 = centralIndexingServerInterface.registry("", ConstantsUtil.PEER_SERVER, peer2Files);
            PeerServerInterface peer2ServerInterface = new PeerServer(peer2.getId(), ConstantsUtil.PEER_SERVER, peer2Directory);

            List<String> peer3Files = FileUtil.readSharedDirectory(true, peer3Directory);
            Peer peer3 = centralIndexingServerInterface.registry("", ConstantsUtil.PEER_SERVER, peer3Files);
            PeerServerInterface peer3ServerInterface = new PeerServer(peer3.getId(), ConstantsUtil.PEER_SERVER, peer3Directory);

            List<String> peerIds = centralIndexingServerInterface.search("peer0_small_file1");
            centralIndexingServerInterface.deRegistry(peer1.getId(), peer1Files);
            centralIndexingServerInterface.deRegistry(peer2.getId(), peer2Files);
            centralIndexingServerInterface.deRegistry(peer3.getId(), peer3Files);
        }
        catch (Exception ex) {
            System.err.println("EXCEPTION: CentralServer Exception while creating server: " + ex);
            ex.printStackTrace();
        }
    }
}
