package com.hf.core.model.po;

import com.hf.base.annotations.Field;

import java.util.Date;

public class UserGroup {
    @Field(alias = "groupId")
    private Long id;
    @Field(required = true)
    private String name;
    @Field(required = true)
    private String idCard;
    @Field(required = true)
    private String tel;
    @Field(required = true)
    private String address;
    @Field(required = true,type = Field.Type.number)
    private Integer type;

    private Integer status;
    @Field
    private String groupNo;
    @Field(type = Field.Type.number)
    private Long subGroupId;

    private String subGroupNo;

    private String subGroupName;

    private Long companyId;

    private Date createTime;
    @Field(required = true)
    private String ownerName;
    @Field
    private String callbackUrl;
    @Field
    private String cipherCode;

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
        this.name = name == null ? null : name.trim();
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
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

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo == null ? null : groupNo.trim();
    }

    public Long getSubGroupId() {
        return subGroupId;
    }

    public void setSubGroupId(Long subGroupId) {
        this.subGroupId = subGroupId;
    }

    public String getSubGroupNo() {
        return subGroupNo;
    }

    public void setSubGroupNo(String subGroupNo) {
        this.subGroupNo = subGroupNo;
    }

    public String getSubGroupName() {
        return subGroupName;
    }

    public void setSubGroupName(String subGroupName) {
        this.subGroupName = subGroupName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCipherCode() {
        return cipherCode;
    }

    public void setCipherCode(String cipherCode) {
        this.cipherCode = cipherCode;
    }

    public enum Status {
        NEW(1,"未认证"),
        VALID(2,"已认证"),
        INVALID(3,"禁用");

        private int value;
        private String desc;

        Status(int value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static Status parse(int value) {
            for(Status status: Status.values()) {
                if(status.value == value) {
                    return status;
                }
            }
            return null;
        }

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum GroupType {
        CUSTOMER(1,"普通商户"),
        AGENT(2,"代理商"),
        COMPANY(3,"分公司"),
        SUPER(10,"总部");

        private int value;
        private String desc;

        GroupType(int value,String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int getValue() {
            return this.value;
        }

        public static GroupType parse(int value) {
            for (GroupType groupType : GroupType.values()) {
                if (groupType.value == value) {
                    return groupType;
                }
            }
            return null;
        }

        public String getDesc() {
            return desc;
        }
    }
}