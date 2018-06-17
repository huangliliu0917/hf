package com.hf.base.enums;

import org.apache.commons.lang3.StringUtils;

public enum  ChannelProvider {
    YS("ys","友收宝"),
    FXT("fxt","富信通"),
    WW("ww","微微"),
    HFB("hfb","合付宝"),
    ZF("zf","中付"),
    WHP("whp","微互宝"),
    ZFB("zfb","中付宝");

    private String code;
    private String name;

    ChannelProvider(String code,String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ChannelProvider parse(String code) {
        for(ChannelProvider channelProvider:ChannelProvider.values()) {
            if(StringUtils.equalsIgnoreCase(code,channelProvider.getCode())) {
                return channelProvider;
            }
        }
        return null;
    }
}
