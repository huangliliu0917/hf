package com.hf.core.callback;

import com.google.gson.Gson;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ZfbCallBack  extends HttpServlet {
    protected Logger logger = LoggerFactory.getLogger(ZfbCallBack.class);
    private TradingBiz zfbTradingBiz = BeanContextUtils.getBean("zfbTradingBiz");
    private PayRequestDao payRequestDao = BeanContextUtils.getBean("payRequestDao");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Enumeration<String> params = req.getParameterNames();
        Map<String,String> paramMap = new HashMap<>();
        while (params.hasMoreElements()) {
            String key = params.nextElement();
            paramMap.put(key,String.valueOf(req.getParameter(key)));
        }

        logger.info("zfb qr callback param data:"+new Gson().toJson(paramMap));

        String outTradeNo = paramMap.get("orderid");
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        paramMap.put("service",payRequest.getService());

        String result = zfbTradingBiz.handleCallBack(paramMap);
        logger.info("Start notice call back:"+outTradeNo);
        payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        zfbTradingBiz.notice(payRequest);
        resp.getWriter().write(result);
    }
}
