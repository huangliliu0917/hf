package com.hf.base.enums;

public enum OperateType {
    USER_HF(0),
    HF_CLIENT(1),
    CLIENT_HF(2),
    HF_USER(3),
    CALLBACK_CLIENT_HF(4),
    CALLBACK_HF_USER(5);

    private int value;

    OperateType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
