package com.hf.core.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JhTest extends HttpServlet {
    private static final String url = "http://127.0.0.1:8080/jh/pay/unifiedorder";
    protected Logger logger = LoggerFactory.getLogger(JhTest.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");
        logger.info("hfb received");
        logger.info("hfb receive:"+req.getParameter("rtnCode"));
        logger.info("hfb receive:"+req.getParameter("rtnMsg"));
        logger.info("hfb receive:"+req.getParameter("tranFlow"));
        resp.getWriter().write("SUCCESS");
    }
}
