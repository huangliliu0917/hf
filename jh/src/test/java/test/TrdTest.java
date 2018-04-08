package test;

import com.google.gson.Gson;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.enums.TradeType;
import com.hf.base.model.TradeRequest;
import com.hf.base.model.TradeRequestDto;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.Pagenation;
import com.hf.base.utils.TypeConverter;
import com.hf.core.biz.TrdBiz;
import com.hf.core.model.PropertyConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

public class TrdTest extends BaseTestCase {
    @Autowired
    private TrdBiz trdBiz;
    @Autowired
    private PropertyConfig propertyConfig;

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
}
