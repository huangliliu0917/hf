package com.hf.base.enums;

public enum WithDrawRole {
    DRAWER(0), PAYER(1);

    private int value;

    WithDrawRole(int value) {
        this.value = value;
    }

    public static WithDrawRole parse(Integer value) {
        for(WithDrawRole withDrawRole:WithDrawRole.values()) {
            if(withDrawRole.value == value) {
                return withDrawRole;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
