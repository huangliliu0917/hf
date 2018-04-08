package com.hf.core.api;

import com.hf.base.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JhTest extends HttpServlet {
    private static final String url = "http://127.0.0.1:8080/jh/pay/unifiedorder";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");

        Map<String,Object> map = new HashMap<>();

        Map<String,String[]> maps = req.getParameterMap();
        for(String key:maps.keySet()) {
            if(Objects.isNull(maps.get(key)) || maps.get(key).length==0) {
                continue;
            }
            map.put(key,maps.get(key)[0]);
        }

        map.put("sign_type","MD5");
        map.put("nonce_str",getRandomString(8));
        String cipherCode = map.get("cipherCode").toString();
        map.remove("cipherCode");
        String sign = encrypt(map,cipherCode);
        map.put("sign",sign);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.postForEntity("http://127.0.0.1:8080/jh/pay/unifiedorder",map,String.class, new Object[0]);

        resp.getWriter().write("SUCCESS"+","+entity.getBody());
    }

    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < length; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }

    public static String encrypt(Map<String,Object> map,String cipherCode) {
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
