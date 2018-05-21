package com.hf.core.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.utils.Utils;
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

public class WhpCallBack extends HttpServlet {
    protected Logger logger = LoggerFactory.getLogger(WhpCallBack.class);
    private TradingBiz whpTradingBiz = BeanContextUtils.getBean("whpTradingBiz");
    private PayRequestDao payRequestDao = BeanContextUtils.getBean("payRequestDao");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("whp callback received , %s");

        String receivedData = Utils.getPostString(req);
        logger.info("whp callback received data:"+receivedData);

        Map<String,String> paramMap = new Gson().fromJson(receivedData,new TypeToken<Map<String,String>>(){}.getType());

        logger.info("whp callback param data:"+new Gson().toJson(paramMap));

        String result = whpTradingBiz.handleCallBack(paramMap);
        String tradeNo = paramMap.get("out_tradeid");
        logger.info("Start notice call back:"+tradeNo);
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        whpTradingBiz.notice(payRequest);
        resp.getWriter().write(result);
    }
}
