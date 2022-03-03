package com.models;

public class TestResults {
    private long obtainStartTime;
    private long obtainEndTime;
    private long forwardStartTime;
    private long forwardEndTime;
    private int numberOfObtainRequest;
    private int numberOfForwardRequest;

    public long getObtainStartTime() {
        return obtainStartTime;
    }

    public void setObtainStartTime(long obtainStartTime) {
        this.obtainStartTime = obtainStartTime;
    }

    public long getObtainEndTime() {
        return obtainEndTime;
    }

    public void setObtainEndTime(long obtainEndTime) {
        this.obtainEndTime = obtainEndTime;
    }

    public long getForwardStartTime() {
        return forwardStartTime;
    }

    public void setForwardStartTime(long forwardStartTime) {
        this.forwardStartTime = forwardStartTime;
    }

    public long getForwardEndTime() {
        return forwardEndTime;
    }

    public void setForwardEndTime(long forwardEndTime) {
        this.forwardEndTime = forwardEndTime;
    }

    public int getNumberOfObtainRequest() {
        return numberOfObtainRequest;
    }

    public void setNumberOfObtainRequest(int numberOfObtainRequest) {
        this.numberOfObtainRequest = numberOfObtainRequest;
    }

    public int getNumberOfForwardRequest() {
        return numberOfForwardRequest;
    }

    public void setNumberOfForwardRequest(int numberOfForwardRequest) {
        this.numberOfForwardRequest = numberOfForwardRequest;
    }
}
