package com.hf.core.biz.impl;

import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.OprType;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.enums.TradeType;
import com.hf.base.model.TradeRequest;
import com.hf.base.model.TradeRequestDto;
import com.hf.base.model.TradeStatisticsRequest;
import com.hf.base.model.TradeStatisticsRequestDto;
import com.hf.base.utils.EpaySignUtil;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Pagenation;
import com.hf.base.utils.Utils;
import com.hf.core.biz.TrdBiz;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.UserService;
import com.hf.core.dao.local.AccountOprLogDao;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.po.AccountOprLog;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserGroupExt;
import com.hf.core.utils.CipherUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TrdBizImpl implements TrdBiz {
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    @Qualifier("wwClient")
    private PayClient wwClient;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;

    @Override
    public Pagenation<TradeRequestDto> getTradeList(TradeRequest request) {
        Map<String,Object> params = MapUtils.beanToMap(request);
        Integer startIndex = (request.getCurrentPage()-1)*request.getPageSize();
        params.put("startIndex",startIndex);
        params.put("pageSize",request.getPageSize());
        params.put("service",request.getChannelCode());

        List<UserGroup> groups = userService.getChildMchIds(request.getGroupId());
        List<String> mchIds = groups.parallelStream().map(UserGroup::getGroupNo).collect(Collectors.toList());

        params.put("mchIds",mchIds);

        List<PayRequest> list = payRequestDao.select(params);

        if(CollectionUtils.isEmpty(list)) {
            return new Pagenation(new ArrayList(),list.size(),request.getCurrentPage(),request.getPageSize());
        }

        List<UserGroup> userGroups = userGroupDao.selectByGroupNos(list.stream().map(PayRequest::getMchId).collect(Collectors.toSet()));
        Map<String,UserGroup> mchMap = userGroups.parallelStream().collect(Collectors.toMap(UserGroup::getGroupNo,Function.identity()));

        List<TradeRequestDto> result = new ArrayList<>();
        list.forEach(payRequest -> result.add(buildPayRequest(payRequest,mchMap)));

        Integer totalSize = payRequestDao.selectCount(params);

        return new Pagenation<>(result,totalSize,request.getCurrentPage(),request.getPageSize());
    }

    /**
     * 获取交易统计数据
     * @param request
     * @return
     */
    @Override
    public Pagenation<TradeStatisticsRequestDto> getTradeStatisticsList(TradeStatisticsRequest request) {

        //分页信息
        Map<String,Object> params = MapUtils.beanToMap(request);
        Integer startIndex = (request.getCurrentPage()-1)*request.getPageSize();
        params.put("startIndex",startIndex);
        params.put("pageSize",request.getPageSize());

        //查询数据
        Integer totalSize = payRequestDao.selectStatisticsCount(params);                //查询条数
        List<TradeStatisticsRequestDto> list = payRequestDao.selectStatistics(params);  //查询结果集

        if(CollectionUtils.isEmpty(list)) {
            return new Pagenation(new ArrayList(),list.size(),request.getCurrentPage(),request.getPageSize());
        }

        return new Pagenation<>(list,totalSize,request.getCurrentPage(),request.getPageSize());
    }

    private TradeRequestDto buildPayRequest(PayRequest payRequest,Map<String,UserGroup> map) {
        TradeRequestDto tradeRequestDto = new TradeRequestDto();
        tradeRequestDto.setCreateTime(payRequest.getCreateTime());
        tradeRequestDto.setGroupName(null==map.get(payRequest.getMchId())?"":map.get(payRequest.getMchId()).getName());
        tradeRequestDto.setId(payRequest.getId());
        tradeRequestDto.setMchId(payRequest.getMchId());
        tradeRequestDto.setOutTradeNo(payRequest.getOutTradeNo());
        tradeRequestDto.setType(payRequest.getTradeType());

        tradeRequestDto.setActualAmount(payRequest.getActualAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        tradeRequestDto.setFee(payRequest.getFee().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        if(payRequest.getActualAmount().compareTo(BigDecimal.ZERO)<=0) {
            if(null!=map.get(payRequest.getMchId())) {
                AccountOprLog accountOprLog = accountOprLogDao.selectByUnq(payRequest.getOutTradeNo(),map.get(payRequest.getMchId()).getId(), OprType.PAY.getValue());
                tradeRequestDto.setActualAmount(accountOprLog.getAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
                tradeRequestDto.setFee((new BigDecimal(payRequest.getTotalFee()).subtract(accountOprLog.getAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP) );
            }
        }

        tradeRequestDto.setAmoun(new BigDecimal(payRequest.getTotalFee()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        tradeRequestDto.setSuccessTime(payRequest.getUpdateTime());
        tradeRequestDto.setTypeDesc(TradeType.parse(payRequest.getTradeType()).getDesc());
        tradeRequestDto.setService(payRequest.getService());
        tradeRequestDto.setStatus(payRequest.getStatus());
        tradeRequestDto.setStatusDesc(PayRequestStatus.parse(payRequest.getStatus()).getDesc());
        return tradeRequestDto;
    }

    @Override
    public Map<String,Object> orderInfo(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        if(Objects.isNull(payRequest)) {
            return MapUtils.buildMap("msg","未生成交易单");
        }
        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(), ChannelProvider.WW.getCode());
        Map<String,Object> params = new HashMap<>();
        params.put("memberCode",userGroupExt.getMerchantNo());
        params.put("orderNum",payRequest.getOutTradeNo());
        String signUrl = Utils.getEncryptStr(params);
        String signStr = EpaySignUtil.sign(CipherUtils.private_key,signUrl);
        params.put("signStr",signStr);
        Map<String,Object> result = wwClient.orderinfo(params);
        result.put("hfInfo",payRequest);

        return MapUtils.buildMap("msg",result);
    }
}
