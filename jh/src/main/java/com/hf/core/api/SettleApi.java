package com.hf.core.api;

import com.hf.base.contants.CodeManager;
import com.hf.base.model.AgentPayLog;
import com.hf.base.model.WithDrawInfo;
import com.hf.base.model.WithDrawRequest;
import com.hf.base.utils.Pagenation;
import com.hf.base.utils.ResponseResult;
import com.hf.base.utils.TypeConverter;
import com.hf.core.biz.SettleBiz;
import com.hf.core.dao.local.SettleTaskDao;
import com.hf.core.model.po.SettleTask;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/settle")
public class SettleApi {
    @Autowired
    private SettleBiz settleBiz;
    @Autowired
    private SettleTaskDao settleTaskDao;

    @RequestMapping(value = "/new_settle_request",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Boolean> settleRequest(@RequestBody Map<String,Object> params) {
        try {
            SettleTask settleTask = TypeConverter.convert(params,SettleTask.class);
            settleBiz.saveSettle(settleTask);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        }
    }

    @RequestMapping(value = "/get_with_draw_page",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Pagenation<WithDrawInfo>> getWithDrawPage(@RequestBody Map<String,Object> params) {
        WithDrawRequest withDrawRequest;
        try {
            withDrawRequest = TypeConverter.convert(params,WithDrawRequest.class);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),new Pagenation<WithDrawInfo>(new ArrayList<>()));
        }
        Pagenation<WithDrawInfo> pagenation = settleBiz.getWithDrawPage(withDrawRequest);
        return ResponseResult.success(pagenation);
    }

    @RequestMapping(value = "/finish_with_draw",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Boolean> finishWithDraw(@RequestBody Map<String,Object> params) {
        if(MapUtils.isEmpty(params) || params.get("id") == null) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,"condition empty",false);
        }
        Long id = new BigDecimal(params.get("id").toString()).longValue();
        settleBiz.finishSettle(id);
        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/with_draw_failed",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Boolean> withDrawFailed(@RequestBody Map<String,Object> params) {
        if(MapUtils.isEmpty(params) || params.get("id") == null) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,"condition empty",false);
        }
        Long id = new BigDecimal(params.get("id").toString()).longValue();
        settleBiz.settleFailed(id);
        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/new_agent_pay",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<List<AgentPayLog>> newAgentPay(@RequestBody Map<String,Object> params) {
        if(MapUtils.isEmpty(params) || params.get("withDrawId") == null) {
            return ResponseResult.success(new ArrayList<>());
        }
        List<AgentPayLog> results = settleBiz.newAgentPay(params.get("withDrawId").toString());
        return ResponseResult.success(results);
    }

    @RequestMapping(value = "/get_settle_task",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<SettleTask> getSettleTask(@RequestBody Map<String,Object> params) {
        try {
            Long withDrawId = new BigDecimal(params.get("withDrawId").toString()).longValue();
            SettleTask settleTask = settleTaskDao.selectByPrimaryKey(withDrawId);
            return ResponseResult.success(settleTask);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),null);
        }
    }

    @RequestMapping(value = "/submit_agent_pay",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<Boolean> submitAgentPay(@RequestBody Map<String,Object> params) {
        Long id = new BigDecimal(params.get("taskId").toString()).longValue();
        settleBiz.submitAgentPay(id);
        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/finish_agent_pay",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<Boolean> finishAgentPay(@RequestBody Map<String,Object> params) {
        Long id = new BigDecimal(params.get("id").toString()).longValue();
        settleBiz.finishAgentPay(id);
        return ResponseResult.success(true);
    }
}
