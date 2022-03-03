package com.test;

import com.interfaces.SuperPeerServerInterface;

import com.interfaces.LeafNodeServerInterface;
import com.models.Peer;
import com.server.LeafNodeServer;
import com.server.SuperPeerServer;
import com.utility.ConstantsUtil;
import com.utility.FileUtil;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import com.utility.JSONUtil;
import org.json.*;

public class SuperPeerServerTest {

    public static void clientIndexingServer(int bufferSize, int ttl, JSONArray superPeerToSuperPeerNeighbours, JSONArray superPeerToPeerNeighbours) {

        try {
            int numberOfSuperPeers = superPeerToSuperPeerNeighbours.length();
            for(int i = 0; i < numberOfSuperPeers; i++) {
                SuperPeerServerInterface superPeer = new SuperPeerServer
                        (i, ConstantsUtil.SUPER_PEER_SERVER, bufferSize, new ArrayList<>());
            }

            // Connect to all neighbour super peers
            for(int i = 0; i < numberOfSuperPeers; i++) {
                JSONUtil jsonUtil = new JSONUtil<Integer>();
                List<Integer> superPeerNeighbours = jsonUtil.getListFromJsonArray(superPeerToSuperPeerNeighbours.getJSONArray(i));
                SuperPeerServerInterface centralIndexingServer = new SuperPeerServer
                        (i, ConstantsUtil.SUPER_PEER_SERVER, bufferSize, superPeerNeighbours);
            }

            // Connect to all neighbour peers
            for(int i = 0; i < numberOfSuperPeers; i++) {
                JSONUtil jsonUtil = new JSONUtil<Integer>();

                SuperPeerServerInterface superPeerInterface = (SuperPeerServerInterface) Naming.lookup(ConstantsUtil.SUPER_PEER_SERVER + "-" + i);
                List<Integer> peerNeighbours = jsonUtil.getListFromJsonArray(superPeerToPeerNeighbours.getJSONArray(i));
                for(Integer id : peerNeighbours) {
                    List<String> files = FileUtil.readSharedDirectory(false, ConstantsUtil.shared + "/" + id);
                    LeafNodeServerInterface leafNodeServerInterface = new LeafNodeServer(id, i);
                    superPeerInterface.registry(leafNodeServerInterface.getPeer(), files);
                }
            }

        }
        catch (Exception ex) {
            System.err.println("EXCEPTION: CentralServer Exception while creating server: " + ex);
            ex.printStackTrace();
        }
    }
}
