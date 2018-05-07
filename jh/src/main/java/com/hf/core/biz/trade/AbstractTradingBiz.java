package com.hf.core.biz.trade;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.*;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.*;
import com.hf.core.dao.remote.CallBackClient;
import com.hf.core.model.PropertyConfig;
import com.hf.core.model.po.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractTradingBiz implements TradingBiz {

    private String[] needFields = {"service","merchant_no","total","out_trade_no","nonce_str","sign"};
    @Autowired
    protected CacheService cacheService;
    @Autowired
    protected UserGroupExtDao userGroupExtDao;
    @Autowired
    protected PayRequestDao payRequestDao;
    @Autowired
    protected UserChannelDao userChannelDao;
    @Autowired
    protected ChannelDao channelDao;
    @Autowired
    protected PayRequestBackDao payRequestBackDao;
    @Autowired
    protected PayService payService;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private CallBackClient callBackClient;
    @Autowired
    private PropertyConfig propertyConfig;

    protected Logger logger = LoggerFactory.getLogger(AbstractTradingBiz.class);

    public abstract ChannelProvider getChannelProvider();
    public abstract void doPay(PayRequest payRequest,HttpServletRequest request,HttpServletResponse response);

    public Map<String,Object> pay(Map<String,Object> requestMap) {
        return this.pay(requestMap,null,null);
    }

    public Map<String,Object> pay(Map<String,Object> requestMap, HttpServletRequest request,HttpServletResponse response) {
        for(String needField:needFields) {
            if(Objects.isNull(requestMap.get(needField)) || Utils.isEmpty(String.valueOf(requestMap.get(needField)))) {
                throw new BizFailException(String.format("%s不能为空",needField));
            }
        }

        String merchant_no = Utils.nvl(requestMap.get("merchant_no"));
        String out_trade_no = Utils.nvl(requestMap.get("out_trade_no"));
        String service = Utils.nvl(requestMap.get("service"));
        String tradeNo = merchant_no+"_"+out_trade_no;

        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);

        if(!Objects.isNull(payRequest)) {
            if(payRequest.getStatus() == PayRequestStatus.NEW.getValue()) {
                payService.saveOprLog(payRequest);
            }
            return finishRemotePay(payRequest);
        }

        UserGroup userGroup = cacheService.getGroup(merchant_no);
        if(Objects.isNull(userGroup) || userGroup.getStatus() != GroupStatus.AVAILABLE.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"userGroup没有权限");
        }

        if(!Utils.checkEncrypt(requestMap,userGroup.getCipherCode())) {
            throw new BizFailException(CodeManager.CHECK_ENCRIPT_FAILED,"验签失败");
        }

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),service, getChannelProvider().getCode());
        if(Objects.isNull(userChannel) || userChannel.getStatus() != UserChannelStatus.VALID.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"userChannel没有权限");
        }

        Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
        if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"channel没有权限");
        }

        String total = Utils.nvl(requestMap.get("total"));

        if(channel.getMinPrice().compareTo(BigDecimal.ZERO)>0) {
            if(new BigDecimal(total).compareTo(channel.getMinPrice())<0) {
                if(channel.getMaxPrice().compareTo(BigDecimal.ZERO)>0) {
                    throw new BizFailException(CodeManager.PERMISSION_DENY,String.format("交易金额区间:%s元-%s元",channel.getMinPrice().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP),channel.getMaxPrice().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)));
                } else {
                    throw new BizFailException(CodeManager.PERMISSION_DENY,"最小支付金额:"+channel.getMinPrice().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)+"元");
                }
            }
        }

        if(channel.getMaxPrice().compareTo(BigDecimal.ZERO)>0) {
            if(channel.getMaxPrice().compareTo(new BigDecimal(total))<0) {
                if(channel.getMinPrice().compareTo(BigDecimal.ZERO)>0) {
                    throw new BizFailException(CodeManager.PERMISSION_DENY,String.format("交易金额区间:%s元-%s元",channel.getMinPrice().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP),channel.getMaxPrice().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)));
                } else {
                    throw new BizFailException(CodeManager.PERMISSION_DENY,"最大支付金额:"+channel.getMaxPrice().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)+"元");
                }
            }
        }

        if(channel.getStartHour()>0 || channel.getStopHour()>0) {
            int currentHour = new Date().getHours();
            if(channel.getStartHour()>0) {
                if(currentHour<channel.getStartHour()) {
                    if(channel.getStopHour()>0) {
                        throw new BizFailException("交易时限:"+channel.getStartHour()+"点到"+channel.getStopHour()+"点");
                    } else {
                        throw new BizFailException("交易时限:"+channel.getStartHour()+"点开始");
                    }

                }
            }
            if(channel.getStopHour()>0) {
                if(currentHour>=channel.getStopHour()) {
                    if(channel.getStartHour()>0) {
                        throw new BizFailException("交易时限:"+channel.getStartHour()+"点到"+channel.getStopHour()+"点");
                    } else {
                        throw new BizFailException("交易时限:"+channel.getStopHour()+"点结束");
                    }
                }
            }
        }

        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),getChannelProvider().getCode());
        if(Objects.isNull(userGroupExt)) {
            throw new BizFailException("未注册，权限不足");
        }

        payRequest = remotePay(requestMap,request,response);

        return finishRemotePay(payRequest);
    }

    private PayRequest remotePay(Map<String,Object> requestMap,HttpServletRequest request,HttpServletResponse response) {

        String version = Utils.nvl(requestMap.get("version"));
        String service = Utils.nvl(requestMap.get("service"));
        String merchant_no = Utils.nvl(requestMap.get("merchant_no"));
        String total = Utils.nvl(requestMap.get("total"));
        String name = Utils.nvl(requestMap.get("name"));
        String remark = Utils.nvl(requestMap.get("remark"));
        String out_trade_no = Utils.nvl(requestMap.get("out_trade_no"));
        String create_ip = Utils.nvl(requestMap.get("create_ip"));
        String sub_openid = Utils.nvl(requestMap.get("sub_openid"));
        String buyer_id = Utils.nvl(requestMap.get("buyer_id"));
        String authcode = Utils.nvl(requestMap.get("authcode"));
        String bank_code = Utils.nvl(requestMap.get("bank_code"));
        String nonce_str = Utils.nvl(requestMap.get("nonce_str"));
        String sign_type = Utils.nvl(requestMap.get("sign_type"));
        String sign = Utils.nvl(requestMap.get("sign"));
        String out_notify_url = Utils.nvl(requestMap.get("out_notify_url"));
        String front_url = Utils.nvl(requestMap.get("front_url"));

        UserGroup userGroup = cacheService.getGroup(merchant_no);

        String tradeNo = merchant_no+"_"+out_trade_no;

        PayRequest payRequest = new PayRequest();
        payRequest.setAppid("");
        payRequest.setBankCode(bank_code);
        if(StringUtils.isEmpty(name) && StringUtils.isEmpty(remark)) {
            payRequest.setBody("转账:"+total);
        } else {
            payRequest.setBody(Utils.merge(",",name,remark));
        }
        if(!StringUtils.isEmpty(buyer_id)) {
            String buyerId = merchant_no+"_"+buyer_id;
            if(buyerId.length()<=10) {
                payRequest.setBuyerId(buyerId);
            } else {
                payRequest.setBuyerId(buyerId.substring(buyerId.length()-10,buyerId.length()));
            }
        }

        payRequest.setChannelProviderCode(getChannelProvider().getCode());
        payRequest.setCreateIp(create_ip);
        payRequest.setMchId(merchant_no);
        payRequest.setOutNotifyUrl(StringUtils.isEmpty(out_notify_url)?userGroup.getCallbackUrl():out_notify_url);
        payRequest.setOutTradeNo(tradeNo);
        payRequest.setRemark(remark);
        payRequest.setService(service);
        payRequest.setTotalFee(Integer.parseInt(total));
        payRequest.setSubOpenid(sub_openid);
        payRequest.setTradeType(TradeType.PAY.getValue());
        payRequest.setSign(sign);
        payRequest.setIversion(version);
        payRequest.setFrontUrl(front_url);

        payRequestDao.insertSelective(payRequest);
        payService.saveOprLog(payRequest);
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());

        doPay(payRequest,request,response);

        return payRequest;
    }

    private Map<String,Object> finishRemotePay(PayRequest payRequest) {
        PayRequestBack payRequestBack = payRequestBackDao.selectByTradeNo(payRequest.getOutTradeNo());
        if(Objects.isNull(payRequestBack)) {
            logger.error("no back data,"+payRequest.getOutTradeNo());
            return new HashMap<>();
        }
        String errcode = payRequestBack.getErrcode();
        String message = payRequestBack.getMessage();
        String no = String.valueOf(payRequest.getId());
        String out_trade_no = payRequest.getOutTradeNo().split("_")[1];
        String pay_info = payRequestBack.getPayInfo();
        String total = String.valueOf(payRequest.getTotalFee());
        String code_url = payRequestBack.getCodeUrl();
        String page_content = payRequestBack.getPageContent();
        String sign_type = "MD5";

        Map<String,Object> resultMap = MapUtils.buildMap("errcode",errcode,
                "message",message,
                "no",no,
                "out_trade_no",out_trade_no,
                "pay_info",pay_info,
                "total",total,
                "code_url",code_url,
                "page_content",page_content,
                "sign_type",sign_type);
        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
        String sign = Utils.encrypt(resultMap,userGroup.getCipherCode());
        resultMap.put("sign",sign);

        return resultMap;
    }

    @Override
    public void handleProcessingRequest(PayRequest payRequest) {
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        if(payRequest.getStatus() != PayRequestStatus.PROCESSING.getValue() && payRequest.getStatus() != PayRequestStatus.OPR_SUCCESS.getValue()) {
            logger.warn(String.format("payRequest not processing,%s,%s",payRequest.getOutTradeNo(),payRequest.getStatus()));
            return;
        }

        if(payRequest.getStatus() == PayRequestStatus.PROCESSING.getValue()) {
            Map<String,Object> payResult = query(payRequest);

            if(org.apache.commons.collections.MapUtils.isEmpty(payResult)) {
                logger.warn(String.format("query failed,%s",payRequest.getOutTradeNo()));
                return;
            }

            if(Objects.isNull(payResult.get("errcode"))) {
                logger.warn(String.format("query failed,errcode is null,%s",payRequest.getOutTradeNo()));
            }

            int errcode = new BigDecimal(String.valueOf(payResult.get("errcode"))).intValue();
            if(errcode != 0) {
                return;
            }
            logger.info(String.format("%s,query result:%s",payRequest.getOutTradeNo(),new Gson().toJson(payResult)));

            String message = String.valueOf(payResult.get("message"));
            String service = String.valueOf(payResult.get("service"));
            String no = String.valueOf(payResult.get("no"));
            String out_trade_no = String.valueOf(payResult.get("out_trade_no"));
            int status = new BigDecimal(String.valueOf(payResult.get("status"))).intValue();

            switch (status) {
                case 0:
                    logger.info(String.format("not paid,%s",payRequest.getOutTradeNo()));
                    return;
                case 1:
                    logger.info(String.format("pay success,%s",payRequest.getOutTradeNo()));
                    payService.paySuccess(payRequest.getOutTradeNo());
                    return;
                case 2:
                    logger.info(String.format("waiting pay,%s",payRequest.getOutTradeNo()));
                    return;
                case 3:
                case 4:
                case 5:
                    logger.info(String.format("pay failed,%s",payRequest.getOutTradeNo()));
                    payService.payFailed(payRequest.getOutTradeNo());
            }
        }
        notice(payRequest);
    }

    @Override
    public void notice(PayRequest payRequest) {
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        if(payRequest.getStatus() != PayRequestStatus.OPR_SUCCESS.getValue()
                && payRequest.getStatus() != PayRequestStatus.PAY_FAILED.getValue()
                && payRequest.getStatus() != PayRequestStatus.OPR_FINISHED.getValue()
                && payRequest.getStatus() != PayRequestStatus.PAY_SUCCESS.getValue()) {
            return;
        }
        if(payRequest.getNoticeRetryTime()>=propertyConfig.getOutNotifyLimit()) {
            payRequestDao.updateNoticeStatus(payRequest.getId());
            return;
        }

        payRequestDao.updateNoticeRetryTime(payRequest.getId());

        UserGroup userGroup = userGroupDao.selectByGroupNo(payRequest.getMchId());
        String url = StringUtils.isEmpty(payRequest.getOutNotifyUrl())?userGroup.getCallbackUrl():payRequest.getOutNotifyUrl();
        if(StringUtils.isEmpty(url)) {
            logger.warn(String.format("callback url is null,%s",url));
            payRequestDao.updateNoticeStatus(payRequest.getId());
            return;
        }

        Map<String,Object> resutMap = new HashMap<>();
        boolean noticeResult = false;

        if(StringUtils.equalsIgnoreCase(payRequest.getPayResult(),"0")) {
            //code 0成功 99失败
            resutMap.put("errcode","0");
            //msg
            resutMap.put("message","支付成功");

            resutMap.put("no",String.valueOf(payRequest.getId()));
            //out_trade_no
            resutMap.put("out_trade_no",payRequest.getOutTradeNo().split("_")[1]);
            //mch_id
            resutMap.put("merchant_no",payRequest.getMchId());
            //total
            resutMap.put("total",String.valueOf(payRequest.getTotalFee()));
            //fee
            resutMap.put("fee",String.valueOf(payRequest.getFee().intValue()));
            //trade_type 1:收单 2:撤销 3:退款
            resutMap.put("trade_type","1");
            //sign_type
            resutMap.put("sign_type","MD5");
            String sign = Utils.encrypt(resutMap,userGroup.getCipherCode());
            resutMap.put("sign",sign);
            logger.info("Start call back:"+payRequest.getOutTradeNo()+",url:"+url);
            noticeResult = callBackClient.post(url,resutMap);
        } else {
            resutMap.put("errcode","99");
            resutMap.put("message","支付失败");
            resutMap.put("no",payRequest.getId());
            resutMap.put("out_trade_no",payRequest.getOutTradeNo());
            resutMap.put("merchant_no",payRequest.getMchId());
            resutMap.put("trade_type","1");
            resutMap.put("sign_type","MD5");
            String sign = Utils.encrypt(resutMap,userGroup.getCipherCode());
            resutMap.put("sign",sign);
            logger.info("Start call back:"+payRequest.getOutTradeNo()+",url:"+url);
            noticeResult = callBackClient.post(url,resutMap);
        }

        if(noticeResult) {
            payRequestDao.updateNoticeStatus(payRequest.getId());
        }
    }
}
