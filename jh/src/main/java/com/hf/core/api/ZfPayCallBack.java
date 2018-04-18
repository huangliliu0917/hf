package com.hf.core.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.utils.Utils;
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
import java.io.IOException;
import java.util.Map;

public class ZfPayCallBack extends HttpServlet {

    protected Logger logger = LoggerFactory.getLogger(ZfPayCallBack.class);
    private TradingBiz zfTradingBiz = BeanContextUtils.getBean("zfTradingBiz");
    private PayRequestDao payRequestDao = BeanContextUtils.getBean("payRequestDao");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("zf callback received , %s");

        String receivedData = Utils.getPostString(req);
        logger.info("zf callback received data:"+receivedData);

        Map<String,String> paramMap = new Gson().fromJson(receivedData,new TypeToken<Map<String,String>>(){}.getType());

        logger.info("zf callback param data:"+new Gson().toJson(paramMap));

        String result = zfTradingBiz.handleCallBack(paramMap);
        String tradeNo = paramMap.get("inTradeOrderNo");
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        zfTradingBiz.notice(payRequest);

        resp.getWriter().write(result);
    }
}
