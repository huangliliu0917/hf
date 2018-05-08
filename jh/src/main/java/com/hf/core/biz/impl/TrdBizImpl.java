package com.hf.core.biz.impl;

import com.hf.base.enums.*;
import com.hf.base.model.*;
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

        if(request.getStatus() != null && request.getStatus()==0) {
            params.put("notInStatusList",Arrays.asList(100,99));
            params.remove("status");
        }

        if(request.getStatus() != null && request.getStatus()==-1) {
            params.remove("status");
        }

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

    @Override
    public Map<String, BigDecimal> getOrderCal(TradeRequest request) {
        Map<String,Object> params = MapUtils.beanToMap(request);
        params.put("service",request.getChannelCode());

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(request.getGroupId());

        List<UserGroup> groups = userService.getChildMchIds(request.getGroupId());
        List<String> mchIds = groups.parallelStream().map(UserGroup::getGroupNo).collect(Collectors.toList());
        List<Long> groupIds = groups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());

        params.put("mchIds",mchIds);
        params.remove("status");

        int totalCount = payRequestDao.selectCount(params);
        BigDecimal totalAmount = payRequestDao.selectSum(params);

        params.put("status","100");
        int successCount = payRequestDao.selectCount(params);
        params.put("oprGroupIds",groupIds);
        BigDecimal successAmount = payRequestDao.selectSum(params);

        if(userGroup.getType()== UserGroup.GroupType.COMPANY.getValue() || userGroup.getType() == UserGroup.GroupType.SUPER.getValue()) {
            params.put("oprGroupIds",Arrays.asList(userGroup.getId()));
        } else {
            params.put("oprGroupIds",Arrays.asList(userGroup.getCompanyId()));
        }
        BigDecimal platAmount = payRequestDao.selectSum(params);

        return MapUtils.buildMap("totalCount",totalCount,"successCount",successCount,"successAmount",successAmount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP),"platAmount",platAmount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
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

        if(request.getAdmin() != 0) {
            List<UserGroup> groups = userService.getChildMchIds(request.getGroupId());
            List<Long> groupIds = groups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());
            params.put("groupIds",groupIds);
        }

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
                List<AccountOprLog> accountOprLogs = accountOprLogDao.selectByUnq(payRequest.getOutTradeNo(),map.get(payRequest.getMchId()).getId(), OprType.PAY.getValue());
                if(CollectionUtils.isNotEmpty(accountOprLogs)) {
                    BigDecimal totalAmount = accountOprLogs.stream().map(AccountOprLog::getAmount).reduce(new BigDecimal("0"),BigDecimal::add);
                    tradeRequestDto.setActualAmount(totalAmount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
                    tradeRequestDto.setFee((new BigDecimal(payRequest.getTotalFee()).subtract(totalAmount)).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP) );
                }
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

    @Override
    public List<UserStatistic> getUserStatistics(TradeStatisticsRequest request) {
        Map<String,Object> params = MapUtils.beanToMap(request);
        Long id = Long.parseLong(String.valueOf(params.get("groupId")));
        UserGroup userGroup = cacheService.getGroup(id);
        if(userGroup.getType() == UserGroup.GroupType.CUSTOMER.getValue()) {
            params.put("groupId",id);
        } else {
            List<UserGroup> userGroups = userService.getChildMchIds(id);
            List<Long> groupIds = userGroups.parallelStream().map(UserGroup::getId).collect(Collectors.toList());
            groupIds.remove(id);
            params.put("groupIds",groupIds);
            params.remove("groupId");
        }

        List<Map<String,Object>> results = payRequestDao.selectUserStatistics(params);

        Map<Long,UserStatistic> statisticMap = new TreeMap<>();

        results.forEach(map -> {
            BigDecimal amount = new BigDecimal(String.valueOf(map.get("amount"))).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
            Long groupId = Long.parseLong(String.valueOf(map.get("groupId")));
            String groupNo = String.valueOf(map.get("groupNo"));
            String name = String.valueOf(map.get("name"));
            String channelCode = String.valueOf(map.get("service"));
            String channelName = ChannelCode.parseFromCode(channelCode).getDesc();

            if(Objects.isNull(statisticMap.get(groupId))) {
                UserStatistic userStatistic = new UserStatistic();
                userStatistic.setGroupId(groupId);
                userStatistic.setGroupNo( groupNo);
                userStatistic.setGroupName(name);
                userStatistic.setAmount(amount);

                UserStatisticDetail detail = new UserStatisticDetail();
                detail.setChannelCode(channelCode);
                detail.setChannelName(channelName);
                detail.setAmount(amount);

                if(null == userStatistic.getList()) {
                    userStatistic.setList(new ArrayList<>());
                }
                userStatistic.getList().add(detail);
                statisticMap.put(groupId,userStatistic);
            } else {
                UserStatistic userStatistic = statisticMap.get(groupId);
                userStatistic.setAmount(userStatistic.getAmount().add(amount));

                UserStatisticDetail detail = new UserStatisticDetail();
                detail.setChannelCode(channelCode);
                detail.setChannelName(channelName);
                detail.setAmount(amount);

                userStatistic.getList().add(detail);
            }
        });

        List<UserStatistic> list = new ArrayList<>();
        statisticMap.keySet().forEach(groupId -> {
            list.add(statisticMap.get(groupId));
        });

        return list;
    }
}
