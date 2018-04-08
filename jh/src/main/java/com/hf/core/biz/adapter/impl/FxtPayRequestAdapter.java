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
import com.hf.core.model.PropertyConfig;
import com.hf.core.model.dto.trade.unifiedorder.FxtPayRequest;
import com.hf.core.model.dto.trade.unifiedorder.HfPayRequest;
import com.hf.core.model.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Service
public class FxtPayRequestAdapter implements Adapter<FxtPayRequest> {
    @Autowired
    private PayService payService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private PropertyConfig propertyConfig;

    @Override
    public FxtPayRequest adpat(Map<String,Object> request) {
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

        UserChannel userChannel = userChannelDao.selectByGroupChannelCode(userGroup.getId(),service, ChannelProvider.FXT.getCode());
        if(Objects.isNull(userChannel) || userChannel.getStatus() != UserChannelStatus.VALID.getValue()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        Channel channel = channelDao.selectByPrimaryKey(userChannel.getChannelId());
        if(Objects.isNull(channel) || channel.getStatus() != ChannelStatus.VALID.getStatus()) {
            throw new BizFailException(CodeManager.PERMISSION_DENY,"没有权限");
        }

        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),ChannelProvider.FXT.getCode());
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

        FxtPayRequest fxtPayRequest = new FxtPayRequest();
        fxtPayRequest.setService(channelCode.getFxtCode());
        fxtPayRequest.setVersion("1.0");
        fxtPayRequest.setMerchant_no(outMerchantNo);
        fxtPayRequest.setOutlet_no(outOutLetNo);
        fxtPayRequest.setTotal(payRequest.getTotal());
        fxtPayRequest.setName(payRequest.getName());
        fxtPayRequest.setRemark(payRequest.getRemark());
        fxtPayRequest.setOut_trade_no(outTradeNo);
        fxtPayRequest.setCreate_ip(payRequest.getCreate_ip());
        fxtPayRequest.setOut_notify_url(propertyConfig.getCallbackUrl());
        fxtPayRequest.setSub_openid(payRequest.getSub_openid());
        fxtPayRequest.setBuyer_id(payRequest.getBuyer_id());
        fxtPayRequest.setAuthcode(payRequest.getAuthcode());
        fxtPayRequest.setSign_type("MD5");

        PayMsgRecord inPayMsgRecord = new PayMsgRecord(outTradeNo,payRequest.getMerchant_no(),service, OperateType.USER_HF.getValue(), TradeType.PAY.getValue(),request);
        PayMsgRecord outPayMsgRecord = new PayMsgRecord(outTradeNo,outMerchantNo,channel.getChannelCode(),OperateType.HF_CLIENT.getValue(),TradeType.PAY.getValue(),outCipherCode,MapUtils.beanToMap(fxtPayRequest));

        PayRequest hfPayRequest = new PayRequest();
        hfPayRequest.setOutTradeNo(outTradeNo);
        hfPayRequest.setBody(String.format("%s_%s",fxtPayRequest.getName(),fxtPayRequest.getRemark()));
        hfPayRequest.setMchId(payRequest.getMerchant_no());
        hfPayRequest.setSubOpenid(payRequest.getSub_openid());
        hfPayRequest.setBuyerId(payRequest.getBuyer_id());
        hfPayRequest.setService(payRequest.getService());
        hfPayRequest.setAppid("");
        hfPayRequest.setSign(payRequest.getSign());
        hfPayRequest.setTotalFee(Integer.parseInt(payRequest.getTotal()));
        hfPayRequest.setChannelProviderCode(ChannelProvider.FXT.getCode());

        payService.savePayRequest(Arrays.asList(inPayMsgRecord,outPayMsgRecord),hfPayRequest);

        fxtPayRequest.setNonce_str(Utils.getRandomString(8));
        String sign = Utils.encrypt(MapUtils.beanToMap(fxtPayRequest),outCipherCode);
        fxtPayRequest.setSign(sign);

        return fxtPayRequest;
    }
}
