package com.hf.core.biz.trade;

import cn.com.sandpay.cashier.sdk.util.CertUtil;
import cn.com.sandpay.cashier.sdk.util.CryptoUtil;
import cn.com.sandpay.cashier.sdk.util.SDKUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hf.base.enums.ChannelProvider;
import com.hf.core.model.po.PayRequest;
import com.hf.core.utils.HttpRequestUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class SandTradingBiz extends AbstractTradingBiz {

    private static final String SD_REQUEST_URL = "https://cashier.sandpay.com.cn/fastPay/quickPay/index";

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.SAND;
    }

    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
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
        body.put("totalAmount", payRequest.getTotalFee());
        body.put("body", payRequest.getBody());
        body.put("subject", payRequest.getBody());

        JSONObject data = new JSONObject();
        data.put("head", head);
        data.put("body", body);

        try {
            String reqSign = URLEncoder.encode(new String(
                    Base64.encodeBase64(CryptoUtil.digitalSign(JSON.toJSONString(data).getBytes("UTF-8"),
                            CertUtil.getPrivateKey(), "SHA1WithRSA"))), "UTF-8");
            JSONObject r = new JSONObject();
            r.put("data", JSON.toJSONString(data));
            r.put("sign", reqSign);// 签名串

            // 模拟请求
            logger.info("请求报文------>request：" + r.toString());
            String responseMsg = HttpRequestUtil.sendPost(SD_REQUEST_URL, r.toString());
            // 将响应报文解码
            try {
                String result = URLDecoder.decode(responseMsg, "UTF-8");
                // 响应结果打印
                logger.info("响应报文------------>response:" + result);
                // 验证签名(需要先URL解码才能转换成为MAP)
                Map respMap = SDKUtil.convertResultStringToMap(result);
                String respData = (String) respMap.get("data");
                String respSign = (String) respMap.get("sign");
                try {
                    boolean valid = CryptoUtil.verifyDigitalSign(respData.getBytes("UTF-8"), Base64.decodeBase64(respSign), CertUtil.getPublicKey(), "SHA1WithRSA");
                    if (!valid) {
                        logger.error("响应验证杉德签名------------>失败！");
                        throw new RuntimeException("verify sign fail.");
                    } else {
                        logger.error("响应验证杉德签名------------>成功！");
                    }
                } catch (Exception e) {
                    logger.error("响应验证杉德签名------------>失败！", e);
                }
            } catch (Exception e) {
                logger.error("decode失败",e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
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
        return null;
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }
}
