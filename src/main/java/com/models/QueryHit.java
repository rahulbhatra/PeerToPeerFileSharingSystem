package com.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryHit implements Serializable {
    private MessageID messageId;
    private int timeToLive;
    private String filename;

    // Slightly different than specfication
    // because this system runs on localhost only.
    // Instead of port and IP, we use
    // this information to connect to a peer
    private List<Integer> superPeerIds;
    private List<Integer> peerIds;

    public QueryHit() {
        this.superPeerIds = new ArrayList<>();
        this.peerIds = new ArrayList<>();
    }

    public MessageID getMessageId() {
        return messageId;
    }

    public void setMessageId(MessageID messageId) {
        this.messageId = messageId;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<Integer> getSuperPeerIds() {
        return superPeerIds;
    }

    public void setSuperPeerIds(List<Integer> superPeerIds) {
        this.superPeerIds = superPeerIds;
    }

    public List<Integer> getPeerIds() {
        return peerIds;
    }

    public void setPeerIds(List<Integer> peerIds) {
        this.peerIds = peerIds;
    }
}