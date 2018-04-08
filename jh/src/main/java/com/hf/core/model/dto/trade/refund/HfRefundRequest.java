package com.hf.core.model.dto.trade.refund;

import com.hf.base.annotations.Field;
import com.hf.core.model.dto.trade.IEntity;

public class HfRefundRequest implements IEntity {
    @Field(required = true)
    private String version;
    @Field(required = true)
    private String merchant_no;
    @Field(required = true)
    private String total;
    @Field(required = true,group = "orderNo")
    private String ori_no;
    @Field(required = true,group = "orderNo")
    private String ori_order_no;
    @Field(required = true)
    private String refund_no;
    @Field(required = true)
    private String nonce_str;
    @Field(required = true)
    private String sign_type;
    @Field(required = true)
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
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
