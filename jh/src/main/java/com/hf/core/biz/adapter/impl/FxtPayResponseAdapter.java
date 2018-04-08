package com.hf.core.biz.adapter.impl;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.OperateType;
import com.hf.base.enums.TradeType;
import com.hf.base.utils.Utils;
import com.hf.core.biz.adapter.Adapter;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.PayService;
import com.hf.core.dao.local.PayMsgRecordDao;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.model.dto.trade.unifiedorder.HfPayResponse;
import com.hf.core.model.enums.ErrCode;
import com.hf.core.model.po.PayMsgRecord;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Service
public class FxtPayResponseAdapter implements Adapter<HfPayResponse> {

    protected Logger logger = LoggerFactory.getLogger(FxtPayResponseAdapter.class);

    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayMsgRecordDao payMsgRecordDao;
    @Autowired
    private PayService payService;
    @Autowired
    private CacheService cacheService;

    @Override
    public HfPayResponse adpat(Map<String, Object> request) {
        PayMsgRecord inputMsgRecord = (PayMsgRecord)request.get("inputMsg");
        request.remove("inputMsg");
        PayMsgRecord resultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.CLIENT_HF.getValue(),TradeType.PAY.getValue(),request);
        try {
            payMsgRecordDao.insertSelective(resultMsg);
        } catch (DuplicateKeyException e) {
            logger.warn("msg already exist,%s",new Gson().toJson(resultMsg));
        }

        HfPayResponse defaultResponse = new HfPayResponse();
        defaultResponse.setErrcode(CodeManager.WAITING_RESPONSE);
        defaultResponse.setMessage("等待服务器响应");

        if(MapUtils.isEmpty(request) || Objects.isNull(request.get("errcode"))) {
            return defaultResponse;
        }

        String errCode = String.valueOf(request.get("errcode"));
        String message = String.valueOf(request.get("message"));

        HfPayResponse response = new HfPayResponse();
        response.setErrcode(errCode);
        response.setMessage(message);

        PayMsgRecord hfResultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),response);

        if(ErrCode.equals(errCode,ErrCode.SUCCESS) || ErrCode.equals(errCode,ErrCode.WAITING_PAY)) {
            PayRequest payRequest = payRequestDao.selectByTradeNo(String.valueOf(request.get("out_trade_no")));
            UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
            response.setNo(String.valueOf(payRequest.getId()));
            response.setOut_trade_no(payRequest.getOutTradeNo().split("_")[1]);
            if(!Objects.isNull(request.get("pay_info"))) {
                response.setPay_info(String.valueOf(request.get("pay_info")));
            }
            if(!Objects.isNull(request.get("total"))) {
                response.setTotal(String.valueOf(request.get("total")));
            }
            if(!Objects.isNull(request.get("code_url"))) {
                response.setCode_url(String.valueOf(request.get("code_url")));
            }

            response.setErrcode(String.valueOf(new BigDecimal(errCode).intValue()));

            response.setSign_type("MD5");
            String sign = Utils.encrypt(com.hf.base.utils.MapUtils.beanToMap(response),userGroup.getCipherCode());
            response.setSign(sign);

            hfResultMsg = new PayMsgRecord(inputMsgRecord.getOutTradeNo(),inputMsgRecord.getMerchantNo(),inputMsgRecord.getService(), OperateType.HF_USER.getValue(),TradeType.PAY.getValue(),response);
            payService.remoteSuccess(payRequest,hfResultMsg);
        } else {
            payService.payFailed(inputMsgRecord.getOutTradeNo(),hfResultMsg);
        }
        return response;
    }
}
