package com.models;

import java.io.Serializable;
import java.util.List;

public class Peer implements Serializable {
    private String peerLookUpId;
    private Integer peerId;
    private Integer superPeerId;

    public Peer(Integer peerId, Integer superPeerId, String peerLookUpId) {
        this.peerId = peerId;
        this.superPeerId = superPeerId;
        this.peerLookUpId = peerLookUpId;
    }

    public String getPeerLookUpId() {
        return peerLookUpId;
    }

    public void setPeerLookUpId(String peerLookUpId) {
        this.peerLookUpId = peerLookUpId;
    }

    public Integer getPeerId() {
        return peerId;
    }

    public Integer getSuperPeerId() {
        return superPeerId;
    }

    public void setSuperPeerId(Integer superPeerId) {
        this.superPeerId = superPeerId;
    }

    public void setPeerId(Integer peerId) {
        this.peerId = peerId;
    }
}
