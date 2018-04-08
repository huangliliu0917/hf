package com.hf.core.model.po;

import com.hf.base.annotations.Field;

import java.util.Date;

public class UserInfo {
    @Field(required = true,alias = "userId",type = Field.Type.number)
    private Long id;
    private String loginId;
    private String password;
    @Field(required = true)
    private String name;
    @Field
    private String tel;
    @Field(required = true)
    private String qq;
    @Field(required = true)
    private String address;
    private Integer type;
    private Integer status;
    private Date createTime;
    private String inviteCode;
    private Long groupId;
    @Field(required = true)
    private String idCard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public enum STATUS {
        init(0,"用户完善资料"),
        processing(1,"待认证"),
        available(2,"正常"),
        failed(99,"禁用");

        private int value;
        private String desc;

        STATUS(int value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static STATUS parse(int value) {
            for(STATUS status: STATUS.values()) {
                if(value == status.value) {
                    return status;
                }
            }
            return null;
        }

        public String getDesc() {
            return this.desc;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum TYPE {
        ADMIN(1,"管理员"),
        CUSTOMER(3,"普通用户"),
        SUPER_ADMIN(10,"超级管理员");

        private int value;
        private String desc;

        TYPE(int value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static TYPE parse(int value) {
            for(TYPE type: TYPE.values()) {
                if(type.value == value) {
                    return type;
                }
            }
            return null;
        }

        public String getDesc() {
            return this.desc;
        }

        public int getValue() {
            return this.value;
        }
    }
}