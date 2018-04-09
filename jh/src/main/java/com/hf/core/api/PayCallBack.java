package com.hf.core.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.core.biz.trade.TradeBiz;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.model.po.PayRequest;
import com.hf.core.utils.BeanContextUtils;
import com.hf.core.utils.CallBackCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

public class PayCallBack extends HttpServlet {

    protected Logger logger = LoggerFactory.getLogger(PayCallBack.class);
    private TradingBiz wwTradingBiz = BeanContextUtils.getBean("wwTradingBiz");
    private PayRequestDao payRequestDao = BeanContextUtils.getBean("payRequestDao");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("ww callback received , %s");

        String receivedData = getPostString(req);
        logger.info("ww callback received data:"+receivedData);

        Map<String,String> paramMap = new Gson().fromJson(receivedData,new TypeToken<Map<String,String>>(){}.getType());

        if(null != paramMap.get("orderNum")) {
            CallBackCache.noticedList.add(String.valueOf(paramMap.get("orderNum")));
        }

        logger.info("ww callback param data:"+new Gson().toJson(paramMap));

        String result = wwTradingBiz.handleCallBack(paramMap);
        String tradeNo = paramMap.get("orderNum");
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        wwTradingBiz.notice(payRequest);

        resp.getWriter().write(result);
    }

    private String getPostString(HttpServletRequest request) {

        StringBuffer buffer = new StringBuffer();
        try {
            InputStream inputStream = request.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            // SysLogger.error(HttpUtil.class, "请求字符集不支持，获取请求数据出错");
            e.printStackTrace();
        } catch (IOException e) {
            // SysLogger.error(HttpUtil.class, "IO异常，获取请求出错");
            e.printStackTrace();
        }

        return buffer.toString();
    }
}
