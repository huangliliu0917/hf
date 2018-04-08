package test.pay;

import com.google.gson.Gson;
import com.hf.base.utils.EpaySignUtil;
import com.hf.base.utils.Utils;
import com.hf.core.biz.trade.TradeBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.dao.remote.CallBackClient;
import com.hf.core.dao.remote.FxtClient;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.dao.remote.YsClient;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import com.hf.core.utils.CipherUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import test.BaseCommitTestCase;

import java.util.HashMap;
import java.util.Map;

public class RemoteTest extends BaseCommitTestCase {
    @Autowired
    private YsClient ysClient;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private FxtClient fxtClient;
    @Autowired
    @Qualifier("wwTradeBiz")
    private TradeBiz wwTradeBiz;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    @Qualifier("wwClient")
    private PayClient wwClient;
    @Autowired
    private CallBackClient callBackClient;

    @Test
    public void testPay() {
        UserGroup userGroup = userGroupDao.selectByGroupNo("13588");

        Map<String,Object> payParams = new HashMap<>();
        payParams.put("create_ip","127.0.0.1");
        payParams.put("merchant_no",userGroup.getGroupNo());
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("name","测试");
        payParams.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        payParams.put("service","04");
        payParams.put("sign_type","MD5");
        payParams.put("total","11000");//10000.00
        payParams.put("version","1.0");
//        payParams.put("out_notify_url","http://gate.8zhongyi.com/scan/callback/trade/pay_notify_huifu");

        String sign = Utils.encrypt(payParams,userGroup.getCipherCode());
        payParams.put("sign",sign);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.postForEntity("http://127.0.0.1:8080/jh/pay/unifiedorder",payParams,String.class, new Object[0]);
        System.out.println(entity.getBody());
    }

    @Test
    public void testRemotePay() {
        Map<String,Object> payParams = new HashMap<>();
        payParams.put("version","1.0");
        payParams.put("service", "04");
        payParams.put("merchant_no","5161");
        payParams.put("total","10000");//10000.00
        payParams.put("out_trade_no","247103585651230");
        payParams.put("create_ip","192_168_2_123");
        payParams.put("nonce_str", "st1523080979677");
        payParams.put("name","H247103585651230");
        payParams.put("sign_type","MD5");
        payParams.put("out_notify_url","http://gate.8zhongyi.com/scan/callback/trade/pay_notify_huifu");
        String cipherCode = "s3r7tWx7NQ8h78zrhNeyEyFAhNT62kXB";
        String sign = Utils.encrypt(payParams,cipherCode);
        payParams.put("sign",sign);

        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> entity = restTemplate.postForEntity("http://127.0.0.1:8080/jh/pay/unifiedorder",payParams,String.class, new Object[0]);
        ResponseEntity<String> entity = restTemplate.postForEntity("http://huifufu.cn/openapi/unifiedorder",payParams,String.class, new Object[0]);
        System.out.println(entity.getBody());
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
        wwTradeBiz.handleProcessingRequest(payRequest);
    }

    @Test
    public void doQuery() {
        Map<String,Object> params = new HashMap<>();
        params.put("version","1.0");
        params.put("merchant_no","212000912");
        params.put("out_trade_no","5132_20180122133710");
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
        Map<String,Object> params = new HashMap<>();
        params.put("memberCode","9010000025");
        params.put("orderNum","5161_2565611754388059");
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

        resutMap.put("no","198");
        //out_trade_no
        resutMap.put("out_trade_no","2499897009406918");
        //mch_id
        resutMap.put("merchant_no","5161");
        //total
        resutMap.put("total","1000");
        //fee
        resutMap.put("fee","38");
        //trade_type 1:收单 2:撤销 3:退款
        resutMap.put("trade_type","1");
        //sign_type
        resutMap.put("sign_type","MD5");
        String sign = Utils.encrypt(resutMap,"s3r7tWx7NQ8h78zrhNeyEyFAhNT62kXB");
        resutMap.put("sign",sign);

        boolean result = callBackClient.post("http://gate.8zhongyi.com/scan/callback/trade/pay_notify_huifu",resutMap);

        System.out.println(result);
    }
}
