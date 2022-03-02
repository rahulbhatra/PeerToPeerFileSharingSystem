package com.models;

import java.io.Serializable;

public class MessageID implements Serializable {
    private int peerId;
    private int superPeerId;
    private int seq;

    public MessageID(int peerId, int superPeerId, int seq) {
        this.superPeerId = superPeerId;
        this.peerId = peerId;
        this.seq = seq;
    }

    @Override
    @Deprecated
    public int hashCode() {
        final int prime = 31; // random prime
        int result = 1;
        result = prime * result + ((superPeerId == 0) ? 0 : (new Integer(superPeerId)).hashCode());
        result = prime * result + ((peerId == 0) ? 0 : (new Integer(peerId)).hashCode());
        result = prime * result + ((seq == 0) ? 0 : (new Integer(seq)).hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MessageID))
            return false;
        if (obj == this)
            return true;

        MessageID m = (MessageID) obj;
        return (
                this.superPeerId == m.superPeerId &&
                        this.peerId == m.peerId &&
                        this.seq == m.seq
        );
    }

    public String toString() {
        return "[" + superPeerId + "/" + peerId + ":" + seq + "]";
    }
}
