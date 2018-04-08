package com.hf.core.biz.impl;

import com.hf.base.contants.CodeManager;
import com.hf.base.enums.*;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.Utils;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.*;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.dto.*;
import com.hf.core.model.po.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class FxtPayBiz extends AbstractPayBiz {
    @Autowired
    private PayService payService;
    @Autowired
    @Qualifier("fxtClient")
    private PayClient payClient;

    private static String[] requestFields = {"service","version","merchant_no","outlet_no","total","name","remark","out_trade_no","create_ip","out_notify_url","authcode","nonce_str","sign_type","sign"};
    private static String[] unifiedorderRespFields = {"errcode","message","no","out_trade_no","code_url","pay_info","total","transaction_id","paytime","sign_type","sign"};


    public void checkParam(Map<String, Object> map) {
        for(String field:requestFields) {
            if(Objects.isNull(map.get(field))) {
                throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,String.format("参数错误:%s不能为空",field));
            }
        }

        String service = String.valueOf(map.get("service"));
        String sub_openid = String.valueOf(map.get("sub_openid"));
        String buyer_id = String.valueOf(map.get("buyer_id"));

        if("01".equalsIgnoreCase(service) && Utils.isEmpty(sub_openid)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"参数错误,微信公众号支付sub_openid不能为空");
        }

        if("06".equalsIgnoreCase(service) && Utils.isEmpty(buyer_id)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"支付宝服务窗体支付buyer_id不能为空");
        }
    }

    public Long savePayRequest(Map<String, Object> map) {
        UserGroup userGroup = cacheService.getGroup(String.valueOf(map.get("merchant_no")));
        if(Objects.isNull(userGroup) || userGroup.getStatus() != GroupStatus.AVAILABLE.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),String.valueOf(map.get("service")), ChannelProvider.FXT.getCode());
        if(Objects.isNull(userChannel) || userChannel.getStatus() != UserChannelStatus.VALID.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
        if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        //check enth
        if(!Utils.checkEncrypt(map,userChannel.getCipherCode())) {
            throw new BizFailException(CodeManager.CHECK_ENCRIPT_FAILED,"验签失败");
        }

        String service = String.valueOf(map.get("service"));
        String version = String.valueOf(map.get("version"));
        String merchant_no = String.valueOf(map.get("merchant_no"));
        String outlet_no = String.valueOf(map.get("outlet_no"));
        int total = Integer.parseInt(map.get("total").toString());
        String name = String.valueOf(map.get("name"));
        String remark = String.valueOf(null==map.get("remark")?"":map.get("remark"));
        String out_trade_no = String.valueOf(map.get("out_trade_no"));
        String create_ip = String.valueOf(map.get("create_ip"));
        String out_notify_url = String.valueOf(map.get("out_notify_url"));
        String sub_openid = String.valueOf(map.get("sub_openid"));
        String buyer_id = String.valueOf(map.get("buyer_id"));
        String authcode = String.valueOf(map.get("authcode"));
        String nonce_str = String.valueOf(map.get("nonce_str"));
        String sign_type = "MD5";
        String sign = String.valueOf(map.get("sign"));

        AdminBankCard adminBankCard = payService.chooseAdminBank1(userGroup.getCompanyId(),new BigDecimal(total));
        Map<String,Object> outRequest = new HashMap<>();
        outRequest.put("service",service);
        outRequest.put("version",version);
        outRequest.put("merchant_no",adminBankCard.getMchId());
        outRequest.put("outlet_no",adminBankCard.getOutletNo());
        outRequest.put("total",total);
        outRequest.put("name",name);
        outRequest.put("remark",remark);
        outRequest.put("out_trade_no",out_trade_no);
        outRequest.put("create_ip",create_ip);
        outRequest.put("out_notify_url",channel.getUrl());
        if(StringUtils.isNotEmpty(sub_openid)) {
            outRequest.put("sub_openid",sub_openid);
        }
        if(StringUtils.isNotEmpty(buyer_id)) {
            outRequest.put("buyer_id",buyer_id);
        }
        outRequest.put("authcode",authcode);
        outRequest.put("nonce_str",Utils.getRandomString(8));
        outRequest.put("sign_type","MD5");
        sign = Utils.encrypt(outRequest,adminBankCard.getCipherCode());
        outRequest.put("sign",sign);

        //save record
        PayMsgRecord inPayMsgRecord = new PayMsgRecord(out_trade_no,merchant_no,service, OperateType.USER_HF.getValue(), TradeType.PAY.getValue(),map);
        PayMsgRecord outPayMsgRecord = new PayMsgRecord(out_trade_no,adminBankCard.getMchId(),service,OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue(),outRequest);

        PayRequest payRequest = new PayRequest();
        payRequest.setOutTradeNo(out_trade_no);
        payRequest.setBody(String.format("%s,%s",name,remark));
        payRequest.setMchId(merchant_no);
        payRequest.setSubOpenid(sub_openid);
        payRequest.setBuyerId(buyer_id);
        payRequest.setService(service);
        payRequest.setAppid("");
        payRequest.setSign(sign);
        payRequest.setTotalFee(total);
        payService.savePayRequest(Arrays.asList(inPayMsgRecord,outPayMsgRecord),payRequest);

        return payRequest.getId();
    }

    @Override
    PayClient getPayClient() {
        return payClient;
    }

    @Override
    void handlePayResult(PayRequest payRequest,PayMsgRecord payMsgRecord,Map<String, Object> payResult) {
        if(MapUtils.isEmpty(payResult)) {
            throw new BizFailException();
        }

        if(Objects.isNull(payResult.get("errcode"))) {
            throw new BizFailException();
        }

        PayMsgRecord remoteReturnMsgRecord = new PayMsgRecord(payMsgRecord.getOutTradeNo(),payMsgRecord.getMerchantNo(),payMsgRecord.getService(),OperateType.CLIENT_HF.getValue(), TradeType.PAY.getValue(),payResult);
        try {
            payMsgRecordDao.insertSelective(remoteReturnMsgRecord);
        } catch (DuplicateKeyException e) {
            logger.warn(String.format("pay msg already exist,%s,%s,%s",remoteReturnMsgRecord.getOutTradeNo(),remoteReturnMsgRecord.getTradeType(),remoteReturnMsgRecord.getOperateType()));
        }

        PayMsgRecord hfToUserMsgRecord = new PayMsgRecord(payMsgRecord.getOutTradeNo(),payMsgRecord.getMerchantNo(),payMsgRecord.getService(),OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),payResult);
        try {
            payMsgRecordDao.insertSelective(hfToUserMsgRecord);
        } catch (DuplicateKeyException e ){
            logger.warn(String.format("pay msg already exists,%s,%s,%s",hfToUserMsgRecord.getOutTradeNo(),hfToUserMsgRecord.getTradeType(),hfToUserMsgRecord.getOperateType()));
        }

        String errcode = String.valueOf(payResult.get("errcode"));
        String message = String.valueOf(payResult.get("message"));
        if("0".equalsIgnoreCase(errcode) || "4".equalsIgnoreCase(errcode)) {
            payService.saveOprLog(payRequest);
        } else {
            payRequestDao.updateFailed(payRequest.getId(),PayRequestStatus.NEW.getValue(),message);
        }
    }

    @Override
    public ChannelProvider getProvider() {
        return ChannelProvider.FXT;
    }

    @Override
    public void checkCallBack(Map<String, Object> map) {
        String[] needFields = {"errcode","message","no","out_trade_no","sign_type","sign"};
        if(MapUtils.isEmpty(map)) {
            throw new BizFailException("callback result empty");
        }
        for(String field:needFields) {
            if(Objects.isNull(map.get(field))) {
                throw new BizFailException(String.format("%s is null",field));
            }
        }
    }

    @Override
    public void finishPay(Map<String, Object> map) {
        String tradeNo = String.valueOf(map.get("out_trade_no"));
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);

        if(Objects.isNull(payRequest)) {
            throw new BizFailException("payRequest is null");
        }

        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;
        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());

        PayMsgRecord payMsgRecord = new PayMsgRecord(tradeNo,userGroup.getGroupNo(),payRequest.getService(),OperateType.CALLBACK_CLIENT_HF.getValue(),TradeType.PAY.getValue(),map);
        payService.savePayMsg(payMsgRecord);

        String errcode = String.valueOf(map.get("errcode"));

        if("0".equalsIgnoreCase(errcode)) {
            payService.paySuccess(tradeNo);
        } else {
            payService.payFailed(tradeNo);
        }
    }
}
