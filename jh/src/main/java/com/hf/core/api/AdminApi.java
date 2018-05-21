package com.hf.core.api;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.GroupStatus;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.enums.SettleStatus;
import com.hf.base.enums.TradeType;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.ResponseResult;
import com.hf.base.utils.TypeConverter;
import com.hf.base.utils.Utils;
import com.hf.core.biz.TrdBiz;
import com.hf.core.biz.UserBiz;
import com.hf.core.biz.service.TradeBizFactory;
import com.hf.core.biz.service.UserService;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.dao.local.*;
import com.hf.core.job.pay.PayJob;
import com.hf.core.model.po.*;
import com.hf.core.utils.CallBackCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/jhAdmin")
public class AdminApi {

    protected Logger logger = LoggerFactory.getLogger(AdminApi.class);

    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private TrdBiz trdBiz;
    @Autowired
    private PayJob payJob;
    @Autowired
    private TradeBizFactory tradeBizFactory;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private UserService userService;
    @Autowired
    private SettleTaskDao settleTaskDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;

    @RequestMapping(value = "/get_authorized_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<UserGroup>> getAuthorizedList(@RequestBody Map<String,String> params) {
        Long companyId = Long.parseLong(params.get("companyId"));
        List<UserGroup> list = userGroupDao.select(MapUtils.buildMap("status", GroupStatus.SUBMITED.getValue(),"companyId",companyId));
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/save_admin_group",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveAdminGroup(@RequestBody Map<String,Object> params) {
        try {
            UserGroup userGroup = TypeConverter.convert(params, UserGroup.class);
            if(Objects.isNull(userGroup.getSubGroupId())) {
                throw new BizFailException("sub group id is null");
            }
            UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(userGroup.getSubGroupId());
            if(Objects.isNull(subUserGroup)) {
                throw new BizFailException(String.format("sub group not exists,id:%s",userGroup.getSubGroupId()));
            }
            userBiz.saveAminGroup(userGroup);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        }
    }

    @RequestMapping(value = "/trade_status_monitor",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List> tradeStatusMonitor(@RequestBody Map<String,Object> params) {
        String groupNo = String.valueOf(params.get("groupNo"));
        String outTradeNo = String.valueOf("outTradeNo");

        return null;
    }

    @RequestMapping(value = "/orderinfo",method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> orderinfo(String outTradeNo) {
        return trdBiz.orderInfo(outTradeNo);
    }

    @RequestMapping(value = "/getCallBackCache",method = RequestMethod.GET)
    public @ResponseBody String getCallBackCache() {
        System.out.println(new Gson().toJson(CallBackCache.noticedList));
        return new Gson().toJson(CallBackCache.noticedList);
    }

    @RequestMapping(value = "/notice",method = RequestMethod.GET)
    public @ResponseBody String notice(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        TradingBiz tradeBiz = tradeBizFactory.getTradingBiz(payRequest.getChannelProviderCode());
        tradeBiz.notice(payRequest);
        return "SUCCESS";
    }

    @RequestMapping(value = "/initUserChannelAccount",method = RequestMethod.GET)
    public @ResponseBody String initUserChannelAccount() {
        List<Map<String,Object>> resultList = new ArrayList<>();
        List<UserGroupExt> userGroupExts = userGroupExtDao.selectAll();
        userGroupExts.forEach(userGroupExt -> userService.saveUserGroupExt(userGroupExt));
        List<Account> accounts = accountDao.select(MapUtils.buildMap("startIndex",0,"pageSize",Integer.MAX_VALUE));
        Map<String,BigDecimal> feeMap = new HashMap<>();

        for(Account account:accounts) {
            Map<String,Object> result = new HashMap<>();
            BigDecimal totalAmount = account.getAmount();
            UserGroup userGroup = userGroupDao.selectByPrimaryKey(account.getGroupId());
            //付款
            List<Map<String,Object>> accountInfos = accountOprLogDao.getGroupAmount(account.getGroupId(),0);
            Map<String,BigDecimal> channelAmount = new HashMap<>();
            for(Map<String,Object> map:accountInfos) {
                Long groupId = Long.parseLong(map.get("groupId").toString());
                String channelProviderCode = map.get("channelProviderCode").toString();
                BigDecimal amount = new BigDecimal(map.get("amount").toString());
                channelAmount.put(channelProviderCode,amount);
            }

            //手续费 用户
            List<Map<String,Object>> userFees = accountOprLogDao.getGroupAmount(account.getGroupId(),1);
            for(Map<String,Object> map:userFees) {
//                String channelProviderCode = map.get("channelProviderCode").toString();
                BigDecimal amount = new BigDecimal(map.get("amount").toString());
                for(String key:channelAmount.keySet()) {
                    if(amount.compareTo(BigDecimal.ZERO)<=0) {
                        break;
                    }
                    BigDecimal subAmount = channelAmount.get(key);
                    BigDecimal currentAmount = Utils.min(subAmount,amount);
                    amount = amount.subtract(currentAmount);
                    subAmount = subAmount.subtract(currentAmount);
                    channelAmount.put(key,subAmount);
                    if(feeMap.get(key) == null) {
                        feeMap.put(key,new BigDecimal("0"));
                    }
                    feeMap.put(key,feeMap.get(key).add(currentAmount));
                }
                if(amount.compareTo(BigDecimal.ZERO)>0) {
                    throw new BizFailException("amount未扣减完");
                }
            }

            //手续费 管理员
            List<Map<String,Object>> adminFees = accountOprLogDao.getGroupAmount(account.getGroupId(),6);
            BigDecimal adminFee = new BigDecimal("0");
            if(CollectionUtils.isNotEmpty(adminFees)) {
                for(Map<String,Object> map:adminFees) {
                    BigDecimal amount = new BigDecimal(map.get("amount").toString());
                    adminFee = adminFee.add(amount);
                }
                BigDecimal totalFee = feeMap.values().stream().reduce(new BigDecimal("0"),BigDecimal::add);
                if(totalFee.compareTo(adminFee)!=0) {
                    throw new BizFailException("totalFee & adminFee not equals");
                }
                for(String key:feeMap.keySet()) {
                    BigDecimal tempAmount = channelAmount.get(key).add(feeMap.get(key));
                    channelAmount.put(key,tempAmount);
                }
            }

            //提现
            List<Map<String,Object>> settles = accountOprLogDao.getGroupAmount(account.getGroupId(),4);
            for(Map<String,Object> map:settles) {
                BigDecimal amount = new BigDecimal(map.get("amount").toString());
                for(String key:channelAmount.keySet()) {
                    if(amount.compareTo(BigDecimal.ZERO)<=0) {
                        break;
                    }
                    BigDecimal subAmount = channelAmount.get(key);
                    BigDecimal tempAmount = Utils.min(subAmount,amount);
                    amount = amount.subtract(tempAmount);
                    subAmount = subAmount.subtract(tempAmount);
                    channelAmount.put(key,subAmount);
                }
                if(amount.compareTo(BigDecimal.ZERO)>0) {
                    throw new BizFailException("amount未扣减完");
                }
            }

            result.put("groupId",account.getGroupId());
            result.put("amount",account.getAmount());
            if(org.apache.commons.collections.MapUtils.isEmpty(channelAmount)) {
                BigDecimal totalChannelAmount = channelAmount.values().stream().reduce(new BigDecimal("0"),BigDecimal::add);
                result.put("channelAmount",totalChannelAmount);
            }
            result.put("channels",channelAmount);

            resultList.add(result);
        }
        return new Gson().toJson(resultList);
    }

    @RequestMapping(value = "/handleNoCallBackPayRequest",method = RequestMethod.GET)
    public @ResponseBody String handleNoCallBackPayRequest(String outTradeNos) {
        logger.info("Start handle no callback");
        String[] nos = outTradeNos.split(",");
        for(String no:nos) {
            PayRequest payRequest = payRequestDao.selectByTradeNo(no);
            if(Objects.isNull(payRequest) || payRequest.getStatus() != PayRequestStatus.PROCESSING.getValue()) {
                continue;
            }
            TradingBiz tradeBiz = tradeBizFactory.getTradingBiz(payRequest.getChannelProviderCode());
            tradeBiz.handleCallBack(payRequest);
            tradeBiz.notice(payRequest);
        }
        return "SUCCESS";
    }
 }
