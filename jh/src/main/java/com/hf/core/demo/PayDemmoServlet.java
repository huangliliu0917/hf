package com.hf.core.demo;

import com.hf.base.utils.HttpClient;
import com.hf.base.utils.ParameterRequestWrapper;
import com.hf.base.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PayDemmoServlet extends HttpServlet {
    String[] fields = {"version","service","merchant_no","total","name","remark","out_trade_no","out_notify_url","create_ip",
    "sub_openid","buyer_id","authcode","bank_code","nonce_str","sign_type"};

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");

        Map<String,Object> map = new HashMap<>();
        for(String field:fields) {
            String value = req.getParameter(field);
            map.put(field,value);
        }
        String sign = this.encrypt(map,req.getParameter("cipherCode"));
        map.put("sign",sign);
        HttpClient httpClient = new HttpClient(resp);
        httpClient.setParameter(map);
        httpClient.sendByPost("http://huifufu.cn/openapi/unifiedorder_2");
    }

    private String encrypt(Map<String,Object> map,String cipherCode) {
        Set<String> set = map.keySet().parallelStream().collect(Collectors.toCollection(TreeSet::new));
        StringBuilder str = new StringBuilder("");
        for(String key:set) {
            if(StringUtils.equalsIgnoreCase("sign",key)) {
                continue;
            }
            if(Objects.isNull(map.get(key)) || Utils.isEmpty(String.valueOf(map.get(key)))) {
                continue;
            }
            str = str.append(String.format("%s=%s",key,map.get(key)));
            str = str.append("&");
        }
        str = str.append("key=").append(cipherCode);
        return DigestUtils.md5Hex(str.toString()).toUpperCase();
    }
}
