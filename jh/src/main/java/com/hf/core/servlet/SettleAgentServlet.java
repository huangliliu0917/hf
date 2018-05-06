package com.hf.core.servlet;

import com.google.gson.Gson;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.api.PayCallBack;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.dao.remote.WwClient;
import com.hf.core.job.pay.PayJob;
import com.hf.core.utils.BeanContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class SettleAgentServlet extends HttpServlet {
    protected Logger logger = LoggerFactory.getLogger(SettleAgentServlet.class);
    private WwClient wwClient = BeanContextUtils.getBean("wwClient");
    private String cipher = Utils.getRandomString(18);
    private PayJob payJob = BeanContextUtils.getBean("payJob");

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("current cipher:"+cipher);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");

        String password = req.getParameter("password");
        if(!password.equals(payJob.getPassword())) {
            resp.getWriter().write("密码错误");
            return;
        }

        String orderNum = req.getParameter("orderNum");
        String bankCode = req.getParameter("bankCode");
        String bankAccount = req.getParameter("bankAccount");
        String accountName = req.getParameter("accountName");
        String certNo = req.getParameter("certNo");
        String tel = req.getParameter("tel");
        String payMoney = req.getParameter("payMoney");

        Map<String,Object> map = MapUtils.buildMap("orderNum",orderNum,
                "bankCode",bankCode,
                "bankAccount",bankAccount,
                "accountName",accountName,
                "certNo",certNo,
                "tel",tel,
                "memberCode","9010000025",
                "payFlag","1030",
                "payMoney",payMoney);

        logger.info("settle agent pay request:"+new Gson().toJson(map));

        Map<String,Object> res = wwClient.agentPay(map);

        String returnCode = String.valueOf(res.get("returnCode"));
        String returnMsg = String.valueOf(res.get("returnMsg"));

        resp.getWriter().write(returnCode);
        resp.getWriter().write(returnMsg);
    }
}
