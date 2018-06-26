package com.hf.core.biz.trade;

import cn.com.sandpay.cashier.sdk.util.CertUtil;
import cn.com.sandpay.cashier.sdk.util.CryptoUtil;
import cn.com.sandpay.cashier.sdk.util.SDKUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.HttpClient;
import com.hf.base.utils.MapUtils;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.PayRequestBack;
import com.hf.core.utils.HttpRequestUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SandTradingBiz extends AbstractTradingBiz {

    private static final String SD_REQUEST_URL = "https://cashier.sandpay.com.cn/fastPay/quickPay/index";
    private static final String AMOUNT_TEMPLATE = "00000000000";

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.SAND;
    }

    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        PayRequestBack payRequestBack = new PayRequestBack();
        payRequestBack.setMchId(payRequest.getMchId());
        payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());
        try {
            JSONObject head = new JSONObject();
            head.put("version", "1.0");
            head.put("method", "sandPay.fastPay.quickPay.index");
            head.put("productId", "00000016");
            head.put("accessType", "1");
            head.put("mid", "18781666");
            head.put("channelType", "07"); //08 移动端
            String reqTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            head.put("reqTime",reqTime);

            JSONObject body = new JSONObject();
            body.put("userId", payRequest.getBuyerId());
            body.put("currencyCode", "156");
            body.put("frontUrl", payRequest.getFrontUrl());
            body.put("notifyUrl", "http://huifufu.cn/openapi/sand/pay_notice");
            body.put("orderCode", payRequest.getOutTradeNo());
            body.put("orderTime", reqTime);
            String totalAmount = AMOUNT_TEMPLATE.substring(0,AMOUNT_TEMPLATE.length()-payRequest.getTotalFee().toString().length()+1)+payRequest.getTotalFee().toString();
            body.put("totalAmount", totalAmount);
            body.put("body", payRequest.getBody());
            body.put("subject", payRequest.getBody());

            JSONObject data = new JSONObject();
            data.put("head", head);
            data.put("body", body);

            String reqSign = URLEncoder.encode(new String(
                    Base64.encodeBase64(CryptoUtil.digitalSign(JSON.toJSONString(data).getBytes("UTF-8"),
                            CertUtil.getPrivateKey(), "SHA1WithRSA"))), "UTF-8");
            Map<String,Object> r = new HashMap<>();
            r.put("data", JSON.toJSONString(data));
            r.put("sign", reqSign);// 签名串
            r.put("charset","UTF-8");
            r.put("signType","01");

            body.put("body", URLEncoder.encode(payRequest.getBody(),"UTF-8"));
            body.put("subject", URLEncoder.encode(payRequest.getBody(),"UTF-8"));

            // 模拟请求
            logger.info("请求报文------>request：" + r.toString());
            String url = SD_REQUEST_URL+"?data="+JSON.toJSONString(data)+"&sign="+reqSign+"&charset=UTF-8&signType=01";
            response.sendRedirect(url);

            payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);
            payRequestBack.setMessage("下单成功");

            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            payService.remoteSuccess(payRequest,payRequestBack);
            return;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        payRequestBack.setErrcode(CodeManager.PAY_FAILED);
        payRequestBack.setMessage("下单失败");
        payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
    }

    private String getRequestMessage(Object o1, Object o2) {
        // 组织DATA域
        JSONObject data = new JSONObject();
        try {
            // head
            JSONObject head = new JSONObject();
            Method[] ms1 = o1.getClass().getMethods();
            for (int i = 0; i < ms1.length; i++) {
                if (ms1[i].getName().startsWith("get") && !ms1[i].getName().startsWith("getClass")) {
                    String paramName = ms1[i].getName().substring(3);
                    head.put(paramName.substring(0, 1).toLowerCase() + paramName.substring(1), ms1[i].invoke(o1, new Object[]{}));
                }
            }
            data.put("head", head);

            // body
            JSONObject body = new JSONObject();
            Method[] ms2 = o2.getClass().getMethods();
            for (int i = 0; i < ms2.length; i++) {
                if (ms2[i].getName().startsWith("get") && !ms2[i].getName().startsWith("getClass")) {
                    String paramName = ms2[i].getName().substring(3);
                    body.put(paramName.substring(0, 1).toLowerCase() + paramName.substring(1), ms2[i].invoke(o2, new Object[]{}));
                }
            }
            data.put("body", body);
            String dataParam = "data=" + data.toJSONString() + "&";

            // 组织其他域
            String charsetParam = "charset=UTF-8&";
            String signTypeParam = "signType=01&";
            String signParam = "sign=" +
                    URLEncoder.encode(
                            new String(Base64.encodeBase64(
                                    CryptoUtil.digitalSign(data.toJSONString().getBytes("UTF-8"), CertUtil.getPrivateKey(), "SHA1WithRSA")))
                    ) + "&";

            String extendParam = "extend=";

            StringBuffer resultMsg = new StringBuffer();
            resultMsg.append(charsetParam);
            resultMsg.append(dataParam);
            resultMsg.append(signTypeParam);
            resultMsg.append(signParam);
            resultMsg.append(extendParam);
            return resultMsg.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        int orderStatus = new BigDecimal(map.get("orderStatus")).intValue();
        String tradeNo = map.get("tradeNo");
        String orderCode = map.get("orderCode");

        PayRequest payRequest = payRequestDao.selectByTradeNo(orderCode);
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;

        if(payRequestStatus == PayRequestStatus.OPR_SUCCESS) {
            return new Gson().toJson(MapUtils.buildMap("resCode","0000"));
        }

        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        if(orderStatus == 1) {
            payService.paySuccess(orderCode);
        } else {
            payService.payFailed(orderCode);
        }

        return "SUCCESS";
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }
}
