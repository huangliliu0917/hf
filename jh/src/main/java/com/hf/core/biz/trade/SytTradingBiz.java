package com.hf.core.biz.trade;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.HttpClient;
import com.hf.base.utils.MapUtils;
import com.hf.core.dao.remote.SytClient;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.PayRequestBack;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SytTradingBiz extends AbstractTradingBiz {
    @Autowired
    private SytClient sytClient;

    private static final List<Map<String,String>> partners = new ArrayList<>();

    static {
        Map<String,String> partner1 = MapUtils.buildMap("partner","120180627000770000000574630661","cipher","7551F179052E5A333C2D6BE25F177E75");
        partners.add(partner1);
        Map<String,String> partner2 = MapUtils.buildMap("partner","120180627000770000000575063241","cipher","8BA6E495AACB6EF17F319F7B49A017FD");
        partners.add(partner2);
        Map<String,String> partner3 = MapUtils.buildMap("partner","120180627000770000000574643251","cipher","F6C50638F78CE01E35E64C8595FD906B");
        partners.add(partner3);
    }

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.SYT;
    }

    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        Map<String,String> partnerMap = partners.get(RandomUtils.nextInt(0,partners.size()));
        String payType = "syt";
        String partner = partnerMap.get("partner");
        String key = partnerMap.get("cipher");
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
            HttpClient httpClient = new HttpClient(response);
            httpClient.setParameter(params);
            httpClient.sendByPost("http://qr.sytpay.cn/api/v1/create.php");
//            Map<String,Object> result = sytClient.unifiedorder(params);
//            response.getWriter().write(String.valueOf(result.get("data")));

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
