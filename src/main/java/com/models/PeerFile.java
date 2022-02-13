package com.models;

import java.io.Serializable;

public class PeerFile implements Serializable {
    private byte[] data;
    private String fileName;

    public PeerFile(byte[] data, String fileName) {
        this.data = data;
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
