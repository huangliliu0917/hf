package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.model.RemoteParams;
import com.hf.core.biz.service.impl.PayServiceImpl;
import com.hfb.merchant.code.util.http.Httpz;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Component
public class WhpClient extends BaseClient implements PayClient {

    private static final String PAY_URL = "http://pay.weihupay.cn/payinfo.php";
    private static final String QQ_QR_PAY_URL = "http://www.82ypay.com/qqpay/trade";
    private static final String PAY_INFO_URL = "http://pay.weihupay.cn/getres.php";

    private Logger logger = LoggerFactory.getLogger(WhpClient.class);

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        try {
            String msg = new Httpz("utf-8", 30000, 30000).post(PAY_URL, params);
            Map<String,Object> resultMap = new Gson().fromJson(msg,new TypeToken<Map<String,Object>>(){}.getType());
            logger.info(params.get("out_tradeid")+": pay result"+new Gson().toJson(resultMap));
            return resultMap;
        } catch (Exception e) {
            logger.error("pay failed,"+e.getMessage()+"---"+new Gson().toJson(params));
            return null;
        }
    }

    public Map<String,Object> qqQrPay(Map<String,Object> params) {
        StringBuffer buffer = new StringBuffer();
        HttpURLConnection httpurl = null;
        String charEncoding = "utf-8";

        try {
            httpurl = createUrl(QQ_QR_PAY_URL);
            httpurl.setDoOutput(true);
            httpurl.setRequestMethod("POST");
            httpurl.setRequestProperty("Content-Type", "application/json");
            httpurl.setRequestProperty("Accept-Charset", "utf-8");
            DataOutputStream out = new DataOutputStream(httpurl.getOutputStream());
            out.write(new Gson().toJson(params).getBytes("utf-8"));
            out.flush();
            out.close();

            InputStream in = httpurl.getInputStream();
            int code = httpurl.getResponseCode();
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, charEncoding));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } else {
                logger.error("no response");
            }
        } catch (Exception e) {
            logger.error("operate failed! url is {}", QQ_QR_PAY_URL, e);
            return null;
        } finally {
            closeHttpRequest(httpurl);
        }
        String result = buffer.toString();
        return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
    }

    private void closeHttpRequest(HttpURLConnection httpReq) {
        if (httpReq != null) {
            httpReq.disconnect();
        }
    }

    private HttpURLConnection createUrl(String url) {
        URL httpurl = null;
        HttpURLConnection http = null;
        try {
            httpurl = new URL(url);
            http = (HttpURLConnection) httpurl.openConnection();
        } catch (Exception e) {
            logger.error("open connection error!");
        }
        http.setConnectTimeout(20000);
        http.setReadTimeout(20000);
        return http;
    }

    @Override
    public Map<String, Object> unifiedorder(List<Header> headers, Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> refundorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> reverseorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> orderinfo(Map<String, Object> params) {
        try {
            String url = String.format("http://pay.weihupay.cn/getres.php?mchid=%s&out_tradeid=%s",params.get("mchid"),params.get("out_tradeid"));
            RemoteParams remoteParams = new RemoteParams(url).withParams(null);
            String result = super.get(remoteParams);
            return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Map<String, Object> refundorderinfo(Map<String, Object> params) {
        return null;
    }
}
