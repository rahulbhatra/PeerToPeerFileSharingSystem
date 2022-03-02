package com.models;

import java.io.Serializable;

public class Query implements Serializable {
    private MessageID messageId;
    private Integer ttl;
    private String filename;

    public Query(MessageID messageId, Integer timeToLive, String filename) {
        this.messageId = messageId;
        this.ttl = timeToLive;
        this.filename = filename;
    }

    public MessageID getMessageId() {
        return messageId;
    }

    public void setMessageId(MessageID messageId) {
        this.messageId = messageId;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
