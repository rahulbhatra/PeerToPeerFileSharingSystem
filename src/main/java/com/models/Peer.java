package com.models;

import java.io.Serializable;
import java.util.List;

public class Peer implements Serializable {
    private String peerId;
    private Integer id;
    private Integer superPeerId;
    private List<String> files;

    public Peer(Integer peerNumber, Integer superPeerId, String peerId, List<String> files) {
        this.id = peerNumber;
        this.superPeerId = superPeerId;
        this.peerId = peerId;
        this.files = files;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSuperPeerId() {
        return superPeerId;
    }

    public void setSuperPeerId(Integer superPeerId) {
        this.superPeerId = superPeerId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
