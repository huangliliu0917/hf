package com.hf.core.api;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.GroupStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.base.utils.ResponseResult;
import com.hf.base.utils.TypeConverter;
import com.hf.core.biz.TrdBiz;
import com.hf.core.biz.UserBiz;
import com.hf.core.biz.service.TradeBizFactory;
import com.hf.core.biz.trade.TradeBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.job.pay.PayJob;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import com.hf.core.utils.CallBackCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/jhAdmin")
public class AdminApi {
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private TrdBiz trdBiz;
    @Autowired
    private PayJob payJob;
    @Autowired
    private TradeBizFactory tradeBizFactory;
    @Autowired
    private PayRequestDao payRequestDao;

    @RequestMapping(value = "/get_authorized_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<UserGroup>> getAuthorizedList(@RequestBody Map<String,String> params) {
        Long companyId = Long.parseLong(params.get("companyId"));
        List<UserGroup> list = userGroupDao.select(MapUtils.buildMap("status", GroupStatus.SUBMITED.getValue(),"companyId",companyId));
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/save_admin_group",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveAdminGroup(@RequestBody Map<String,Object> params) {
        try {
            UserGroup userGroup = TypeConverter.convert(params, UserGroup.class);
            if(Objects.isNull(userGroup.getSubGroupId())) {
                throw new BizFailException("sub group id is null");
            }
            UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(userGroup.getSubGroupId());
            if(Objects.isNull(subUserGroup)) {
                throw new BizFailException(String.format("sub group not exists,id:%s",userGroup.getSubGroupId()));
            }
            userBiz.saveAminGroup(userGroup);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        }
    }

    @RequestMapping(value = "/trade_status_monitor",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List> tradeStatusMonitor(@RequestBody Map<String,Object> params) {
        String groupNo = String.valueOf(params.get("groupNo"));
        String outTradeNo = String.valueOf("outTradeNo");

        return null;
    }

    @RequestMapping(value = "/orderinfo",method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> orderinfo(String outTradeNo) {
        return trdBiz.orderInfo(outTradeNo);
    }

    @RequestMapping(value = "/runJob",method = RequestMethod.GET)
    public @ResponseBody
    String runJob(String outTradeNo) {
        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        TradeBiz tradeBiz = tradeBizFactory.getTradeBiz(payRequest.getChannelProviderCode());
        tradeBiz.handleProcessingRequest(payRequest);
        return "finished";
    }

    @RequestMapping(value = "/getCallBackCache",method = RequestMethod.GET)
    public void getCallBackCache() {
        System.out.println(new Gson().toJson(CallBackCache.noticedList));
    }
 }
