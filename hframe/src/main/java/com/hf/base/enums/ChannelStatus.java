package com.hf.base.enums;

public enum  ChannelStatus {
    VALID(0),
    INVALID(1),
    DELETED(99);

    private int status;

    ChannelStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
