package com.hf.base.model;

public class UserGroupExt {
    private Long id;

    private Long groupId;

    private String providerCode;

    private String providerName;

    private String merchantNo;

    private String outletNo;

    private String cipherCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode == null ? null : providerCode.trim();
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public String getOutletNo() {
        return outletNo;
    }

    public void setOutletNo(String outletNo) {
        this.outletNo = outletNo == null ? null : outletNo.trim();
    }

    public String getCipherCode() {
        return cipherCode;
    }

    public void setCipherCode(String cipherCode) {
        this.cipherCode = cipherCode == null ? null : cipherCode.trim();
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}