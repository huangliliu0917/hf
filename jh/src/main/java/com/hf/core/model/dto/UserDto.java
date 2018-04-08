package com.hf.core.model.dto;

import com.hf.core.model.po.UserInfo;

/**
 * Created by tengfei on 2017/10/29.
 */
public class UserDto {
    private Long id;
    private String name;
    private String loginId;
    private String merchantNo;
    private String outletNo;
    private Integer userType;
    private String msg;

    public UserDto() {

    }

    public UserDto(UserInfo userInfo) {
        this.id = userInfo.getId();
        this.name = userInfo.getName();
//        this.merchantNo = userInfo.getMerchantNo();
//        this.outletNo = userInfo.getOutletNo();
        this.userType = userInfo.getType();
        this.loginId = userInfo.getLoginId();
        this.msg = "登录成功";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getOutletNo() {
        return outletNo;
    }

    public void setOutletNo(String outletNo) {
        this.outletNo = outletNo;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
