package com.hf.core.model.dto.trade.unifiedorder;

import com.hf.core.model.dto.trade.IEntity;

public class YsPayResponse implements IEntity {
    private String status;
    private String appId;
    private String timeStamp;
    private String signType;
    private String _package;
    private String callback_url;
    private String nonce_Str;
    private String paySign;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String get_package() {
        return _package;
    }

    public void set_package(String _package) {
        this._package = _package;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public String getNonce_Str() {
        return nonce_Str;
    }

    public void setNonce_Str(String nonce_Str) {
        this.nonce_Str = nonce_Str;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }
}
