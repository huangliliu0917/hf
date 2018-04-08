package com.hf.base.enums;

public enum UserChannelStatus {
    VALID(0),INVALID(1);

    private int value;

    UserChannelStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
