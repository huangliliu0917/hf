package com.hf.base.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易信息统计返回类
 */
public class TradeStatisticsRequestDto {

    private Date createTime;    //创建时间
    private BigDecimal amoun;   //金额
    private String groupNo;     //商户编号
    private String name;        //商户名称
    private String service;     //通道
    private String serviceName; //通道名称
    private String channelProviderCode; //供应商
    private String channelProviderName; //供应商名称

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getChannelProviderName() {
        return channelProviderName;
    }

    public void setChannelProviderName(String channelProviderName) {
        this.channelProviderName = channelProviderName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getAmoun() {
        return amoun;
    }

    public void setAmoun(BigDecimal amoun) {
        this.amoun = amoun;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getChannelProviderCode() {
        return channelProviderCode;
    }

    public void setChannelProviderCode(String channelProviderCode) {
        this.channelProviderCode = channelProviderCode;
    }
}
