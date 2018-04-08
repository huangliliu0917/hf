package com.hf.core.biz.adapter.impl;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelCode;
import com.hf.base.enums.OperateType;
import com.hf.base.enums.TradeType;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.PayMsgRecordDao;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.model.dto.trade.unifiedorder.WwPayResponse;
import com.hf.core.model.po.PayMsgRecord;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WwPayResponseAdapter implements Adapter<WwPayResponse> {
    protected Logger logger = LoggerFactory.getLogger(WwPayResponseAdapter.class);

    @Autowired
    private PayMsgRecordDao payMsgRecordDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private PayService payService;

    @Override
    public WwPayResponse adpat(Map<String, Object> request) {
        PayMsgRecord inputMsgRecord = (PayMsgRecord)request.get("inputMsg");
        request.remove("inputMsg");
        PayMsgRecord resultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.CLIENT_HF.getValue(), TradeType.PAY.getValue(),request);
        try {
            payMsgRecordDao.insertSelective(resultMsg);
        } catch (DuplicateKeyException e) {
            logger.warn("msg already exist,%s",new Gson().toJson(resultMsg));
        }

        WwPayResponse payResponse = new WwPayResponse();
        payResponse.setErrcode(CodeManager.WAITING_RESPONSE);
        payResponse.setMessage("等待服务器响应");

        String payContent = String.valueOf(request.get("payResult"));

        ChannelCode channelCode = ChannelCode.parseFromCode(resultMsg.getService());
        switch (channelCode) {
            case WX_H5:
            case QQ_H5:
                if(StringUtils.isEmpty(payContent) || !payContent.contains("var url = ")) {
                    String payResult = request.get("payResult") == null?"":String.valueOf(request.get("payResult"));
                    try {
                        String message = payResult.substring(payResult.indexOf("<span>")+6,payResult.lastIndexOf("</span>")).replace("<b>","").replace("</b>","").replace("\n","").replace("<p>","").replace("</p>","").replace("，",",").replace("\t","").replace("<span>","").replace("</span>","").replace("\r","");
                        payResponse.setMessage(message);
                    } catch (Exception e) {
                        payResponse.setMessage(payResult);
                    }

                    payResponse.setErrcode(CodeManager.BIZ_FAIELD);
                    PayMsgRecord hfResultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),payResponse);
                    payService.payFailed(inputMsgRecord.getOutTradeNo(),hfResultMsg);
                    return payResponse;
                }
                payResponse.setErrcode(CodeManager.PAY_SUCCESS);
                payResponse.setMessage("下单成功");
                String codeUrl = payContent.split("'")[1];
                payResponse.setCode_url(codeUrl);
                break;
            case WY:
                if(StringUtils.isEmpty(payContent) || !payContent.contains("pGateWayReq")) {
                    String payResult = request.get("payResult") == null?"":String.valueOf(request.get("payResult"));
                    try {
                        String message = payResult.substring(payResult.indexOf("<span>")+6,payResult.lastIndexOf("</span>")).replace("<b>","").replace("</b>","").replace("\n","").replace("<p>","").replace("</p>","").replace("，",",").replace("\t","").replace("<span>","").replace("</span>","").replace("\r","");
                        payResponse.setMessage(message);
                    } catch (Exception e) {
                        payResponse.setMessage(payResult);
                    }
                    payResponse.setErrcode(CodeManager.PAY_FAILED);
                    PayMsgRecord hfResultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),payResponse);
                    payService.payFailed(inputMsgRecord.getOutTradeNo(),hfResultMsg);
                    return payResponse;
                }
                payResponse.setErrcode(CodeManager.PAY_SUCCESS);
                payResponse.setMessage("下单成功");
                String content = payContent.substring(payContent.indexOf("<form"),payContent.indexOf("</form>")+7);
                payResponse.setPage_content(content);
                break;
            default:

        }

        String outTradeNo = String.valueOf(request.get("outTradeNo"));
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);

        payResponse.setNo(String.valueOf(payRequest.getId()));
        payResponse.setOut_trade_no(outTradeNo);
        payResponse.setSign_type("MD5");
        payResponse.setTotal(String.valueOf(payRequest.getTotalFee()));

        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
        String sign = Utils.encrypt(MapUtils.beanToMap(payResponse),userGroup.getCipherCode());
        payResponse.setSign(sign);

        PayMsgRecord hfResultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),payResponse);

        payService.remoteSuccess(payRequest,hfResultMsg);
        return payResponse;
    }
}
