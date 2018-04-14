package test.pay;

import com.hf.base.enums.PayRequestStatus;
import com.hf.base.utils.Utils;
import com.hf.core.biz.service.PayService;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.po.AdminAccount;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
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
import java.util.Map;

public class CallBackTest extends BaseTestCase {
    @Autowired
    private TradingBiz wwTradingBiz;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Mock
    private PayClient wwPayClient;
    @Autowired
    private PayService payService;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(wwTradingBiz,"wwClient",wwPayClient);
    }

    @Test
    public void testCallBack() {
        String outTradeNo = String.valueOf(RandomUtils.nextLong());
        UserGroup userGroup = userGroupDao.selectByGroupNo("13588");

        Map<String,Object> payParams = new HashMap<>();
        payParams.put("create_ip","127.0.0.1");
        payParams.put("merchant_no",userGroup.getGroupNo());
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("name","测试");
        payParams.put("out_trade_no",outTradeNo);
        payParams.put("service","04");
        payParams.put("sign_type","MD5");
        payParams.put("total","11000");//10000.00
        payParams.put("version","1.0");

        String sign = Utils.encrypt(payParams,userGroup.getCipherCode());
        payParams.put("sign",sign);

        Map<String,Object> response = new HashMap<>();
        response.put("payResult","var url = '1232112511'");
        Mockito.when(wwPayClient.unifiedorder(Mockito.anyMap())).thenReturn(response);

        wwTradingBiz.pay(null,null);

        PayRequest payRequest = payRequestDao.selectByTradeNo("13588_"+outTradeNo);
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.PROCESSING.getValue());

        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("orderNum","13588_"+outTradeNo);
        paramMap.put("respType","2");
        paramMap.put("memberCode","12411");
        paramMap.put("payNum","1");
        paramMap.put("payType","4");
        paramMap.put("payMoney","100");
        paramMap.put("payTime","2018");
        paramMap.put("platformType","1");
        paramMap.put("interfaceType","12");
        paramMap.put("resultCode","000000");
        paramMap.put("resultMsg","12314");
        paramMap.put("signStr","12wdasdsadsa");

        String result = wwTradingBiz.handleCallBack(paramMap);
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.OPR_SUCCESS.getValue());
        Assert.assertEquals(payRequest.getNoticeStatus(),0);
        String tradeNo = paramMap.get("orderNum");
        wwTradingBiz.notice(payRequest);
        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getNoticeStatus(),1);

        payService.payPromote(payRequest.getOutTradeNo());

        payRequest = payRequestDao.selectByPrimaryKey(payRequest.getId());
        Assert.assertEquals(payRequest.getStatus().intValue(),PayRequestStatus.PAY_SUCCESS.getValue());
    }
}
