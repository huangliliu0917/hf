package com.hf.user.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.DefaultClient;
import com.hf.base.enums.ChannelCode;
import com.hf.base.model.UserGroup;
import com.hf.base.utils.Utils;
import com.hf.user.client.PayClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
public class PayController {
    @Autowired
    private DefaultClient client;
    @Autowired
    private PayClient payClient;

    @RequestMapping(value = "/we_sm_pay",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> weSmPay(HttpServletRequest request) {
        BigDecimal total = new BigDecimal(request.getParameter("total")).multiply(new BigDecimal("100"));
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());

        UserGroup userGroup = client.getUserGroupById(groupId);
        Map<String,Object> payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service","02");
        payParams.put("merchant_no",userGroup.getGroupNo());
        payParams.put("total",String.valueOf(total.intValue()));
        payParams.put("name","转账"+request.getParameter("total"));
        payParams.put("remark",payParams.get("name"));
        payParams.put("out_trade_no",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        payParams.put("create_ip","127.0.0.1");
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("sign_type","MD5");
        String sign = Utils.encrypt(payParams,userGroup.getCipherCode());
        payParams.put("sign",sign);

        String result = payClient.doPay(payParams);
        Map<String,Object> resultMap = new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
        return resultMap;
    }

    @RequestMapping(value = "/wy_pay",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> wyPay(HttpServletRequest request) {
        BigDecimal total = new BigDecimal(request.getParameter("total")).multiply(new BigDecimal("100"));
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        String remark = request.getParameter("remark");
        String bankCode = request.getParameter("bank");

        UserGroup userGroup = client.getUserGroupById(groupId);
        Map<String,Object> payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service","09");
        payParams.put("merchant_no",userGroup.getGroupNo());
        payParams.put("total",String.valueOf(total.intValue()));
        payParams.put("name","转账"+request.getParameter("total"));
        payParams.put("remark", StringUtils.isEmpty(remark)?payParams.get("name"):remark);
        payParams.put("out_trade_no",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        payParams.put("create_ip","127.0.0.1");
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("sign_type","MD5");
        payParams.put("bank_code",bankCode);
        String sign = Utils.encrypt(payParams,userGroup.getCipherCode());
        payParams.put("sign",sign);

        String result = payClient.doPay(payParams);
        Map<String,Object> resultMap = new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
        return resultMap;
    }

    @RequestMapping(value = "/h5_pay",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> h5Pay(HttpServletRequest request) {
        BigDecimal total = new BigDecimal(request.getParameter("total")).multiply(new BigDecimal("100"));
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        String remark = request.getParameter("remark");
        String payType = request.getParameter("payType");
        String service = payType.equals("1")? ChannelCode.WX_H5.getCode():ChannelCode.QQ_H5.getCode();

        UserGroup userGroup = client.getUserGroupById(groupId);
        Map<String,Object> payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service",service);
        payParams.put("merchant_no",userGroup.getGroupNo());
        payParams.put("total",String.valueOf(total.intValue()));
        payParams.put("name","转账"+request.getParameter("total"));
        payParams.put("remark", StringUtils.isEmpty(remark)?payParams.get("name"):remark);
        payParams.put("out_trade_no",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        payParams.put("create_ip","127.0.0.1");
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("sign_type","MD5");
        String sign = Utils.encrypt(payParams,userGroup.getCipherCode());
        payParams.put("sign",sign);

        String result = payClient.doPay(payParams);
        return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
    }
}
