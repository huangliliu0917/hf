package com.hf.core.biz.impl;

import com.hf.base.contants.CodeManager;
import com.hf.base.enums.*;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.Utils;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.dto.*;
import com.hf.core.model.po.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class YsPayBiz extends AbstractPayBiz {
    @Autowired
    @Qualifier("ysClient")
    private PayClient payClient;
    @Autowired
    private PayService payService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private UserGroupDao userGroupDao;

    @Override
    PayClient getPayClient() {
        return payClient;
    }

    public void checkParam(Map<String, Object> map) {
        super.checkField(map);
        String service = String.valueOf(map.get("service"));
        String sub_openid = String.valueOf(map.get("sub_openid"));
        String buyer_id = String.valueOf(map.get("buyer_id"));

        ChannelCode channelCode = ChannelCode.parseFromCode(service);
        if(channelCode == ChannelCode.WX_OPEN && Utils.isEmpty(sub_openid)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"参数错误,微信公众号支付sub_openid不能为空");
        }
        if(channelCode == ChannelCode.ALI_OPEN && Utils.isEmpty(buyer_id)) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"支付宝服务窗体支付buyer_id不能为空");
        }
    }

    public Long savePayRequest(Map<String, Object> map) {
        UserGroup userGroup = cacheService.getGroup(String.valueOf(map.get("merchant_no")));
        if(Objects.isNull(userGroup) || userGroup.getStatus() != GroupStatus.AVAILABLE.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),String.valueOf(map.get("service")), ChannelProvider.YS.getCode());
        if(Objects.isNull(userChannel) || userChannel.getStatus() != UserChannelStatus.VALID.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
        if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.YS.getCode());
        if(Objects.isNull(userGroupExt)) {
            throw new BizFailException("未注册，权限不足");
        }
        String service = String.valueOf(map.get("service"));

        ChannelCode channelCode = ChannelCode.parseFromCode(service);

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
        String appid = String.valueOf(map.get("appid"));
        String sign_type = "MD5";
        String sign = String.valueOf(map.get("sign"));

        if(!Utils.checkEncrypt(map,userGroup.getCipherCode())) {
            throw new BizFailException(CodeManager.CHECK_ENCRIPT_FAILED,"验签失败");
        }

        String outTradeNo = String.format("%s_%s",merchant_no,out_trade_no);

        Map<String,Object> outRequest = new HashMap<>();
        outRequest.put("total_fee",total);
        outRequest.put("body",name);
        outRequest.put("out_trade_no",outTradeNo);
        outRequest.put("mch_id",userGroupExt.getMerchantNo());
        if(!Utils.isEmpty(sub_openid)) {
            outRequest.put("sub_openid",sub_openid);
        }
        if(!Utils.isEmpty(buyer_id)) {
            outRequest.put("buyer_id",buyer_id);
        }
        outRequest.put("service",channel.getChannelCode());
        if(!Utils.isEmpty(appid)) {
            outRequest.put("appid",appid);
        }
        sign = Utils.encrypt2(outRequest,userGroupExt.getCipherCode());
        outRequest.put("sign",sign);

        PayMsgRecord inPayMsgRecord = new PayMsgRecord(outTradeNo,merchant_no,service, OperateType.USER_HF.getValue(), TradeType.PAY.getValue(),map);
        PayMsgRecord outPayMsgRecord = new PayMsgRecord(outTradeNo,userGroupExt.getMerchantNo(),channel.getChannelCode(),OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue(),outRequest);

        PayRequest payRequest = new PayRequest();
        payRequest.setOutTradeNo(outTradeNo);
        payRequest.setBody(name);
        payRequest.setMchId(merchant_no);
        payRequest.setSubOpenid(sub_openid);
        payRequest.setBuyerId(buyer_id);
        payRequest.setService(service);
        payRequest.setAppid(appid);
        payRequest.setSign(sign);
        payRequest.setTotalFee(total);
        payRequest.setChannelProviderCode(ChannelProvider.YS.getCode());
        payService.savePayRequest(Arrays.asList(inPayMsgRecord,outPayMsgRecord),payRequest);

        return payRequest.getId();
    }

    @Override
    void handlePayResult(PayRequest payRequest, PayMsgRecord payMsgRecord, Map<String, Object> payResult) {
        if(MapUtils.isEmpty(payResult)) {
            throw new BizFailException("pay result empty");
        }
        if(Objects.isNull(payResult.get("status"))) {
            throw new BizFailException("pay result status numm");
        }
        PayMsgRecord remoteReturnMsgRecord = new PayMsgRecord(payMsgRecord.getOutTradeNo(),payMsgRecord.getMerchantNo(),payMsgRecord.getService(),OperateType.CLIENT_HF.getValue(), TradeType.PAY.getValue(),payResult);
        try {
            payMsgRecordDao.insertSelective(remoteReturnMsgRecord);
        } catch (DuplicateKeyException e) {
            logger.warn(String.format("pay msg already exist,%s,%s,%s",remoteReturnMsgRecord.getOutTradeNo(),remoteReturnMsgRecord.getTradeType(),remoteReturnMsgRecord.getOperateType()));
        }

        String status = String.valueOf(payResult.get("status"));
        String message = String.valueOf(null==payResult.get("msg")?"":payResult.get("msg"));
        Map<String,Object> resultMap = new HashMap<>();
        if("0".equalsIgnoreCase(status)) {
            resultMap.put("errcode",CodeManager.PAY_SUCCESS);
            resultMap.put("message",message);
            resultMap.put("no",payRequest.getId());
            resultMap.put("out_trade_no",payRequest.getOutTradeNo().split("_")[1]);
            resultMap.put("pay_info",payResult.get("package"));
            resultMap.put("total",payRequest.getTotalFee());
            resultMap.put("sign_type","MD5");
            UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
            String sign = Utils.encrypt(resultMap,userGroup.getCipherCode());
            resultMap.put("sign",sign);
        } else {
            resultMap.put("errcode",status);
            resultMap.put("message",message);
        }
        PayMsgRecord hfToUserMsgRecord = new PayMsgRecord(payMsgRecord.getOutTradeNo(),payMsgRecord.getMerchantNo(),payMsgRecord.getService(),OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),resultMap);
        try {
            payMsgRecordDao.insertSelective(hfToUserMsgRecord);
        } catch (DuplicateKeyException e ) {
            logger.warn(String.format("pay msg already exists,%s,%s,%s",hfToUserMsgRecord.getOutTradeNo(),hfToUserMsgRecord.getTradeType(),hfToUserMsgRecord.getOperateType()));
        }

        if("0".equalsIgnoreCase(status)) {
            payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_GENERATED.getValue(),PayRequestStatus.PROCESSING.getValue());
        } else {
            payService.payFailed(payRequest.getOutTradeNo());
        }
    }

    @Override
    public ChannelProvider getProvider() {
        return ChannelProvider.YS;
    }

    @Override
    public void checkCallBack(Map<String, Object> map) {
        if(MapUtils.isEmpty(map)) {
            throw new BizFailException("callback result empty");
        }

        String[] needFields = {"mch_id","trade_type","out_trade_no","out_transaction_id","transaction_id","pay_result","result_code","status","sign"};
        for(String field:needFields) {
            if(Objects.isNull(map.get(field))) {
                throw new BizFailException(String.format("%s is null",field));
            }
        }
    }

    @Override
    public void finishPay(Map<String,Object> params) {
        String mch_id = String.valueOf(params.get("mch_id"));
        String out_trade_no = String.valueOf(params.get("out_trade_no"));
        PayRequest payRequest = payRequestDao.selectByTradeNo(out_trade_no);
        if(Objects.isNull(payRequest)) {
            throw new BizFailException("payRequest is null");
        }

        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;
        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        String trade_type = String.valueOf(params.get("trade_type"));
        String pay_result = String.valueOf(params.get("pay_result"));
        PayMsgRecord payMsgRecord = new PayMsgRecord(out_trade_no,mch_id,trade_type,OperateType.CALLBACK_CLIENT_HF.getValue(),TradeType.PAY.getValue(),params);
        payService.savePayMsg(payMsgRecord);

        if("0".equalsIgnoreCase(pay_result)) {
            payService.paySuccess(out_trade_no);
        } else {
            payService.payFailed(out_trade_no);
        }
    }
}
