package com.hf.core.model.dto.trade.refund;

import com.hf.core.model.dto.trade.IEntity;

public class FxtRefundRequest implements IEntity {
    private String version;
    private String merchant_no;
    private String refund_fee;
    private String ori_no;
    private String ori_order_no;
    private String refund_no;
    private String out_notify_url;
    private String nonce_str;
    private String sign_type;
    private String sign;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMerchant_no() {
        return merchant_no;
    }

    public void setMerchant_no(String merchant_no) {
        this.merchant_no = merchant_no;
    }

    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getOri_no() {
        return ori_no;
    }

    public void setOri_no(String ori_no) {
        this.ori_no = ori_no;
    }

    public String getOri_order_no() {
        return ori_order_no;
    }

    public void setOri_order_no(String ori_order_no) {
        this.ori_order_no = ori_order_no;
    }

    public String getRefund_no() {
        return refund_no;
    }

    public void setRefund_no(String refund_no) {
        this.refund_no = refund_no;
    }

    public String getOut_notify_url() {
        return out_notify_url;
    }

    public void setOut_notify_url(String out_notify_url) {
        this.out_notify_url = out_notify_url;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
