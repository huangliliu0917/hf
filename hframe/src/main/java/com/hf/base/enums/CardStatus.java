package com.hf.base.enums;

public enum CardStatus {
    NOT_IN_USE(0),
    IN_USE(10),
    DELETED(99);

    private int value;

    CardStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
