package com.hf.core.biz.service;

import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelCode;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.ChannelStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.biz.trade.TradeBiz;
import com.hf.core.dao.local.ChannelDao;
import com.hf.core.dao.local.UserChannelDao;
import com.hf.core.model.po.Channel;
import com.hf.core.model.po.UserChannel;
import com.hf.core.model.po.UserGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TradeBizFactory {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    @Qualifier("fxtTradeBiz")
    private TradeBiz fxtTradeBiz;
    @Autowired
    @Qualifier("ysTradeBiz")
    private TradeBiz ysTradeBiz;
    @Autowired
    @Qualifier("wwTradeBiz")
    private TradeBiz wwTradeBiz;

    public TradeBiz getTradeBiz(String providerCode) {
        ChannelProvider channelProvider = ChannelProvider.parse(providerCode);
        if(null == channelProvider) {
            throw new BizFailException(String.format("no provider found,%s",providerCode));
        }
        switch (channelProvider) {
            case FXT:
                return fxtTradeBiz;
            case YS:
                return ysTradeBiz;
            case WW:
                return wwTradeBiz;
        }
        throw new BizFailException(String.format("no provider found,%s",providerCode));
    }

    public TradeBiz getTradeBiz(String mchId,String service) {
        if(StringUtils.isEmpty(mchId) || StringUtils.isEmpty(service)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"商户编号参数错误");
        }

        ChannelCode channelCode = ChannelCode.parseFromCode(service);
        if(null == channelCode) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,String.format("服务类型错误,%s",service));
        }

        UserGroup userGroup = cacheService.getGroup(mchId);
        for(ChannelProvider provider:ChannelProvider.values()) {
            Channel channel = channelDao.selectByCode(service,provider.getCode());

            if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
                continue;
            }

            UserChannel userChannel = userChannelDao.selectByGroupChannel(userGroup.getId(),channel.getId());
            if(Objects.isNull(userChannel) || userChannel.getStatus() != ChannelStatus.VALID.getStatus()) {
                continue;
            }

            if(ChannelProvider.FXT == provider) {
                return fxtTradeBiz;
            }

            if(ChannelProvider.YS == provider) {
                return ysTradeBiz;
            }

            if(ChannelProvider.WW == provider) {
                return wwTradeBiz;
            }
        }

        throw new BizFailException("用户无收单权限");
    }
}
