package com.models;

import java.io.Serializable;
import java.util.List;

public class Peer implements Serializable {
    private String id;
    private Integer peerNumber;
    private List<String> files;

    public Peer(String id, Integer peerNumber, List<String> files) {
        this.id = id;
        this.peerNumber = peerNumber;
        this.files = files;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPeerNumber() {
        return peerNumber;
    }

    public void setPeerNumber(Integer peerNumber) {
        this.peerNumber = peerNumber;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
