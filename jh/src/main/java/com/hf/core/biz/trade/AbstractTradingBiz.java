package com.hf.core.biz.trade;

import com.hf.base.contants.CodeManager;
import com.hf.base.enums.*;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.api.PayApi;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.*;
import com.hf.core.model.po.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

    protected Logger logger = LoggerFactory.getLogger(AbstractTradingBiz.class);

    public abstract ChannelProvider getChannelProvider();
    public abstract void doPay(PayRequest payRequest);

    public Map<String,Object> pay(Map<String,Object> requestMap) {
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
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        if(!Utils.checkEncrypt(requestMap,userGroup.getCipherCode())) {
            throw new BizFailException(CodeManager.CHECK_ENCRIPT_FAILED,"验签失败");
        }

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),service, getChannelProvider().getCode());
        if(Objects.isNull(userChannel) || userChannel.getStatus() != UserChannelStatus.VALID.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
        if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),getChannelProvider().getCode());
        if(Objects.isNull(userGroupExt)) {
            throw new BizFailException("未注册，权限不足");
        }

        payRequest = remotePay(requestMap);

        return finishRemotePay(payRequest);
    }

    private PayRequest remotePay(Map<String,Object> requestMap) {

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

        UserGroup userGroup = cacheService.getGroup(merchant_no);

        String tradeNo = merchant_no+"_"+out_trade_no;

        PayRequest payRequest = new PayRequest();
        payRequest.setAppid("");
        payRequest.setBankCode(bank_code);
        payRequest.setBody(String.format("%s_%s",name,remark));
        payRequest.setBuyerId(buyer_id);
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

        payRequestDao.insertSelective(payRequest);
        payService.saveOprLog(payRequest);
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());

        doPay(payRequest);

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
}
