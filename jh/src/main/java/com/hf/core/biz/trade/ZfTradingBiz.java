package com.hf.core.biz.trade;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.BankCode;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.core.biz.service.CacheService;
import com.hf.core.dao.local.UserInfoDao;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.PayRequestBack;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZfTradingBiz extends AbstractTradingBiz {

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.ZF;
    }
    @Autowired
    @Qualifier("zfClient")
    private PayClient payClient;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private CacheService cacheService;

    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        PayRequestBack payRequestBack = new PayRequestBack();
        payRequestBack.setMchId(payRequest.getMchId());
        payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());

        try {
            UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());

            String inTradeOrderNo = payRequest.getOutTradeNo();
            String merchantNo = "990581077770019";
            String terminalNo = "77700474";
            String attach = "991361077780001";
            String key = "SU62HD9438";
            String productPrice = String.valueOf(payRequest.getTotalFee());
            String payMoney = String.valueOf(payRequest.getTotalFee());
            String productName = payRequest.getBody();
            String payType = "70";
            String merchantURL = "http://huifufu.cn/openapi/zf/pay_notice";
            String frontURL = payRequest.getFrontUrl();
            String operatorId = payRequest.getBuyerId();
            String productDescription = payRequest.getBody();
            String terminal = "PC";
            String defaultBank = BankCode.parse(payRequest.getBankCode()).getBank();

            String signature = DigestUtils.md5Hex(merchantNo+terminalNo+ inTradeOrderNo +defaultBank+ payMoney + productPrice  + productName +payType+key).toUpperCase();
            String reqSource = "2";
            String url = "http://paygw.guangyinwangluo.com/swPayInterface/CHPay";
            Map<String,Object> map = new HashMap<>();
            map.put("inTradeOrderNo",inTradeOrderNo);
            map.put("merchantNo",merchantNo);
            map.put("terminalNo",terminalNo);
            map.put("productPrice",productPrice);
            map.put("payMoney",payMoney);
            map.put("productName",URLEncoder.encode(productName,"UTF-8"));
            map.put("payType",payType);
            map.put("merchantURL",merchantURL);
            map.put("frontURL",frontURL);
            map.put("operatorId",operatorId);
            map.put("productDescription",URLEncoder.encode(productDescription,"UTF-8"));
            map.put("terminal",terminal);
            map.put("defaultBank",URLEncoder.encode(defaultBank,"UTF-8"));
            map.put("signature",signature);
            map.put("reqSource",reqSource);
            map.put("attach",attach);

//            Map<String, Object> resultMap = payClient.unifiedorder(map);

            for(int i=0;i<map.keySet().size();i++) {
                if(i==0) {
                    url = url+"?"+map.keySet().toArray()[i]+"="+map.get(map.keySet().toArray()[i]);
                } else {
                    url = url+"&"+map.keySet().toArray()[i]+"="+map.get(map.keySet().toArray()[i]);
                }
            }
            response.sendRedirect(url);

            payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);
            payRequestBack.setMessage("下单成功");
        } catch (Exception e) {
            payRequestBack.setErrcode(CodeManager.PAY_FAILED);
            payRequestBack.setMessage("下单失败");
        }

        payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
        payService.remoteSuccess(payRequest,payRequestBack);
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        String webOrderId = map.get("webOrderId");
        String inTradeOrderNo = map.get("inTradeOrderNo");
        String tradeStatus = map.get("tradeStatus");
        String theThirdOrderNo = map.get("theThirdOrderNo");
        String gmtCreate = map.get("gmtCreate");
        String gmtPayment = map.get("gmtPayment");
        String totalFee = map.get("totalFee");
        String signature = map.get("signature");

        PayRequest payRequest = payRequestDao.selectByTradeNo(inTradeOrderNo);
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;

        if(payRequestStatus == PayRequestStatus.OPR_SUCCESS) {
            return new Gson().toJson(MapUtils.buildMap("resCode","0000"));
        }

        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        if(StringUtils.equalsIgnoreCase(tradeStatus,"SUCCESS")) {
            payService.paySuccess(inTradeOrderNo);
        } else {
            payService.payFailed(inTradeOrderNo);
        }

        return "SUCCESS";
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }
}
