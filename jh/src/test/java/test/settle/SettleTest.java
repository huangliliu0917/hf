package test.settle;

import com.google.gson.Gson;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.OprStatus;
import com.hf.base.enums.OprType;
import com.hf.base.enums.SettleStatus;
import com.hf.base.utils.MapUtils;
import com.hf.core.biz.SettleBiz;
import com.hf.core.biz.service.SettleService;
import com.hf.core.dao.local.*;
import com.hf.core.model.po.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.BaseTestCase;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class SettleTest extends BaseTestCase {
    @Autowired
    private SettleTaskDao settleTaskDao;
    @Autowired
    private SettleBiz settleBiz;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private UserChannelAccountDao userChannelAccountDao;
    @Autowired
    private SettleService settleService;
    @Autowired
    private ChannelProviderDao channelProviderDao;

    public Long prepareData() {
        SettleTask settleTask = new SettleTask();
        settleTask.setGroupId(8773L);
        settleTask.setSettleAmount(new BigDecimal("5997"));
        settleTask.setPayBankCard(0L);
        settleTask.setBank("中国工商银行");
        settleTask.setBankNo("622262222421512512512");
        settleTask.setDeposit("上海支行");
        settleTask.setOwner("张三");
        settleTask.setBankCode("ICBC");
        settleTask.setIdNo("37132619881122005X");
        settleTask.setTel("13611681327");
        settleBiz.saveSettle(settleTask);
        return settleTask.getId();
    }

    @Test
    public void testSettle() {
        Long taskId = prepareData();

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(8773L);
        Account comAccount = accountDao.selectByGroupId(userGroup.getCompanyId());
        List<UserChannelAccount> userChannelAccounts = userChannelAccountDao.selectByGroupId(userGroup.getId());
        List<UserChannelAccount> adminChannelAccounts = userChannelAccountDao.selectByGroupId(userGroup.getCompanyId());
        AdminAccount adminAccount = adminAccountDao.selectByGroupId(userGroup.getCompanyId());
        SettleTask settleTask = settleTaskDao.selectByPrimaryKey(taskId);

        Account account = accountDao.selectByGroupId(8773L);
        Assert.assertTrue(account.getLockAmount().compareTo(new BigDecimal("5997"))==0);

        List<AccountOprLog> logs = accountOprLogDao.select(
                MapUtils.buildMap(
                                        "outTradeNo",String.valueOf(settleTask.getId()),
                                        "startIndex",0,
                                        "pageSize",Integer.MAX_VALUE));
        System.out.println("---------------");
        for(AccountOprLog log:logs) {
            System.out.println(log.getGroupId()+","+log.getStatus()+","+log.getProviderCode()+","+log.getAmount()+","+log.getType());
        }

        System.out.println("user channel account -------------");
        for(ChannelProvider channelProvider:ChannelProvider.values()) {
            UserChannelAccount userChannelAccount = userChannelAccountDao.selectByUnq(8773L,channelProvider.getCode());
            if(userChannelAccount != null) {
                System.out.println("user channel info:"+userChannelAccount.getChannelCode()+","+userChannelAccount.getAmount()+","+userChannelAccount.getLockAmount());
            }
        }

        List<AccountOprLog> accountOprLogs = accountOprLogDao.select(MapUtils.buildMap("outTradeNo",String.valueOf(settleTask.getId()),
                "groupIds", Arrays.asList(settleTask.getGroupId()),
                "oprType", OprType.WITHDRAW.getValue(),
                "startIndex",0,
                "pageSize",Integer.MAX_VALUE));

        for(AccountOprLog accountOprLog:accountOprLogs) {
            com.hf.core.model.po.ChannelProvider channelProvider = channelProviderDao.selectByCode(accountOprLog.getProviderCode());
            if(channelProvider.getAgentPay() == 1) {
                settleBiz.finishAgentPay(accountOprLog.getId());
            }
            settleService.paySuccess(settleTask,accountOprLog);
        }

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        Assert.assertTrue(settleTask.getSettleAmount().compareTo(settleTask.getPaidAmount())==0);

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        Assert.assertTrue(settleTask.getStatus() == SettleStatus.SUCCESS.getValue());

        System.out.println("----------");
        System.out.println(adminAccount.getAmount());
        adminAccount = adminAccountDao.selectByGroupId(userGroup.getCompanyId());
        System.out.println(adminAccount.getAmount());

        System.out.println("0000000000000");
        BigDecimal amount = comAccount.getAmount();
        comAccount = accountDao.selectByPrimaryKey(comAccount.getId());
        System.out.println("ori:"+amount+",to:"+comAccount.getAmount());

        userChannelAccounts.stream().forEach(userChannelAccount -> {
            BigDecimal userAmount = userChannelAccount.getAmount();
            userChannelAccount = userChannelAccountDao.selectByPrimaryKey(userChannelAccount.getId());
            System.out.println("channel:"+userChannelAccount.getChannelProvider()+",ori:"+userAmount+",to:"+userChannelAccount.getAmount());
        });

        adminChannelAccounts.stream().forEach(userChannelAccount -> {
            BigDecimal userAmount = userChannelAccount.getAmount();
            userChannelAccount = userChannelAccountDao.selectByPrimaryKey(userChannelAccount.getId());
            System.out.println("admin channel:"+userChannelAccount.getChannelProvider()+",ori:"+userAmount+",to:"+userChannelAccount.getAmount());
        });
    }

    @Test
    public void testAdminSettle() {
        SettleTask settleTask = new SettleTask();
        settleTask.setGroupId(1L);
        settleTask.setSettleAmount(new BigDecimal("8812"));
        settleTask.setPayBankCard(0L);
        settleTask.setBank("中国工商银行");
        settleTask.setBankNo("622262222421512512512");
        settleTask.setDeposit("上海支行");
        settleTask.setOwner("张三");
        settleTask.setBankCode("ICBC");
        settleTask.setIdNo("37132619881122005X");
        settleTask.setTel("13611681327");
        settleBiz.saveSettle(settleTask);

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        System.out.println(new Gson().toJson(settleTask));

        List<AccountOprLog> list = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),1L,OprType.WITHDRAW.getValue());
        System.out.println("admin logs:"+new Gson().toJson(list));

        List<AccountOprLog> list2 = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),1L,OprType.ADMIN_WITHDRAW.getValue());
        System.out.println("logs:"+new Gson().toJson(list2));

        List<UserChannelAccount> list3 = userChannelAccountDao.selectByGroupId(1L);
        System.out.println("admin channel account:"+new Gson().toJson(list3));

        for(AccountOprLog accountOprLog:list2) {
            List<UserChannelAccount> list4 = userChannelAccountDao.selectByGroupId(accountOprLog.getAccountId());
            System.out.println("user channel account:"+new Gson().toJson(list4));
        }

        for(AccountOprLog accountOprLog:list) {
            Account account = accountDao.selectByGroupId(accountOprLog.getGroupId());
            BigDecimal accountAmount = account.getAmount();

            AdminAccount adminAccount = adminAccountDao.selectByGroupId(accountOprLog.getGroupId());
            BigDecimal adminAmount = adminAccount.getAmount();

            UserChannelAccount userChannelAccount = userChannelAccountDao.selectByUnq(accountOprLog.getGroupId(),accountOprLog.getProviderCode());
            BigDecimal amount = userChannelAccount.getAmount();
            BigDecimal lockAmount = userChannelAccount.getLockAmount();
            BigDecimal paidAmount = userChannelAccount.getPaidAmount();

            settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
            BigDecimal settledAmount = settleTask.getPaidAmount();

            settleService.adminPaySuccess(settleTask,accountOprLog);
            accountOprLog = accountOprLogDao.selectByPrimaryKey(accountOprLog.getId());
            Assert.assertEquals(accountOprLog.getStatus().intValue(), OprStatus.FINISHED.getValue());

            account = accountDao.selectByGroupId(accountOprLog.getGroupId());
            Assert.assertTrue(accountAmount.subtract(account.getAmount()).compareTo(accountOprLog.getAmount())==0);

            adminAccount = adminAccountDao.selectByGroupId(accountOprLog.getGroupId());
            Assert.assertTrue(adminAmount.subtract(adminAccount.getAmount()).compareTo(accountOprLog.getAmount())==0);

            userChannelAccount = userChannelAccountDao.selectByUnq(accountOprLog.getGroupId(),accountOprLog.getProviderCode());
            Assert.assertTrue(amount.subtract(userChannelAccount.getAmount()).compareTo(accountOprLog.getAmount())==0);
            Assert.assertTrue(lockAmount.subtract(userChannelAccount.getLockAmount()).compareTo(accountOprLog.getAmount())==0);
            Assert.assertTrue(userChannelAccount.getPaidAmount().subtract(paidAmount).compareTo(accountOprLog.getAmount())==0);

            settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
            Assert.assertTrue(settleTask.getPaidAmount().subtract(settledAmount).compareTo(accountOprLog.getAmount())==0);
        }

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(settleTask.getGroupId());
        BigDecimal adminAmount = adminAccount.getAmount();

        for(AccountOprLog accountOprLog:list2) {
            UserChannelAccount userChannelAccount = userChannelAccountDao.selectByUnq(accountOprLog.getAccountId(),accountOprLog.getProviderCode());
            BigDecimal amount = userChannelAccount.getAmount();
            BigDecimal lockAmount = userChannelAccount.getLockAmount();
            BigDecimal paidAmount = userChannelAccount.getPaidAmount();

            UserChannelAccount jhChannelAccount = userChannelAccountDao.selectByUnq(accountOprLog.getAccountId(),"jh");
            BigDecimal jhAmount = (null == jhChannelAccount) ? new BigDecimal("0") : jhChannelAccount.getAmount();

            settleService.adminPaySuccess(settleTask,accountOprLog);
            accountOprLog = accountOprLogDao.selectByPrimaryKey(accountOprLog.getId());
            Assert.assertEquals(accountOprLog.getStatus().intValue(), OprStatus.FINISHED.getValue());

            userChannelAccount = userChannelAccountDao.selectByUnq(accountOprLog.getAccountId(),accountOprLog.getProviderCode());
            Assert.assertTrue(amount.subtract(userChannelAccount.getAmount()).compareTo(accountOprLog.getAmount())==0);
            Assert.assertTrue(lockAmount.subtract(userChannelAccount.getLockAmount()).compareTo(accountOprLog.getAmount())==0);
            Assert.assertTrue(userChannelAccount.getPaidAmount().subtract(paidAmount).compareTo(accountOprLog.getAmount())==0);

            jhChannelAccount = userChannelAccountDao.selectByUnq(accountOprLog.getAccountId(),"jh");
            Assert.assertTrue(jhChannelAccount.getAmount().subtract(jhAmount).compareTo(accountOprLog.getAmount())==0);
        }

        adminAccount = adminAccountDao.selectByGroupId(settleTask.getGroupId());
        Assert.assertTrue(adminAccount.getAmount().compareTo(adminAmount)==0);

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        Assert.assertTrue(settleTask.getSettleAmount().compareTo(settleTask.getPaidAmount())==0);
        Assert.assertTrue(settleTask.getStatus() == SettleStatus.SUCCESS.getValue());
    }

    @Test
    public void testSettleFailed() {
        Long taskId = prepareData();
        settleBiz.settleFailed(taskId);
    }
}
