package test;

import com.google.gson.Gson;
import com.hf.base.enums.GroupType;
import com.hf.base.enums.OprType;
import com.hf.base.model.*;
import com.hf.base.utils.Pagenation;
import com.hf.base.utils.TypeConverter;
import com.hf.base.utils.Utils;
import com.hf.core.biz.AccountBiz;
import com.hf.core.dao.local.AccountOprLogDao;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AccountTest extends BaseTestCase {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AccountOprLogDao accountOprLogDao;

    @Test
    public void testGetAccountPageInfo() {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId",8);
        map.put("pageSize",10);
        map.put("currentPage",1);

        try {
            AccountRequest accountRequest = TypeConverter.convert(map,AccountRequest.class);
            Pagenation<AccountPageInfo> page = accountBiz.getAccountPage(accountRequest);
            System.out.println(new Gson().toJson(page));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String,Object> map2 = new HashMap<>();
        map2.put("groupId",1);
        map2.put("pageSize",10);
        map2.put("currentPage",1);

        try {
            AccountRequest accountRequest = TypeConverter.convert(map2,AccountRequest.class);
            Pagenation<AccountPageInfo> page = accountBiz.getAccountPage(accountRequest);
            System.out.println(new Gson().toJson(page));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String,Object> map3 = new HashMap<>();
        map3.put("groupId",3);
        map3.put("pageSize",10);
        map3.put("currentPage",1);

        try {
            AccountRequest accountRequest = TypeConverter.convert(map3,AccountRequest.class);
            Pagenation<AccountPageInfo> page = accountBiz.getAccountPage(accountRequest);
            System.out.println(new Gson().toJson(page));
            Assert.assertEquals(page.getTotalSize(),2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String,Object> map4 = new HashMap<>();
        map4.put("groupId",1);
        map4.put("groupType", GroupType.COMPANY.getValue());
        map4.put("pageSize",10);
        map4.put("currentPage",1);

        try {
            AccountRequest accountRequest = TypeConverter.convert(map4,AccountRequest.class);
            Pagenation<AccountPageInfo> page = accountBiz.getAccountPage(accountRequest);
            System.out.println(new Gson().toJson(page));
            Assert.assertEquals(page.getTotalSize(),2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAdminAccount() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setCurrentPage(1);
        accountRequest.setPageSize(10);
        accountRequest.setGroupId(1L);
        Pagenation<AdminAccountPageInfo> pagenation = accountBiz.getAdminAccountPage(accountRequest);
        System.out.println(new Gson().toJson(pagenation));
    }

    @Test
    public void testGetOprLog() {
        Map<String,Object> params = new HashMap<>();
        params.put("currentPage",1);
        params.put("pageSize",5);
        params.put("groupId",8);

        try {
            AccountOprRequest accountOprRequest = TypeConverter.convert(params, AccountOprRequest.class);
            Pagenation<AccountOprInfo> pagenation = accountBiz.getAccountOprPage(accountOprRequest);
            System.out.println(new Gson().toJson(pagenation));
            Assert.assertEquals(pagenation.getTotalSize(),10);
            for(AccountOprInfo oprInfo:pagenation.getData()) {
                Assert.assertEquals(oprInfo.getGroupId().longValue(),8L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        params.put("currentPage",1);
        params.put("pageSize",5);
        params.put("groupId",1);

        try {
            AccountOprRequest accountOprRequest = TypeConverter.convert(params, AccountOprRequest.class);
            Pagenation<AccountOprInfo> pagenation = accountBiz.getAccountOprPage(accountOprRequest);
            System.out.println(new Gson().toJson(pagenation));
            Assert.assertEquals(pagenation.getTotalSize(),50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSumAmount() {
        BigDecimal logAmount = accountBiz.getLockedAmount(13L);
        System.out.println(logAmount);
    }

    @Test
    public void testGetUrlParams(){
        BigDecimal finishAmount = accountOprLogDao.sumFinishAmount(Arrays.asList(3166L),Arrays.asList(OprType.PAY.getValue()));
    }
}
