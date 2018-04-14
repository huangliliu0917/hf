package test;

import com.google.gson.Gson;
import com.hf.base.enums.OprType;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.enums.TradeType;
import com.hf.base.model.TradeRequest;
import com.hf.base.model.TradeRequestDto;
import com.hf.base.model.TradeStatisticsRequest;
import com.hf.base.model.UserStatistic;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Pagenation;
import com.hf.base.utils.TypeConverter;
import com.hf.core.biz.TrdBiz;
import com.hf.core.dao.local.AccountOprLogDao;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.model.PropertyConfig;
import com.hf.core.model.po.AccountOprLog;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class TrdTest extends BaseTestCase {
    @Autowired
    private TrdBiz trdBiz;
    @Autowired
    private PropertyConfig propertyConfig;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private UserGroupDao userGroupDao;

    @Test
    public void testConvertDate() throws Exception {
        Map<String,Object> map = new HashMap<>();
        Date fromDate = new Date();
        map.put("createTime",fromDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = (Date)map.get("createTime");
        Assert.assertEquals(date,fromDate);
    }

    @Test
    public void testGetTrdList() {
        Map<String,Object> params = MapUtils.buildMap("mchId","123546",
                "outTradeNo","1234546",
                "fromTime",new Date(),
                "endTime",new Date(),
                "channelId","123456",
                "status", PayRequestStatus.OPR_GENERATED.getValue(),
                "type", TradeType.PAY.getValue(),
                "groupId","1234567");
        try {
            TradeRequest tradeRequest = TypeConverter.convert(params, TradeRequest.class);
            System.out.println(new Gson().toJson(tradeRequest));
        }catch (Exception e ) {
            e.printStackTrace();
        }

        Map<String,Object> params2 = new HashMap<>();
        params2.put("groupId","123456");
        try {
            TradeRequest tradeRequest = TypeConverter.convert(params2, TradeRequest.class);
            System.out.println(new Gson().toJson(tradeRequest));
        }catch (Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSelectTrdList() {
        Map<String,Object> params2 = new HashMap<>();
        params2.put("groupId","1");
        params2.put("pageSize",10);
        try {
            TradeRequest tradeRequest = TypeConverter.convert(params2, TradeRequest.class);
            Pagenation<TradeRequestDto> list = trdBiz.getTradeList(tradeRequest);
            System.out.println(new Gson().toJson(list));
        }catch (Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTreeSet() {
        Set<String> set = new TreeSet<>();
        set.add("a1002244");
        set.add("12345");
        set.add("c2435312");
        set.add("cd253");
        set.add("hssd");
        set.add("dfgfas");
        set.add("dg2134565");
        for(String  str:set) {
            System.out.println(str);
        }

    }

    @Test
    public void testGetConfig() {
        System.out.println(propertyConfig.getCallbackUrl());
    }

    @Test
    public void testSelectUserStatistics() {
        List<Long> groupIds = Arrays.asList(8656L,5204L);

        for(Long groupId:groupIds) {
            UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
            for(int i=0;i<100;i++) {
                String outTradeNo = String.valueOf(RandomUtils.nextLong());
                PayRequest payRequest = new PayRequest();
                payRequest.setOutTradeNo(outTradeNo);
                payRequest.setOutNotifyUrl("www.baidu.com");
                payRequest.setActualAmount(new BigDecimal("1000"));
                payRequest.setStatus(PayRequestStatus.OPR_SUCCESS.getValue());
                payRequest.setBody("1234");
                payRequest.setMchId(userGroup.getGroupNo());
                payRequest.setService("04");
                payRequest.setSign(String.valueOf(RandomUtils.nextLong()));
                payRequestDao.insertSelective(payRequest);

                AccountOprLog log = new AccountOprLog();
                log.setRemark("21342edwdsa");
                log.setGroupId(userGroup.getId());
                log.setType(OprType.PAY.getValue());
                log.setAmount(new BigDecimal("1000"));
                log.setOutTradeNo(outTradeNo);
                log.setAccountId(RandomUtils.nextLong());

                accountOprLogDao.insertSelective(log);
            }
        }

        TradeStatisticsRequest request = new TradeStatisticsRequest();
        List<UserStatistic> list = trdBiz.getUserStatistics(request);
        System.out.println(new Gson().toJson(list));
    }
}
