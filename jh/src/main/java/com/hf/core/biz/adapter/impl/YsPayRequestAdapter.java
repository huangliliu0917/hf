package com.hf.core.biz.adapter.impl;

import com.hf.base.contants.CodeManager;
import com.hf.base.enums.*;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.TypeConverter;
import com.hf.base.utils.Utils;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.ChannelDao;
import com.hf.core.dao.local.UserChannelDao;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.model.dto.trade.unifiedorder.HfPayRequest;
import com.hf.core.model.dto.trade.unifiedorder.YsPayRequest;
import com.hf.core.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Service
public class YsPayRequestAdapter implements Adapter<YsPayRequest> {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private PayService payService;

    @Override
    public YsPayRequest adpat(Map<String, Object> request) {
        HfPayRequest payRequest = TypeConverter.convert(request,HfPayRequest.class);
        String merchantNo = payRequest.getMerchant_no();
        String service = payRequest.getService();

        UserGroup userGroup = cacheService.getGroup(merchantNo);
        if(Objects.isNull(userGroup) || userGroup.getStatus() != GroupStatus.AVAILABLE.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        if(!Utils.checkEncrypt(request,userGroup.getCipherCode())) {
            throw new BizFailException(CodeManager.CHECK_ENCRIPT_FAILED,"验签失败");
        }

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),service, ChannelProvider.YS.getCode());
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

        ChannelCode channelCode = ChannelCode.parseFromCode(service);

        if(channelCode == ChannelCode.WX_OPEN && Utils.isEmpty(payRequest.getSub_openid())) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"参数错误,微信公众号支付sub_openid不能为空");
        }
        if(channelCode == ChannelCode.ALI_OPEN && Utils.isEmpty(payRequest.getBuyer_id())) {
            throw new BizFailException(CodeManager.PARAM_CHECK_FAILED,"支付宝服务窗体支付buyer_id不能为空");
        }

        String outTradeNo = String.format("%s_%s",payRequest.getMerchant_no(),payRequest.getOut_trade_no());
        String outMerchantNo = userGroupExt.getMerchantNo();
        String outOutLetNo = userGroupExt.getOutletNo();
        String outCipherCode = userGroupExt.getCipherCode();

        YsPayRequest ysPayRequest = new YsPayRequest();
        ysPayRequest.setTotal_fee(payRequest.getTotal());
        ysPayRequest.setBody(payRequest.getName());
        ysPayRequest.setOut_trade_no(outTradeNo);
        ysPayRequest.setMch_id(outMerchantNo);
        if(!Utils.isEmpty(payRequest.getSub_openid())) {
            ysPayRequest.setSub_openid(payRequest.getSub_openid());
        }
        if(!Utils.isEmpty(payRequest.getBuyer_id())) {
            ysPayRequest.setBuyer_id(payRequest.getBuyer_id());
        }
        ysPayRequest.setService(ChannelCode.parseFromCode(payRequest.getService()).getYsCode());
//        ysPayRequest.setAppid();
        PayMsgRecord inPayMsgRecord = new PayMsgRecord(outTradeNo,payRequest.getMerchant_no(),service, OperateType.USER_HF.getValue(), TradeType.PAY.getValue(),request);
        PayMsgRecord outPayMsgRecord = new PayMsgRecord(outTradeNo,payRequest.getMerchant_no(),channel.getChannelCode(),OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue(),outCipherCode, MapUtils.beanToMap(ysPayRequest));

        PayRequest hfPayRequest = new PayRequest();
        hfPayRequest.setOutTradeNo(outTradeNo);
        hfPayRequest.setBody(hfPayRequest.getBody());
        hfPayRequest.setMchId(hfPayRequest.getMchId());
        hfPayRequest.setSubOpenid(hfPayRequest.getSubOpenid());
        hfPayRequest.setBuyerId(hfPayRequest.getBuyerId());
        hfPayRequest.setService(hfPayRequest.getService());
        hfPayRequest.setAppid(hfPayRequest.getAppid());
        hfPayRequest.setSign(hfPayRequest.getSign());
        hfPayRequest.setTotalFee(hfPayRequest.getTotalFee());
        hfPayRequest.setChannelProviderCode(ChannelProvider.YS.getCode());
        payService.savePayRequest(Arrays.asList(inPayMsgRecord,outPayMsgRecord),hfPayRequest);

        return ysPayRequest;
    }
}
