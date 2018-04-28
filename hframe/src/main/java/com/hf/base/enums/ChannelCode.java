package com.hf.base.enums;

public enum ChannelCode {
    WX_OPEN("01","微信公众号","01",""),
    WX_SM("02","微信主扫","02",""),
    WX_FS("03","微信反扫","03",""),
    WX_H5("04","微信H5支付","04",""),
    WX_APP("05","微信APP支付","05",""),
    ALI_OPEN("06","支付宝服务窗体","06",""),
    ALI_ZS("07","支付宝主扫","07",""),
    ALI_FS("08","支付宝反扫","08",""),
    WY("09","网银","09",""),
    QQ_H5("10","QQ H5支付","10",""),
    QQ_SM("11","QQ扫码","11",""),
    YL("12","银联支付","12",""),
    KJ("13","网银快捷支付","13",""),
    ALI_H5("14","支付宝H5","14","");

    private String code;
    private String desc;
    private String fxtCode;
    private String ysCode;

    ChannelCode(String code,String desc,String fxtCode,String ysCode) {
        this.code = code;
        this.desc = desc;
        this.fxtCode = fxtCode;
        this.ysCode = ysCode;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getFxtCode() {
        return fxtCode;
    }

    public String getYsCode() {
        return ysCode;
    }

    public static ChannelCode parseFromCode(String code) {
        for(ChannelCode channelCode:ChannelCode.values()) {
            if(channelCode.getCode().equalsIgnoreCase(code)) {
                return channelCode;
            }
        }
        return null;
    }

    public static ChannelCode parseFromFxtCode(String fxtCode) {
        for(ChannelCode channelCode:ChannelCode.values()) {
            if(channelCode.getFxtCode().equalsIgnoreCase(fxtCode)) {
                return channelCode;
            }
        }
        return null;
    }

    public static ChannelCode parseFromYsCode(String ysCode) {
        for(ChannelCode channelCode:ChannelCode.values()) {
            if(channelCode.getYsCode().equalsIgnoreCase(ysCode)) {
                return channelCode;
            }
        }
        return null;
    }
}
