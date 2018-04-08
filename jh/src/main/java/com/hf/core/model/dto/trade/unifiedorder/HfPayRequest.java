package com.hf.core.model.dto.trade.unifiedorder;

import com.hf.base.annotations.Field;
import com.hf.core.model.dto.trade.IEntity;

public class HfPayRequest implements IEntity {
    @Field
    private String version;
    @Field(required = true)
    private String service;
    @Field(required = true)
    private String merchant_no;
    @Field
    private String outlet_no;
    @Field(required = true)
    private String total;
    @Field(required = true)
    private String name;
    @Field(required = true)
    private String remark;
    @Field(required = true)
    private String out_trade_no;
    @Field
    private String create_ip;
    @Field
    private String sub_openid;
    @Field
    private String buyer_id;
    @Field(required = true)
    private String authcode;
    @Field(required = true)
    private String nonce_str;
    @Field(required = true)
    private String sign_type;
    @Field(required = true)
    private String sign;
    @Field
    private String bank_code;
    @Field
    private String out_notify_url;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMerchant_no() {
        return merchant_no;
    }

    public void setMerchant_no(String merchant_no) {
        this.merchant_no = merchant_no;
    }

    public String getOutlet_no() {
        return outlet_no;
    }

    public void setOutlet_no(String outlet_no) {
        this.outlet_no = outlet_no;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getCreate_ip() {
        return create_ip;
    }

    public void setCreate_ip(String create_ip) {
        this.create_ip = create_ip;
    }

    public String getSub_openid() {
        return sub_openid;
    }

    public void setSub_openid(String sub_openid) {
        this.sub_openid = sub_openid;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
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

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getOut_notify_url() {
        return out_notify_url;
    }

    public void setOut_notify_url(String out_notify_url) {
        this.out_notify_url = out_notify_url;
    }
}
