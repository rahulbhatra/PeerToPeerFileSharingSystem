package com.models;

import java.util.List;

public class Peer {
    private Integer id;
    private List<String> files;

    public Peer(Integer id, List<String> files) {
        this.id = id;
        this.files = files;
    }

    public Integer getId() {
        return id;
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
