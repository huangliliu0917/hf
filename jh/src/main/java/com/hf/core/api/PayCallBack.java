package com.hf.core.api;

import com.hf.core.biz.trade.TradeBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.model.po.PayRequest;
import com.hf.core.utils.BeanContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PayCallBack extends HttpServlet {

    private TradeBiz wwTradeBiz = BeanContextUtils.getBean("wwTradeBiz");
    private PayRequestDao payRequestDao = BeanContextUtils.getBean("payRequestDao");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");

        Map<String,String[]> paramMap = req.getParameterMap();
        Map<String,String> params = new HashMap<>();
        paramMap.keySet().forEach(s -> params.put(s,paramMap.get(s)[0]));
        String result = wwTradeBiz.handleCallBack(params);
        String tradeNo = params.get("orderNum");
        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        wwTradeBiz.notice(payRequest);

        resp.getWriter().write(result);
    }
}
