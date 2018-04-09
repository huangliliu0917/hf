package com.hf.core.biz.trade;

import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelCode;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupExtDao;
import com.hf.core.dao.remote.WwClient;
import com.hf.core.model.PropertyConfig;
import com.hf.core.model.dto.trade.unifiedorder.WwPayRequest;
import com.hf.core.model.po.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class WwTradingBiz extends AbstractTradingBiz {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private PropertyConfig propertyConfig;
    @Autowired
    private WwClient wwClient;
    @Autowired
    private PayService payService;
    @Autowired
    private PayRequestDao payRequestDao;

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.WW;
    }

    @Override
    public void doPay(PayRequest payRequest) {
        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
        ChannelProvider channelProvider = ChannelProvider.parse(payRequest.getChannelProviderCode());
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(userGroup.getId(),channelProvider.getCode());

        WwPayRequest wwPayRequest = new WwPayRequest();
        ChannelCode channelCode = ChannelCode.parseFromCode(payRequest.getService());
        if(channelCode == ChannelCode.WX_H5) {
            wwPayRequest.setMemberCode(userGroupExt.getMerchantNo());
            wwPayRequest.setOrderNum(payRequest.getOutTradeNo());
            wwPayRequest.setPayMoney(String.valueOf(new BigDecimal(payRequest.getTotalFee()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)));
            wwPayRequest.setPayType("1");
            wwPayRequest.setSceneInfo("慧富微信H5支付");
            wwPayRequest.setIp(payRequest.getCreateIp());
            wwPayRequest.setCallbackUrl(propertyConfig.getWwCallbackUrl());
            wwPayRequest.setChannelCode(payRequest.getService());
        } else if(channelCode == ChannelCode.QQ_H5) {
            wwPayRequest.setMemberCode(userGroupExt.getMerchantNo());
            wwPayRequest.setOrderNum(payRequest.getOutTradeNo());
            wwPayRequest.setPayMoney(String.valueOf(new BigDecimal(payRequest.getTotalFee()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)));
            wwPayRequest.setPayType("3");
            wwPayRequest.setSceneInfo("慧富QQH5支付");
            wwPayRequest.setIp(payRequest.getCreateIp());
            wwPayRequest.setCallbackUrl(propertyConfig.getWwCallbackUrl());
            wwPayRequest.setChannelCode(payRequest.getService());
        } else if(channelCode == ChannelCode.WY) {
            wwPayRequest.setCallbackUrl(propertyConfig.getWwCallbackUrl());
            wwPayRequest.setMemberCode(userGroupExt.getMerchantNo());
            wwPayRequest.setOrderNum(payRequest.getOutTradeNo());
            wwPayRequest.setPayMoney(String.valueOf(new BigDecimal(payRequest.getTotalFee()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP)));
            wwPayRequest.setChannelCode(payRequest.getService());
            if(!Utils.isEmpty(payRequest.getBankCode())) {
                wwPayRequest.setBankCode(payRequest.getBankCode());
            }
            if(!Utils.isEmpty(payRequest.getRemark())) {
                wwPayRequest.setGoodsName(payRequest.getRemark());
            }
        } else {
            throw new BizFailException("no channel defined");
        }

        Map<String,Object> response = wwClient.unifiedorder(MapUtils.beanToMap(wwPayRequest));
        PayRequestBack payRequestBack = new PayRequestBack();
        payRequestBack.setMchId(payRequest.getMchId());
        payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());
        String payContent = String.valueOf(response.get("payResult"));

        switch (channelCode) {
            case WX_H5:
            case QQ_H5:
                if(StringUtils.isEmpty(payContent) || !payContent.contains("var url = ")) {
                    String payResult = response.get("payResult") == null?"":String.valueOf(response.get("payResult"));
                    try {
                        String message = payResult.substring(payResult.indexOf("<span>")+6,payResult.lastIndexOf("</span>")).replace("<b>","").replace("</b>","").replace("\n","").replace("<p>","").replace("</p>","").replace("，",",").replace("\t","").replace("<span>","").replace("</span>","").replace("\r","");
                        payRequestBack.setMessage(message);
                    } catch (Exception e) {
                        payRequestBack.setMessage(payResult);
                    }

                    payRequestBack.setErrcode(CodeManager.BIZ_FAIELD);
                    payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
                    return;
                }
                payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);
                payRequestBack.setMessage("下单成功");
                String codeUrl = payContent.split("'")[1];
                payRequestBack.setCodeUrl(codeUrl);
                break;
            case WY:
                if(StringUtils.isEmpty(payContent) || !payContent.contains("pGateWayReq")) {
                    String payResult = response.get("payResult") == null?"":String.valueOf(response.get("payResult"));
                    try {
                        String message = payResult.substring(payResult.indexOf("<span>")+6,payResult.lastIndexOf("</span>")).replace("<b>","").replace("</b>","").replace("\n","").replace("<p>","").replace("</p>","").replace("，",",").replace("\t","").replace("<span>","").replace("</span>","").replace("\r","");
                        payRequestBack.setMessage(message);
                    } catch (Exception e) {
                        payRequestBack.setMessage(payResult);
                    }
                    payRequestBack.setErrcode(CodeManager.PAY_FAILED);
                    payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
                    return;
                }
                payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);
                payRequestBack.setMessage("下单成功");
                String content = payContent.substring(payContent.indexOf("<form"),payContent.indexOf("</form>")+7);
                payRequestBack.setPageContent(content);
                break;
            default:
        }

        payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
        payService.remoteSuccess(payRequest,payRequestBack);
    }
}
