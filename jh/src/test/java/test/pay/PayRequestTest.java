package test.pay;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hf.base.enums.PayRequestStatus;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.model.po.PayRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.BaseTestCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PayRequestTest extends BaseTestCase {
    @Autowired
    private PayRequestDao payRequestDao;

    @Test
    public void testGetWaitingCallBack() {
        List<String> nos = new ArrayList<>();

        for(int i=0;i<1000;i++) {
            String outTradeNo = String.valueOf(RandomUtils.nextLong());
            nos.add(outTradeNo);
            PayRequest payRequest = new PayRequest();
            payRequest.setOutTradeNo(outTradeNo);
            payRequest.setOutNotifyUrl("www.baidu.com");
            payRequest.setActualAmount(new BigDecimal("1000"));
            payRequest.setStatus(PayRequestStatus.OPR_SUCCESS.getValue());
            payRequest.setBody("1234");
            payRequest.setMchId(String.valueOf(RandomUtils.nextInt()));
            payRequest.setService("04");
            payRequest.setSign(String.valueOf(RandomUtils.nextLong()));
            payRequestDao.insertSelective(payRequest);

            payRequest = payRequestDao.selectByTradeNo(outTradeNo);
            int count = payRequestDao.updateStatusById(payRequest.getId(),PayRequestStatus.OPR_SUCCESS.getValue(),PayRequestStatus.PAY_SUCCESS.getValue());
            Assert.assertTrue(count>0);
            payRequest = payRequestDao.selectByTradeNo(outTradeNo);
            Assert.assertTrue(payRequest.getReceiveTime()>0);
        }

        int count = 0;
        Long id = 0L;
        while(true) {
            List<PayRequest> payRequests = payRequestDao.selectWaitingCallBack(id);
            if(CollectionUtils.isEmpty(payRequests)) {
                break;
            }
            count+=payRequests.size();
            Assert.assertTrue(payRequests.size()<=200);
            System.out.println(new Gson().toJson(payRequests));
            id = payRequests.get(payRequests.size()-1).getId();
        }
        Assert.assertTrue(count == 1000);

        for(String no:nos) {
            PayRequest payRequest = payRequestDao.selectByTradeNo(no);
            payRequestDao.updateNoticeRetryTime(payRequest.getId());
            payRequest = payRequestDao.selectByTradeNo(no);
            Assert.assertTrue(payRequest.getNoticeRetryTime() == 1);
        }

        List<PayRequest> payRequests = payRequestDao.selectWaitingCallBack(id);
        Assert.assertTrue(payRequests.size() == 0);

        try {
            Thread.sleep(130000L);
        } catch (Exception e){
            e.printStackTrace();
        }

        count = 0;
        id = 0L;
        while(true) {
            payRequests = payRequestDao.selectWaitingCallBack(id);
            if(CollectionUtils.isEmpty(payRequests)) {
                break;
            }
            count+=payRequests.size();
            Assert.assertTrue(payRequests.size()<=200);
            System.out.println(new Gson().toJson(payRequests));
            id = payRequests.get(payRequests.size()-1).getId();
        }
        Assert.assertTrue(count == 1000);

        System.out.println("finished");
    }
}
