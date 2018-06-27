package com.hf.core.biz.trade;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.core.dao.remote.SytClient;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.PayRequestBack;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class SytTradingBiz extends AbstractTradingBiz {
    @Autowired
    private SytClient sytClient;

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.SYT;
    }

    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        String payType = "syt";
        String partner = "120180627000770000000574630661";
        String key = "7551F179052E5A333C2D6BE25F177E75";
        String orderId = payRequest.getOutTradeNo();
        String productName = payRequest.getBody();
        String orderAmount = String.valueOf(new BigDecimal(payRequest.getTotalFee()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        String version = "1.0";
        String signType = "MD5";
        String payMethod = "22";
        String notifyUrl = "http://huifufu.cn/openapi/syt/pay_notice";

        Map<String,Object> params = new HashMap<>();
        params.put("payType",payType);
        params.put("partner",partner);
        params.put("orderId",orderId);
        params.put("productName",productName);
        params.put("orderAmount",orderAmount);
        params.put("version",version);
        params.put("signType",signType);
        params.put("payMethod",payMethod);
        params.put("notifyUrl",notifyUrl);

        String signStrTemplate = "orderAmount=%s&orderId=%s&partner=%s&payMethod=%s&payType=%s&signType=MD5&version=1.0%s";
        String signStr = String.format(signStrTemplate,orderAmount,orderId,partner,payMethod,payType,key);
        String sign = DigestUtils.md5Hex(signStr).toUpperCase();
        params.put("sign",sign);

        PayRequestBack payRequestBack = new PayRequestBack();
        payRequestBack.setMchId(payRequest.getMchId());
        payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());

        try {
            Map<String,Object> result = sytClient.unifiedorder(params);
            response.getWriter().write(String.valueOf(result.get("data")));

            payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);
            payRequestBack.setMessage("下单成功");

            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            payService.remoteSuccess(payRequest,payRequestBack);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        payRequestBack.setErrcode(CodeManager.PAY_FAILED);
        payRequestBack.setMessage("下单失败");
        payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);

    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        String code = map.get("code");
        String message = map.get("message");
        String tradeNo = map.get("orderId");

        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;

        if(payRequestStatus == PayRequestStatus.OPR_SUCCESS) {
            return new Gson().toJson(MapUtils.buildMap("resCode","0000"));
        }

        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        if(StringUtils.equals("000000",code)) {
            payService.paySuccess(tradeNo);
        } else {
            payService.payFailed(tradeNo);
        }

        return "SUCCESS";
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }
}
