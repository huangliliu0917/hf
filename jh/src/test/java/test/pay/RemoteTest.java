package test.pay;

import com.google.gson.Gson;
import com.hf.base.enums.ChannelCode;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.TradeType;
import com.hf.base.utils.EpaySignUtil;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.trade.AbstractTradingBiz;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.biz.trade.WhpTradingBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.dao.remote.CallBackClient;
import com.hf.core.dao.remote.FxtClient;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.dao.remote.YsClient;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import com.hf.core.utils.CipherUtils;
import com.hf.core.utils.Https;
import com.hfb.merchant.code.util.http.Httpz;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import test.BaseCommitTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteTest extends BaseCommitTestCase {
    @Autowired
    private YsClient ysClient;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private FxtClient fxtClient;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    @Qualifier("wwClient")
    private PayClient wwClient;
    @Autowired
    private CallBackClient callBackClient;
    @Autowired
    @Qualifier("zfTradingBiz")
    private AbstractTradingBiz zfTradingBiz;
    @Autowired
    private WhpTradingBiz whpTradingBiz;

    @Test
    public void testPay() {
        UserGroup userGroup = userGroupDao.selectByGroupNo("13588");

        Map<String,Object> payParams = new HashMap<>();
        payParams.put("create_ip","47.52.111.205");
        payParams.put("merchant_no",userGroup.getGroupNo());
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("name","测试");
        payParams.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        payParams.put("service",ChannelCode.WX_SM.getCode());
        payParams.put("remark","测试支付宝H5支付");
        payParams.put("sign_type","MD5");
        payParams.put("total","100");//10000.00
        payParams.put("version","2.0");
        payParams.put("out_notify_url","http://huifufu.cn");

        String sign = Utils.encrypt(payParams,userGroup.getCipherCode());
        payParams.put("sign",sign);

        try {
            String asynMsg = (new Httpz("utf-8", 30000, 30000)).post("http://127.0.0.1:8080/jh/pay", payParams);
            System.out.println("message:"+asynMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("----------finished-------------");
    }

    @Test
    public void testRemotePay() throws Exception {
        Map<String,Object> payParams = new HashMap<>();
        payParams.put("version","2.0");
        payParams.put("service", "02");
        payParams.put("merchant_no","13588");
        payParams.put("total","1000");//10000.00
        payParams.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        payParams.put("create_ip","47.52.111.205");
        payParams.put("remark","会员服务客服QQ183508495");
        payParams.put("nonce_str", "20180415124815");
        payParams.put("sign_type","MD5");
        payParams.put("out_notify_url","http://baidu.com/pay/notifyUrlhuifu/");
        String cipherCode = "XyEe2dK7QmRFDFsJeRAZmwfHXBzziNmk";
        String sign = Utils.encrypt(payParams,cipherCode);
        payParams.put("sign",sign);

//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> entity = restTemplate.postForEntity("http://127.0.0.1:8080/jh/pay",payParams,String.class, new Object[0]);
//        ResponseEntity<String> entity = restTemplate.postForEntity("http://huifufu.cn/openapi/unifiedorder",payParams,String.class, new Object[0]);
        String asynMsg = (new com.hfb.merchant.quick.util.http.Httpz("utf-8", 30000, 30000)).post("http://127.0.0.1:8080/jh/pay", payParams);
        System.out.println(asynMsg);
    }

    @Test
    public void testSign() {
        Map<String,Object> map = new HashMap<>();
        map.put("out_trade_no","5139_20180102164506104");
        map.put("service","pay.weixin.jspay");
        map.put("total_fee","1000000");
        map.put("body","转账10000");
        map.put("mch_id","102555074371");
        map.put("sub_openid","ojoKev39p15cuREQKbNnRSmjN9EY");

        String sign = Utils.encrypt2(map,"d4653889e27b45fb51bae4eb427c1a92");
        Assert.assertEquals("15452C3B22FFBFF177254024731B8850",sign);
    }

    @Test
    public void testQueryOrder() {
        String outTradeNo = "13588_20180209173021";
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
    }

    @Test
    public void doQuery() {
        Map<String,Object> params = new HashMap<>();
        params.put("version","1.0");
        params.put("merchant_no","212000912");
        params.put("out_trade_no","5166_app213110100002201745420");
        params.put("nonce_str",Utils.getRandomString(10));
        params.put("sign_type","MD5");
        String sign = Utils.encrypt(params,"38ntxf73xznze26bmnr1uw3er94rce8t");
        params.put("sign",sign);
        Map<String,Object> result = fxtClient.orderinfo(params);
        System.out.println(new Gson().toJson(result));
    }

    @Test
    public void testWWH5Pay() {

    }

    @Test
    public void testQuery() {
        String ids = "5156_20180315134413,5156_20180401212034,5156_20180401212048,5156_1234542156235216,5156_20180404085452,5156_20180404163810,5156_20180404163823,5156_20180404163856,5156_20180404163907,5156_20180408144630920644,5156_20180408144729384307,5156_20180408150859248197,5156_20180408150937502157,5156_20180408151041575900,5156_20180408153120389766,5156_20180408153740231812,5156_20180408154013117566,5156_20180408154229526371,5156_20180408155940541277,5156_20180408160047303764,5156_20180408160125757022,5156_20180408160203157468,5156_20180408160214248636,5156_20180408160403834911,5156_20180408160554947518,5156_20180408162412266285,5156_20180408163527873721,5156_20180408171453379842,5156_20180408171606354955,5156_20180408171726649421,5156_20180408171756169466";
        String[] idsarray = ids.split(",");
        System.out.println(idsarray.length);
        List<String> results = new ArrayList<>();
        for(String id:idsarray) {
            Map<String,Object> params = new HashMap<>();
            params.put("memberCode","9010000025");
            params.put("orderNum",id);
            String signUrl = Utils.getEncryptStr(params);
            String signStr = EpaySignUtil.sign(CipherUtils.private_key,signUrl);
            params.put("signStr",signStr);
            Map<String,Object> result = wwClient.orderinfo(params);
            if(StringUtils.equals(String.valueOf(result.get("oriRespCode")),"000000")) {
                results.add(id);
            }
        }
        System.out.println(new Gson().toJson(results));
//        System.out.println(new Gson().toJson(result));
    }

    @Test
    public void testQueryww() {
        Map<String,Object> params = new HashMap<>();
        params.put("memberCode","9010000025");
        params.put("orderNum","5166_app213110100002201745420");
        String signUrl = Utils.getEncryptStr(params);
        String signStr = EpaySignUtil.sign(CipherUtils.private_key,signUrl);
        params.put("signStr",signStr);
        Map<String,Object> result = wwClient.orderinfo(params);
        System.out.println(new Gson().toJson(result));
    }

    @Test
    public void testNotice() {
        Map<String,Object> resutMap = new HashMap<>();
        resutMap.put("errcode","0");
        //msg
        resutMap.put("message","支付成功");

        resutMap.put("no","85629");
        //out_trade_no
        resutMap.put("out_trade_no","S1805101143425185");
        //mch_id
        resutMap.put("merchant_no","5187");
        //total
        resutMap.put("total","1000");
        //fee
        resutMap.put("fee","33");
        //trade_type 1:收单 2:撤销 3:退款
        resutMap.put("trade_type","1");
        //sign_type
        resutMap.put("sign_type","MD5");

        String key = "js05fv1u";
        String sign = Utils.encrypt(resutMap,key);
        resutMap.put("sign",sign);

        boolean result = callBackClient.post("http://www.k3hui.com/index.hfbybtzRecord.do",resutMap);

        System.out.println(result);
    }

    @Test
    public void testSendForm() {
        Https https = new Https();
//        String url = "http://pay1.hlqlb.cn:8692/pay/payment/toPayment";
        String url = "http://127.0.0.1:8080/jh/user/testApp";

        String memberCode = "9010000025";
        String payMoney = "110.00";
        String orderNum = String.valueOf(RandomUtils.nextLong());
        String callbackUrl = "http://huifufu.cn/openapi/ww/pay_notice";
        String goodsName = "会员服务客服QQ183508495";
        Map<String,Object> params = MapUtils.buildMap("memberCode",memberCode,"payMoney",payMoney,"orderNum",orderNum,"callbackUrl",callbackUrl,"goodsName",goodsName);
        String sign = EpaySignUtil.sign(CipherUtils.private_key, Utils.getEncryptStr(params));

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        NameValuePair memberCodePair = new NameValuePair("memberCode",memberCode);
        nameValuePairs.add(memberCodePair);

        NameValuePair payMoneyPair = new NameValuePair("payMoney","110.00");
        nameValuePairs.add(payMoneyPair);

        NameValuePair orderNumPair = new NameValuePair("orderNum",orderNum);
        nameValuePairs.add(orderNumPair);

        NameValuePair callbackUrlPair = new NameValuePair("callbackUrl",callbackUrl);
        nameValuePairs.add(callbackUrlPair);

        NameValuePair goodsNamePair = new NameValuePair("goodsName",goodsName);
        nameValuePairs.add(goodsNamePair);

        NameValuePair signStrPair = new NameValuePair("signStr",sign);
        nameValuePairs.add(signStrPair);

        NameValuePair[] nameValuePairArray = new NameValuePair[nameValuePairs.size()];
        String response = https.sendAsPost(url,nameValuePairs.toArray(nameValuePairArray));
        System.out.println(response);
    }

    @Test
    public void testZfPay() {
        PayRequest payRequest = new PayRequest();
        payRequest.setAppid("");
        payRequest.setBankCode("中国工商银行");
        payRequest.setBody("转账100");
        payRequest.setBuyerId("12345");
        payRequest.setChannelProviderCode(ChannelProvider.ZF.getCode());
        payRequest.setCreateIp("47.52.111.205");
        payRequest.setMchId("13588");
        payRequest.setOutNotifyUrl("http://huifufu.cn");
        payRequest.setOutTradeNo(String.valueOf(RandomUtils.nextLong()));
        payRequest.setRemark("转账100");
        payRequest.setService(ChannelCode.KJ.getCode());
        payRequest.setTotalFee(50);
        payRequest.setSubOpenid("");
        payRequest.setTradeType(TradeType.PAY.getValue());
        payRequest.setSign("");
        zfTradingBiz.doPay(payRequest,null,null);
        System.out.println(payRequest.getOutTradeNo());
    }

    @Test
    public void testGetPayInfo() {
        PayRequest payRequest = new PayRequest();
        payRequest.setOutTradeNo("5166_appjza1455551174527657");
        Map<String,Object> map = whpTradingBiz.query(payRequest);
        System.out.println(new Gson().toJson(map));
    }

}
