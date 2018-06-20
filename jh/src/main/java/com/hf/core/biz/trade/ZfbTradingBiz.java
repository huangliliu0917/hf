package com.hf.core.biz.trade;

import com.alibaba.fastjson.JSONObject;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.HttpClient;
import com.hf.base.utils.Utils;
import com.hf.core.dao.remote.ZfbClient;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.PayRequestBack;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZfbTradingBiz extends AbstractTradingBiz {
    @Autowired
    private ZfbClient zfbClient;

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.ZFB;
    }

    /**
     * todo 1.请求地址 2.密钥 3.商户号 4.pay_orderid长度不够 5.支付宝H5是否pay_bankcode = "904" 6.notify和callback是否都必填，callback什么意思
     * todo 7.pay_amount单位
     * @param payRequest
     * @param request
     * @param response
     */
    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        String pay_memberid = "10123";
        String pay_orderid = payRequest.getOutTradeNo(); //todo 20长度不够,至少32
        String pay_applydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String pay_bankcode = "903"; //todo 支付宝H5怎么传
        String pay_notifyurl = "http://huifufu.cn/openapi/zfb/pay_notice";
        //String pay_callbackurl = ""; //todo 是否必填
        String pay_amount = String.valueOf(new BigDecimal(String.valueOf(payRequest.getTotalFee())).divide(new BigDecimal("100"),2, RoundingMode.HALF_UP));//todo 单位
        String format = "json";
        String pay_ip = payRequest.getCreateIp();
        String pay_productname = payRequest.getBody();
        String key = "u494h4ttje8tj3nqsogvv7p51p7c58qr";

        Map<String,Object> params = new HashMap<>();
        params.put("pay_memberid",pay_memberid);
        params.put("pay_orderid",pay_orderid);
        params.put("pay_applydate",pay_applydate);
        params.put("pay_bankcode",pay_bankcode);
        params.put("pay_notifyurl",pay_notifyurl);
        params.put("pay_amount",pay_amount);
        String sign = Utils.encrypt(params,key);
        params.put("pay_ip",pay_ip);
        params.put("pay_md5sign",sign);
        params.put("pay_productname",pay_productname);

//        Map<String,Object> result = zfbClient.unifiedorder(params);
        try {
            HttpClient httpClient = new HttpClient(response);
            httpClient.setParameter(params);
            httpClient.sendByPost("https://api.likerpay.com/Pay_Index.html");

            PayRequestBack payRequestBack = new PayRequestBack();
            payRequestBack.setMchId(payRequest.getMchId());
            payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());

            payRequestBack.setMessage("success");
            payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);

            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            payService.remoteSuccess(payRequest,payRequestBack);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BizFailException(e);
        }
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        return null;
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }
}
