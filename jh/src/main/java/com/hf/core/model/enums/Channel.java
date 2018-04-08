package com.hf.core.model.enums;

/**
 * Created by tengfei on 2017/11/5.
 */
public enum Channel {
    FXT(0,"富信通");

    private int value;
    private String desc;

    Channel(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static Channel parse(int value) {
        for(Channel channel:Channel.values()) {
            if(channel.value == value) {
                return channel;
            }
        }
        return null;
    }

    public String getDesc() {
        return this.desc;
    }

}
