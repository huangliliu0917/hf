package test.pay;

import com.google.gson.Gson;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.OprStatus;
import com.hf.base.enums.SettleStatus;
import com.hf.base.model.AgentPayLog;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Utils;
import com.hf.core.biz.SettleBiz;
import com.hf.core.biz.service.PayService;
import com.hf.core.biz.trade.WwTradingBiz;
import com.hf.core.dao.local.*;
import com.hf.core.dao.remote.WwClient;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayTest extends BaseTestCase {
    @Autowired
    private WwTradingBiz wwTradingBiz;
    @Autowired
    private UserGroupDao userGroupDao;
    @Mock
    private WwClient mockWwClient;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private PayService payService;
    @Autowired
    private UserChannelAccountDao userChannelAccountDao;
    @Autowired
    private SettleBiz settleBiz;
    @Autowired
    private SettleTaskDao settleTaskDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;

    private static final String merchant_no = "19858";

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(wwTradingBiz,"wwClient",mockWwClient);
        Map<String,String> map = new HashMap<>();
        map.put("returnCode","0000");
        map.put("qrCode","eqwefwqdwfgasdsdfsda");
        try {
            Mockito.when(mockWwClient.unifiedorder(Mockito.anyMap(),Mockito.any())).thenReturn(new Gson().toJson(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPay() {

        String total = "1000";
        String outTradeNo = String.valueOf(RandomUtils.nextLong());

        Map<String,Object> params = MapUtils.buildMap(
                "version","2.0",
                "service","07",
                "merchant_no",merchant_no,
                "total",total,
                "name","test",
                "remark","test",
                "out_trade_no",outTradeNo,
                "out_notify_url","http://www.baidu.com",
                "create_ip","127.0.0.1",
                "nonce_str", Utils.getRandomString(12),
                "sign_type","MD5");

        UserGroup userGroup = userGroupDao.selectByGroupNo(merchant_no);
        String sign = Utils.encrypt(params,userGroup.getCipherCode());

        params.put("sign",sign);

        Map<String,Object> resultMap = wwTradingBiz.pay(params);
        //"memberCode","orderNum","payNum","payType","payMoney","payTime","platformType","interfaceType","respType","resultCode","resultMsg","signStr"
        String orderNum = merchant_no+"_"+outTradeNo;
        String respType = "2";
        Map<String,String> callBackMap = MapUtils.buildMap("orderNum",orderNum,
                "respType",respType,
                "memberCode","123",
                "payNum","1",
                "payType","1",
                "payMoney","1000",
                "payTime","1234",
                "platformType","123",
                "interfaceType","1214",
                "resultCode","0000",
                "resultMsg","sdasf",
                "signStr","23432`1wew");
        wwTradingBiz.handleCallBack(callBackMap);

        payService.payPromote(merchant_no+"_"+outTradeNo);

        PayRequest payRequest = payRequestDao.selectByTradeNo(merchant_no+"_"+outTradeNo);
        Assert.assertEquals(payRequest.getStatus().intValue(),100);

        List<AccountOprLog> accountOprLogs = accountOprLogDao.selectByTradeNo(merchant_no+"_"+outTradeNo);

        for(AccountOprLog oprLog:accountOprLogs) {
            Assert.assertEquals(oprLog.getService(),payRequest.getService());
            Assert.assertEquals(oprLog.getProviderCode(),payRequest.getChannelProviderCode());
        }

        UserChannelAccount userChannelAccount = userChannelAccountDao.selectByUnq(userGroup.getId(), ChannelProvider.WW.getCode());
        System.out.println(userChannelAccount.getAmount());

        SettleTask settleTask = new SettleTask();
        settleTask.setGroupId(userGroup.getId());
        settleTask.setSettleAmount(new BigDecimal("975"));
        settleTask.setBank("中国建设银行");
        settleTask.setDeposit("上海支行");
        settleTask.setOwner("滕飞");
        settleTask.setBankNo("62222222222222");

        settleBiz.saveSettle(settleTask);

        Account account = accountDao.selectByGroupId(userGroup.getId());
        Assert.assertTrue(account.getLockAmount().compareTo(new BigDecimal("975"))==0);

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());

        Assert.assertEquals(settleTask.getStatus().intValue(), SettleStatus.PROCESSING.getValue());

        List<AgentPayLog> list = settleBiz.newAgentPay(String.valueOf(settleTask.getId()));
        System.out.println(new Gson().toJson(list));

        userChannelAccount = userChannelAccountDao.selectByPrimaryKey(userChannelAccount.getId());

        System.out.println( userChannelAccount.getLockAmount());

//        Mockito.when(mockWwClient.agentPay(Mockito.anyMap())).thenReturn(MapUtils.buildMap("returnCode","0000","returnMsg","成功"));
//
//        settleBiz.submitAgentPay(settleTask.getId());
//
//        List<AgentPayLog> logs = agentPayLogDao.select(MapUtils.buildMap("withDrawTaskId",settleTask.getId(),"status","0"));
//        Assert.assertTrue(logs.isEmpty());
//
//        logs = agentPayLogDao.select(MapUtils.buildMap("withDrawTaskId",settleTask.getId(),"status","1"));
//        Assert.assertTrue(!logs.isEmpty());

        Mockito.when(mockWwClient.agentPay(Mockito.anyMap())).thenReturn(MapUtils.buildMap("returnCode","0001","returnMsg","失败"));
        settleBiz.submitAgentPay(settleTask.getId());

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        Assert.assertTrue(settleTask.getLockAmount().compareTo(new BigDecimal("0"))==0);
        userChannelAccount = userChannelAccountDao.selectByPrimaryKey(userChannelAccount.getId());
        Assert.assertTrue(userChannelAccount.getLockAmount().compareTo(new BigDecimal("0"))==0);
    }
}
