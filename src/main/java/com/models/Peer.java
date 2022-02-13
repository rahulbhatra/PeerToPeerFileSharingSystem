package com.models;

import java.util.List;

public class Peer {
    private String id;
    private List<String> files;

    public Peer(String id, List<String> files) {
        this.id = id;
        this.files = files;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
