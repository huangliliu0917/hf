package com.hf.core.biz.trade;

import com.google.gson.Gson;
import com.hf.base.enums.ChannelCode;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.dao.remote.WhpClient;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.PayRequestBack;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class WhpTradingBiz extends AbstractTradingBiz {
    @Autowired
    private WhpClient whpClient;

    enum PayType {
        WX_H5("pay.weixin.h5",ChannelCode.WX_H5.getCode()),
        WX_SM("pay.weixin.qrcode",ChannelCode.WX_SM.getCode()),
        ALI_H5("pay.alipay.wap",ChannelCode.ALI_H5.getCode()),
        ALI_SM("pay.alipay.qrcode",ChannelCode.ALI_ZS.getCode());

        private String code;
        private String localCode;

        PayType(String code,String localCode) {
            this.code = code;
            this.localCode = localCode;
        }

        public static PayType parse(String localCode) {
            for(PayType payType:PayType.values()) {
                if(StringUtils.equals(payType.localCode,localCode)) {
                    return payType;
                }
            }
            return null;
        }
    }

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.WHP;
    }

    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> map = new HashMap<>();
        String mchid = "26015";
        String cipherCode = "qgS0InuHKnaxb7t4HrePnpHLJQOYKtYZ";
        map.put("mchid",mchid);
        if(null == PayType.parse(payRequest.getService())) {
            throw new BizFailException("通道"+payRequest.getService()+"不支持");
        }
        map.put("paytype",PayType.parse(payRequest.getService()).code);
        map.put("time",new SimpleDateFormat("yyyyMMddHHmmss").format(payRequest.getCreateTime()));
        map.put("amount",payRequest.getTotalFee());
        map.put("out_tradeid",payRequest.getOutTradeNo());
        map.put("subject",payRequest.getBody());
        if(StringUtils.isNotEmpty(payRequest.getRemark())) {
            map.put("attach",payRequest.getRemark());
        }
        map.put("clientip",payRequest.getCreateIp());
        map.put("version","1.0");
        map.put("notifyurl",payRequest.getOutNotifyUrl());

        //paytype+mchid+amount+out_tradeid+time+商户密钥
        String sign = DigestUtils.md5Hex(map.get("paytype")+mchid+map.get("amount")+map.get("out_tradeid")+map.get("time")+cipherCode);
        map.put("sign",sign);

        Map<String,Object> result = whpClient.unifiedorder(map);

        PayRequestBack payRequestBack = new PayRequestBack();
        payRequestBack.setMchId(payRequest.getMchId());
        payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());
        if(MapUtils.isEmpty(result)) {
            //failed
            payRequestBack.setErrcode(String.valueOf(result.get("code")));
            payRequestBack.setMessage(String.valueOf(result.get("message")));
            payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
            return;
        }

        int code = new BigDecimal(result.get("code").toString()).intValue();
        if(code == 1) {
            payRequestBack.setErrcode(String.valueOf(code));
            payRequestBack.setMessage(String.valueOf(result.get("message")));
            payRequestBack.setPageContent(String.valueOf(result.get("type")));
            payRequestBack.setCodeUrl(String.valueOf(result.get("pay_info")));
            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            payService.remoteSuccess(payRequest,payRequestBack);
            return;
        }

        payRequestBack.setErrcode(String.valueOf(result.get("code")));
        payRequestBack.setMessage(String.valueOf(result.get("message")));
        payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
        return;
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        String version = map.get("version");
        String sign = map.get("sign");
        String pay_result = map.get("pay_result");
        String tradeid = map.get("tradeid");
        String out_tradeid = map.get("out_tradeid");
        String amount = map.get("amount");
        String attach = map.get("attach");
        String time = map.get("time");
        String out_transactionid = map.get("out_transactionid");

        PayRequest payRequest = payRequestDao.selectByTradeNo(out_tradeid);
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;

        if(payRequestStatus == PayRequestStatus.OPR_SUCCESS) {
            return new Gson().toJson(com.hf.base.utils.MapUtils.buildMap("resCode","0000"));
        }

        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        if(new BigDecimal(pay_result).intValue() == 0) {
            payService.paySuccess(out_tradeid);
        } else {
            payService.payFailed(out_tradeid);
        }

        return "SUCCESS";
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }
}
