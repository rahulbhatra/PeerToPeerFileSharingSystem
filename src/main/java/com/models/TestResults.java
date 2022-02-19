package com.models;

public class TestResults {
    private long retrieveStartTime;
    private long retrieveEndTime;
    private long searchStartTime;
    private long searchEndTime;

    public long getRetrieveStartTime() {
        return retrieveStartTime;
    }

    public void setRetrieveStartTime(long retrieveStartTime) {
        this.retrieveStartTime = retrieveStartTime;
    }

    public long getRetrieveEndTime() {
        return retrieveEndTime;
    }

    public void setRetrieveEndTime(long retrieveEndTime) {
        this.retrieveEndTime = retrieveEndTime;
    }

    public long getSearchStartTime() {
        return searchStartTime;
    }

    public void setSearchStartTime(long searchStartTime) {
        this.searchStartTime = searchStartTime;
    }

    public long getSearchEndTime() {
        return searchEndTime;
    }

    public void setSearchEndTime(long searchEndTime) {
        this.searchEndTime = searchEndTime;
    }
}
