package test;

import com.google.gson.Gson;
import com.hf.base.enums.OprStatus;
import com.hf.base.enums.OprType;
import com.hf.base.enums.SettleStatus;
import com.hf.base.enums.WithDrawRole;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.model.WithDrawInfo;
import com.hf.base.model.WithDrawRequest;
import com.hf.base.utils.Pagenation;
import com.hf.base.utils.TypeConverter;
import com.hf.core.biz.SettleBiz;
import com.hf.core.dao.local.*;
import com.hf.core.model.po.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SettleTest extends BaseTestCase {
    @Autowired
    private SettleBiz settleBiz;
    @Autowired
    private SettleTaskDao settleTaskDao;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private AdminAccountOprLogDao adminAccountOprLogDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private UserGroupDao userGroupDao;

    @Test
    public void testSaveSettle() {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId","8");
        map.put("settleAmount","1000");
        map.put("settleAmount","1000");
        map.put("settleBankCard","1");
        SettleTask settleTask;
        try {
            settleTask = TypeConverter.convert(map, SettleTask.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizFailException(e.getMessage());
        }
        Account settleAccount = accountDao.selectByGroupId(8L);
        BigDecimal amount = settleAccount.getAmount();
        BigDecimal lockAmount = settleAccount.getLockAmount();
        BigDecimal paidAmount = settleAccount.getPaidAmount();
        BigDecimal fee = settleAccount.getFee();

        Account feeAccount = accountDao.selectByGroupId(1L);
        BigDecimal feeAmount = feeAccount.getAmount();
        BigDecimal feeLockAmount = feeAccount.getLockAmount();
        BigDecimal feePaidAmount = feeAccount.getPaidAmount();
        BigDecimal feeFee = feeAccount.getFee();

        AdminAccount payAccount = adminAccountDao.selectByGroupId(1L);
        BigDecimal payAmount = payAccount.getAmount();
        BigDecimal payLockAmount = payAccount.getLockAmount();
        BigDecimal payPaidAmount = payAccount.getPaidAmount();
        BigDecimal payFee = payAccount.getFee();


        settleBiz.saveSettle(settleTask);

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        Assert.assertEquals(settleTask.getStatus().intValue(), SettleStatus.PROCESSING.getValue());
        System.out.println(new Gson().toJson(settleTask));

        AccountOprLog withDrawLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(), OprType.WITHDRAW.getValue());
        Assert.assertTrue(withDrawLog.getAmount().compareTo(settleTask.getPayAmount())==0);
        AccountOprLog taxLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(), OprType.TAX.getValue());
        Assert.assertTrue(taxLog.getAmount().compareTo(settleTask.getFee()) == 0);
        AccountOprLog feeLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getPayGroupId(), OprType.FEE.getValue());
        Assert.assertTrue(feeLog.getAmount().compareTo(settleTask.getFee())==0);
        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(String.valueOf(settleTask.getId()));
        Assert.assertTrue(adminAccountOprLog.getAmount().compareTo(settleTask.getPayAmount())==0);

        settleAccount = accountDao.selectByPrimaryKey(settleTask.getAccountId());
        Assert.assertTrue(settleAccount.getAmount().compareTo(amount)==0);
        Assert.assertTrue(settleAccount.getLockAmount().compareTo(lockAmount.add(new BigDecimal("1000")))==0);
        Assert.assertTrue(settleAccount.getFee().compareTo(fee)==0);
        Assert.assertTrue(settleAccount.getPaidAmount().compareTo(paidAmount)==0);

        feeAccount = accountDao.selectByPrimaryKey(settleTask.getPayAccountId());
        Assert.assertTrue(feeAccount.getAmount().compareTo(feeAmount)==0);
        Assert.assertTrue(feeAccount.getLockAmount().compareTo(feeLockAmount)==0);
        Assert.assertTrue(feeAccount.getFee().compareTo(feeFee)==0);
        Assert.assertTrue(feeAccount.getPaidAmount().compareTo(feePaidAmount)==0);

        payAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        Assert.assertTrue(payAccount.getAmount().compareTo(payAmount)==0);
        Assert.assertTrue(payAccount.getLockAmount().compareTo(payLockAmount.add(new BigDecimal("950")))==0);
        Assert.assertTrue(payAccount.getFee().compareTo(payFee)==0);
        Assert.assertTrue(payAccount.getPaidAmount().compareTo(payPaidAmount)==0);

        settleBiz.finishSettle(settleTask.getId());

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        Assert.assertTrue(settleTask.getStatus()== SettleStatus.SUCCESS.getValue());

        settleAccount = accountDao.selectByPrimaryKey(settleTask.getAccountId());
        Assert.assertTrue(settleAccount.getAmount().compareTo(amount.subtract(new BigDecimal(1000)))==0);
        Assert.assertTrue(settleAccount.getLockAmount().compareTo(lockAmount)==0);
        Assert.assertTrue(settleAccount.getFee().compareTo(fee.add(new BigDecimal("50")))==0);
        Assert.assertTrue(settleAccount.getPaidAmount().compareTo(paidAmount.add(new BigDecimal("950")))==0);

        feeAccount = accountDao.selectByPrimaryKey(settleTask.getPayAccountId());
        Assert.assertTrue(feeAccount.getAmount().compareTo(feeAmount.add(new BigDecimal("50")))==0);
        Assert.assertTrue(feeAccount.getLockAmount().compareTo(feeLockAmount)==0);
        Assert.assertTrue(feeAccount.getFee().compareTo(feeFee)==0);
        Assert.assertTrue(feeAccount.getPaidAmount().compareTo(feePaidAmount)==0);

        payAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        Assert.assertTrue(payAccount.getAmount().compareTo(payAmount.subtract(new BigDecimal("950")))==0);
        Assert.assertTrue(payAccount.getLockAmount().compareTo(payLockAmount)==0);
        Assert.assertTrue(payAccount.getFee().compareTo(payFee)==0);
        Assert.assertTrue(payAccount.getPaidAmount().compareTo(payPaidAmount.add(new BigDecimal("950")))==0);
    }

    @Test
    public void testGetWithDrawPage() {
        WithDrawRequest withDrawRequest = new WithDrawRequest();
        withDrawRequest.setGroupId(13L);
        withDrawRequest.setCurrentPage(1);
        withDrawRequest.setPageSize(15);
        withDrawRequest.setRole(WithDrawRole.DRAWER.getValue());
        Pagenation<WithDrawInfo> pagenation = settleBiz.getWithDrawPage(withDrawRequest);
        System.out.println(new Gson().toJson(pagenation));

        withDrawRequest = new WithDrawRequest();
        withDrawRequest.setGroupId(1L);
        withDrawRequest.setCurrentPage(1);
        withDrawRequest.setPageSize(15);
        withDrawRequest.setRole(WithDrawRole.PAYER.getValue());
        pagenation = settleBiz.getWithDrawPage(withDrawRequest);
        System.out.println(new Gson().toJson(pagenation));
    }

    @Test
    public void testFinishSettle() {
        Map<String,Object> map = new HashMap<>();
        map.put("groupId","8");
        map.put("settleAmount","1000");
        map.put("settleAmount","1000");
        map.put("settleBankCard","1");
        SettleTask settleTask;
        try {
            settleTask = TypeConverter.convert(map, SettleTask.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizFailException(e.getMessage());
        }
        Account settleAccount = accountDao.selectByGroupId(settleTask.getGroupId());
        BigDecimal settleAmount = settleAccount.getAmount();
        BigDecimal settleLockAmount = settleAccount.getLockAmount();
        BigDecimal settlePayAmount = settleAccount.getPaidAmount();
        BigDecimal settleFee = settleAccount.getFee();

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(settleTask.getGroupId());

        AdminAccount adminAccount = adminAccountDao.selectByGroupId(userGroup.getCompanyId());
        BigDecimal adminAmount = adminAccount.getAmount();
        BigDecimal adminPaidAmount = adminAccount.getPaidAmount();
        BigDecimal adminFee = adminAccount.getFee();
        BigDecimal adminLockAmount = adminAccount.getPaidAmount();

        Account payAccount = accountDao.selectByGroupId(userGroup.getCompanyId());
        BigDecimal payAmount = payAccount.getAmount();
        BigDecimal payFee = payAccount.getFee();
        BigDecimal payPaidAmount = payAccount.getPaidAmount();
        BigDecimal payLockAmount = payAccount.getLockAmount();

        settleBiz.saveSettle(settleTask);

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        Assert.assertEquals(settleTask.getStatus().intValue(),SettleStatus.PROCESSING.getValue());
        settleAccount = accountDao.selectByGroupId(settleTask.getGroupId());

        Assert.assertTrue(settleAccount.getAmount().compareTo(settleAmount)==0);
        Assert.assertTrue(settleAccount.getLockAmount().subtract(settleLockAmount).compareTo(new BigDecimal("1000"))==0);
        Assert.assertTrue(settleAccount.getPaidAmount().compareTo(settlePayAmount)==0);
        Assert.assertTrue(settleAccount.getFee().compareTo(settleFee)==0);

        AccountOprLog amountLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.WITHDRAW.getValue());
        Assert.assertEquals(amountLog.getStatus().intValue(), OprStatus.NEW.getValue());
        AccountOprLog feeLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.TAX.getValue());
        Assert.assertEquals(feeLog.getStatus().intValue(),OprStatus.NEW.getValue());

        adminAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        Assert.assertEquals(adminAccount.getLockAmount(),settleTask.getPayAmount());
        AdminAccountOprLog adminAccountOprLog = adminAccountOprLogDao.selectByNo(String.valueOf(settleTask.getId()));
        Assert.assertEquals(adminAccountOprLog.getStatus().intValue(),OprStatus.NEW.getValue());
        Assert.assertTrue(adminAccountOprLog.getAmount().compareTo(settleTask.getPayAmount())==0);

        AccountOprLog payOprLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getPayGroupId(),OprType.FEE.getValue());
        Assert.assertTrue(payOprLog.getAmount().compareTo(settleTask.getFee())==0);


        settleBiz.settleFailed(settleTask.getId());

        settleTask = settleTaskDao.selectByPrimaryKey(settleTask.getId());
        Assert.assertEquals(settleTask.getStatus().intValue(),SettleStatus.FAILED.getValue());
        settleAccount = accountDao.selectByGroupId(settleTask.getGroupId());
        Assert.assertTrue(settleAccount.getLockAmount().compareTo(settleLockAmount)==0);
        Assert.assertTrue(settleAccount.getAmount().compareTo(settleAmount)==0);
        Assert.assertTrue(settleAccount.getPaidAmount().compareTo(settlePayAmount)==0);
        Assert.assertTrue(settleAccount.getFee().compareTo(settleFee)==0);

        amountLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.WITHDRAW.getValue());
        Assert.assertEquals(amountLog.getStatus().intValue(),OprStatus.PAY_FAILED.getValue());

        feeLog = accountOprLogDao.selectByUnq(String.valueOf(settleTask.getId()),settleTask.getGroupId(),OprType.TAX.getValue());
        Assert.assertEquals(feeLog.getStatus().intValue(),OprStatus.PAY_FAILED.getValue());

        adminAccount = adminAccountDao.selectByGroupId(settleTask.getPayGroupId());
        Assert.assertTrue(adminAccount.getLockAmount().compareTo(adminLockAmount)==0);
        Assert.assertTrue(adminAccount.getAmount().compareTo(adminAmount)==0);
        Assert.assertTrue(adminAccount.getPaidAmount().compareTo(adminPaidAmount)==0);
        Assert.assertTrue(adminAccount.getFee().compareTo(adminFee)==0);

        AdminAccountOprLog adminLog = adminAccountOprLogDao.selectByNo(String.valueOf(settleTask.getId()));
        Assert.assertEquals(adminLog.getStatus().intValue(),OprStatus.PAY_FAILED.getValue());

        Account account = accountDao.selectByGroupId(settleTask.getPayGroupId());
        Assert.assertTrue(account.getFee().compareTo(payFee)==0);
        Assert.assertTrue(account.getLockAmount().compareTo(payLockAmount)==0);
        Assert.assertTrue(account.getAmount().compareTo(payAmount)==0);
        Assert.assertTrue(account.getPaidAmount().compareTo(payPaidAmount)==0);
    }
}
