package com.hf.core.callback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.utils.Utils;
import com.hf.core.api.WhpCallBack;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.model.po.PayRequest;
import com.hf.core.utils.BeanContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WhpQrCallBack extends HttpServlet {

    protected Logger logger = LoggerFactory.getLogger(WhpQrCallBack.class);
    private TradingBiz whpTradingBiz = BeanContextUtils.getBean("whpTradingBiz");
    private PayRequestDao payRequestDao = BeanContextUtils.getBean("payRequestDao");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("whp qr callback received , %s");

        Map<String,String[]> reqMap = req.getParameterMap();
        Map<String,String> paramMap = new HashMap<>();
        for(String key:reqMap.keySet()) {
            if(reqMap.get(key) == null) {
                continue;
            }
            paramMap.put(key,reqMap.get(key)[0]);
        }
        logger.info("whp qr callback param data:"+new Gson().toJson(paramMap));

        String outTradeNo = paramMap.get("outTradeNo");
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        paramMap.put("service",payRequest.getService());

        String result = whpTradingBiz.handleCallBack(paramMap);
        logger.info("Start notice call back:"+outTradeNo);
        payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        whpTradingBiz.notice(payRequest);
        resp.getWriter().write(result);
    }
}
