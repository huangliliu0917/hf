package com.hf.core.api;

import com.hf.core.dao.local.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * Created by tengfei on 2017/11/4.
 */
@Controller
@RequestMapping("/account")
public class AccountApi {
    @Autowired
    private AccountDao accountDao;

    /**
     *
     * @return
     */
    @RequestMapping(value = "/get_account_info",method = RequestMethod.GET)
    public ModelAndView getAccountInfo(Long userId) {
//        Account account = accountDao.selectByUserId(userId);
//        Map<String,Object> resultMap = MapUtils.buildMap("amount",account.getAmount(),"lockAmount",account.getLockAmount(),"paidAmount",account.getPaidAmount());
        return null;
    }

    @RequestMapping(value = "/caculate_fee",method = RequestMethod.POST)
    public ModelAndView caculateFee(BigDecimal amount) {
        return null;
    }

    /**
     * 1.计算提现手续费，校验提现金额
     * 2.校验银行卡
     * 3.校验密码
     * 4.生成oprLog,状态结算中。
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/handle_close_request",method = RequestMethod.POST)
    public ModelAndView handleCloseRequest(HttpServletRequest request, HttpServletResponse response) {


        return null;
    }

    @RequestMapping(value = "/get_close_record",method = RequestMethod.GET)
    public ModelAndView getCloseRecord(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @RequestMapping(value = "/get_trd_record",method = RequestMethod.GET)
    public ModelAndView getTrdRecords() {
        return null;
    }
}
