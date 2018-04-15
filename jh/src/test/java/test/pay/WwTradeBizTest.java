package test.pay;

import com.google.gson.Gson;
import com.hf.base.enums.OprStatus;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.utils.Utils;
import com.hf.core.api.PayApi;
import com.hf.core.biz.service.TradeBizFactory;
import com.hf.core.biz.trade.TradeBiz;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.dao.local.*;
import com.hf.core.dao.remote.WwClient;
import com.hf.core.job.pay.PayJob;
import com.hf.core.model.po.*;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import test.BaseTestCase;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WwTradeBizTest extends BaseTestCase {
    @Autowired
    private PayApi payApi;
    @Autowired
    private UserGroupDao userGroupDao;
    @Mock
    private WwClient mockWwClient;
    @Mock
    private TradeBizFactory mockTradeBizFactory;
    @Autowired
    private TradeBiz wwTradeBiz;
    @Autowired
    private TradingBiz wwTradingBiz;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private PayJob payJob;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(payApi,"tradeBizFactory",mockTradeBizFactory);
        Mockito.when(mockTradeBizFactory.getTradingBiz(Mockito.anyString(), Mockito.anyString())).thenReturn(wwTradingBiz);
        Mockito.when(mockTradeBizFactory.getTradingBiz(Mockito.anyString())).thenReturn(wwTradingBiz);
        ReflectionTestUtils.setField(wwTradeBiz,"wwClient",mockWwClient);
    }

    @Test
    public void testPay() {
        String groupNo = "13588";
        UserGroup userGroup = userGroupDao.selectByGroupNo(groupNo);
        String cipherCode = userGroup.getCipherCode();
        String merchantNo = userGroup.getGroupNo();
        String outTradeNo = String.valueOf(RandomUtils.nextLong());

        Map<String,Object> successResultMap = new HashMap<>();
        successResultMap.put("payResult","<html>\n" +
                "<script type='text/html' style='display:block'>\n" +
                "    <input type=\"text\" />\n" +
                "</scipt>\n" +
                "\n" +
                "<head>\n" +
                "    <title>订单支付</title>\n" +
                "    <script src=\"/pay/js/jquery-2.2.3.min.js\"></script>\n" +
                "</head>\n" +
                "<body>\u2028\n" +
                "<form id=\"form\" action=\"https://newpay.ips.com.cn/psfp-entry/gateway/payment.do\" method=\"post\">\n" +
                "    <input name=\"pGateWayReq\" type=\"hidden\" value=\"<Ips><GateWayReq><head><Version>v1.0.0</Version><MerCode>203951</MerCode><MerName>海南侨乐邦网络科技有限公司</MerName><Account>2039510017</Account><MsgId>msg20180209113746</MsgId><ReqDate>20180209113746</ReqDate><Signature>0b1f992ba1ac78579b586f051b4576d5</Signature></head><body><MerBillNo>20180209113746036690</MerBillNo><Lang>GB</Lang><Amount>11.00</Amount><Date>20180209</Date><CurrencyType>156</CurrencyType><GatewayType>02</GatewayType><Merchanturl>http://pay1.hlqlb.cn:8692/pay/payment/result</Merchanturl><FailUrl><![CDATA[]]></FailUrl><Attach><![CDATA[]]></Attach><OrderEncodeType>5</OrderEncodeType><RetEncodeType>17</RetEncodeType><RetType>1</RetType><ServerUrl><![CDATA[http://47.97.175.195:8682/posp/bankPay/hxPayNotify]]></ServerUrl><BillEXP>24</BillEXP><GoodsName>湖南慧付 收款</GoodsName><IsCredit></IsCredit><BankCode></BankCode><ProductType></ProductType></body></GateWayReq></Ips>\" />\n" +
                "</form>\u2028\n" +
                "<script type=\"text/javascript\">\n" +
                "    $(document).ready(function(){\n" +
                "        $(\"#form\").submit();\n" +
                "    });\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>");
        String tradeNo = merchantNo+"_"+outTradeNo;
        successResultMap.put("outTradeNo",tradeNo);
        Mockito.when(mockWwClient.unifiedorder(Mockito.anyMap())).thenReturn(successResultMap);

        int total = 1000*100;
        Map<String,Object> payMap = new HashMap<>();
        payMap.put("version","1.0");
        payMap.put("service","09");
        payMap.put("merchant_no",merchantNo);
        payMap.put("total",total);
        payMap.put("name","转账1000元");
        payMap.put("remark","转账1000元");
        payMap.put("out_trade_no", outTradeNo);
        payMap.put("create_ip","127.0.0.1");
        payMap.put("nonce_str", Utils.getRandomString(12));
        payMap.put("sign_type","MD5");
        String sign = Utils.encrypt(payMap,cipherCode);
        payMap.put("sign",sign);
        payApi.unifiedorder(payMap,null);

        PayRequest payRequest = payRequestDao.selectByTradeNo(tradeNo);
        Assert.assertNotNull(payRequest);
        Assert.assertEquals(payRequest.getStatus().intValue(), PayRequestStatus.PROCESSING.getValue());

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(userGroup.getCompanyId());
        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(tradeNo);
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(), OprStatus.NEW.getValue());

        Account account = accountDao.selectByGroupId(userGroup.getCompanyId());
        Account userAccount = accountDao.selectByGroupId(userGroup.getId());

        List<AccountOprLog> logs = accountOprLogDao.selectByTradeNo(tradeNo);
        for(AccountOprLog log:logs) {
            Assert.assertEquals(log.getStatus().intValue(),OprStatus.NEW.getValue());
        }

        Map<String,String> callbackMap = new HashMap<>();
        callbackMap.put("memberCode",merchantNo);
        callbackMap.put("orderNum",tradeNo);
        callbackMap.put("respType","2");
        callbackMap.put("resultCode","0000");
        callbackMap.put("payNum",String.valueOf(RandomUtils.nextLong()));
        callbackMap.put("payType","1");
        callbackMap.put("payMoney","1000");
        callbackMap.put("payTime","201802102150");
        callbackMap.put("platformType","1");
        callbackMap.put("interfaceType","1");
        callbackMap.put("resultMsg","支付成功");
        callbackMap.put("signStr","12542162adsfds");
        payApi.wwCallBack(callbackMap);

        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.OPR_SUCCESS.getValue());
        Assert.assertEquals(payRequest.getPayResult(),"0");

        adminAccountOprLog = adminAccountOprLogDao.selectByNo(tradeNo);
        System.out.println("log amount:");
        System.out.println(new Gson().toJson(adminAccountOprLog));
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(),OprStatus.PAY_SUCCESS.getValue());
        logs = accountOprLogDao.selectByTradeNo(tradeNo);
        System.out.println(new Gson().toJson(logs));
        logs.stream().forEach(accountOprLog -> Assert.assertEquals(accountOprLog.getStatus().intValue(),OprStatus.PAY_SUCCESS.getValue()));

        payJob.doPromote();
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.PAY_SUCCESS.getValue());

        adminAccountOprLog = adminAccountOprLogDao.selectByNo(tradeNo);
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(),OprStatus.FINISHED.getValue());
        logs = accountOprLogDao.selectByTradeNo(tradeNo);
        logs.stream().forEach(accountOprLog -> Assert.assertEquals(accountOprLog.getStatus().intValue(),OprStatus.FINISHED.getValue()));
    }
}
