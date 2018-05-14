package com.hf.core.api;

import com.google.gson.Gson;
import com.hf.base.utils.MapUtils;
import com.hf.core.biz.service.TradeBizFactory;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.utils.BeanContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class PayServlet extends HttpServlet {

    private TradeBizFactory tradeBizFactory = BeanContextUtils.getBean("tradeBizFactory");
    protected Logger logger = LoggerFactory.getLogger(PayServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");

        String version = request.getParameter("version");
        String service = request.getParameter("service");
        String merchant_no = request.getParameter("merchant_no");
        String total = request.getParameter("total");
        String name = request.getParameter("name");
        String remark = request.getParameter("remark");
        String out_trade_no = request.getParameter("out_trade_no");
        String out_notify_url = request.getParameter("out_notify_url");
        String create_ip = request.getParameter("create_ip");
        String sub_openid = request.getParameter("sub_openid");
        String buyer_id = request.getParameter("buyer_id");
        String authcode = request.getParameter("authcode");
        String bank_code = request.getParameter("bank_code");
        String nonce_str = request.getParameter("nonce_str");
        String sign_type = request.getParameter("sign_type");
        String sign = request.getParameter("sign");
        String front_url = request.getParameter("front_url");

        TradingBiz tradingBiz = tradeBizFactory.getTradingBiz(merchant_no,service);
        logger.info("tradingBiz:"+tradingBiz.getClass().getName());

        Map<String,Object> params = MapUtils.buildMap("version",version,
                "service",service,
                "merchant_no",merchant_no,
                "total",total,
                "name",name,
                "remark",remark,
                "out_trade_no",out_trade_no,
                "out_notify_url",out_notify_url,
                "create_ip",create_ip,
                "sub_openid",sub_openid,
                "buyer_id",buyer_id,
                "authcode",authcode,
                "bank_code",bank_code,
                "nonce_str",nonce_str,
                "sign_type",sign_type,
                "sign",sign,
                "front_url",front_url);

        logger.info("pay params:"+new Gson().toJson(params));

        Map<String,Object> resultMap = tradingBiz.pay(params,request,response);
        response.getWriter().write(new Gson().toJson(resultMap));
    }
}
