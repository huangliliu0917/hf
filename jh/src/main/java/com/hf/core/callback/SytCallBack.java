package com.hf.core.callback;

import com.alibaba.fastjson.JSONObject;
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

public class SytCallBack extends HttpServlet {

    protected Logger logger = LoggerFactory.getLogger(SandCallBack.class);
    private TradingBiz sytTradingBiz = BeanContextUtils.getBean("sytTradingBiz");
    private PayRequestDao payRequestDao = BeanContextUtils.getBean("payRequestDao");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("syt callback received , %s");

        String receivedData = Utils.getPostString(req);
        logger.info("syt callback received data:"+receivedData);

        String md5Msg = receivedData.split("\\|")[0];
        String dataMsg = receivedData.split("\\|")[1];

        Map<String,String> paramMap = new HashMap<>();
        JSONObject dataObj = JSONObject.parseObject(dataMsg);
        paramMap.put("code",dataObj.getString("code"));
        paramMap.put("message",dataObj.getString("message"));
        JSONObject orderObj = dataObj.getJSONObject("data");
        String orderId = orderObj.getString("orderId");
        paramMap.put("orderId",orderId);
        paramMap.put("orderAmount",dataObj.getString("orderAmount"));
        paramMap.put("dateTime",dataObj.getString("dateTime"));

        logger.info("syt callback param data:"+new Gson().toJson(paramMap));

        String result = sytTradingBiz.handleCallBack(paramMap);
        PayRequest payRequest = payRequestDao.selectByTradeNo(orderId);
        sytTradingBiz.notice(payRequest);

        resp.getWriter().write(result);
    }
}
