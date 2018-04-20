package test.pay;

import com.hf.base.utils.Utils;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import test.BaseCommitTestCase;
import java.util.HashMap;
import java.util.Map;

public class HfPayTest extends BaseCommitTestCase {
    @Autowired
    @Qualifier("zfTradingBiz")
    private TradingBiz hfTradingBiz;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private PayRequestDao payRequestDao;

    @Test
    public void testPay() {
        UserGroup userGroup = userGroupDao.selectByGroupNo("13588");

        Map<String,Object> payParams = new HashMap<>();
        payParams.put("create_ip","47.52.111.205");
        payParams.put("merchant_no",userGroup.getGroupNo());
        payParams.put("nonce_str", Utils.getRandomString(8));
        payParams.put("bank_code", "ICBC");
        payParams.put("name","测试");
        payParams.put("out_trade_no",String.valueOf(RandomUtils.nextLong()));
        payParams.put("service","13");
        payParams.put("sign_type","MD5");
        payParams.put("total","11000");//10000.00
        payParams.put("version","2.0");
        payParams.put("out_notify_url","www.huifufu.cn");
        payParams.put("front_url","http://122124112415");

        String sign = Utils.encrypt(payParams,userGroup.getCipherCode());
        payParams.put("sign",sign);

        hfTradingBiz.pay(payParams);

        String outTradeNo = payParams.get("merchant_no")+"_"+payParams.get("out_trade_no");
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        System.out.println(payRequest.getFrontUrl());
    }
}
