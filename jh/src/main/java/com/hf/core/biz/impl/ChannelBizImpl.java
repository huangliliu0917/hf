package com.hf.core.biz.impl;

import com.hf.base.enums.UserChannelStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.biz.ChannelBiz;
import com.hf.core.dao.local.ChannelDao;
import com.hf.core.dao.local.UserChannelDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.model.po.Channel;
import com.hf.core.model.po.UserChannel;
import com.hf.core.model.po.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ChannelBizImpl implements ChannelBiz {
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserChannelDao userChannelDao;

    @Override
    public List<Channel> getChanneList() {
        return null;
    }

    @Override
    public void saveUserChannel(UserChannel userChannel) {
        if(userChannel.getId()!=null) {
            UserChannel currentUserChannel = userChannelDao.selectByPrimaryKey(userChannel.getId());
            BigDecimal feeRate = userChannel.getFeeRate()==null ? currentUserChannel.getFeeRate():userChannel.getFeeRate();
            BigDecimal subFeeRate = userChannel.getSubFeeRate() == null?currentUserChannel.getSubFeeRate():userChannel.getSubFeeRate();

            if(feeRate.compareTo(subFeeRate)<0) {
                throw new BizFailException(String.format("通道费率设置错误,%s,%s,清调整费率",feeRate,subFeeRate));
            }

            UserChannel subUserChannel = userChannelDao.selectByGroupChannel(currentUserChannel.getSubGroupId(),currentUserChannel.getChannelId());
            if(Objects.isNull(subUserChannel) || subUserChannel.getStatus() == UserChannelStatus.INVALID.getValue()) {
                throw new BizFailException("上级商户没有该通道，请检查上级商户通道");
            }

            userChannelDao.updateByPrimaryKeySelective(userChannel);
        } else {
            Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
            userChannel.setCode(channel.getCode());
            userChannel.setChannelCode(channel.getChannelCode());
            userChannel.setChannelName(channel.getChannelName());
            userChannel.setStandardFeeRate(channel.getFeeRate());
            UserGroup userGroup = userGroupDao.selectByPrimaryKey(userChannel.getGroupId());
            userChannel.setGroupName(userGroup.getName());
            userChannel.setCompanyId(userGroup.getCompanyId());

            UserChannel subUserChannel = getSubUserChannel(userGroup.getSubGroupId(),channel.getId());
            if(Objects.isNull(subUserChannel)) {
                userChannel.setSubFeeRate(channel.getFeeRate());
            } else {
                userChannel.setSubFeeRate(subUserChannel.getFeeRate());
            }

            userChannel.setSubGroupId(userGroup.getSubGroupId());

            if(userChannel.getFeeRate().compareTo(userChannel.getSubFeeRate())<0) {
                throw new BizFailException(String.format("费率必须大于%s",userChannel.getSubFeeRate()));
            }

            if(userChannel.getSubFeeRate().compareTo(userChannel.getStandardFeeRate())<0) {
                throw new BizFailException(String.format("费率必须大于%s",userChannel.getStandardFeeRate()));
            }

            userChannelDao.insertSelective(userChannel);
        }
    }

    private UserChannel getSubUserChannel(Long groupId,Long channelId) {
        return userChannelDao.selectByGroupChannel(groupId,channelId);
    }
}
