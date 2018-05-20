package com.hf.core.biz.trade;

import com.google.gson.Gson;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.BankCode;
import com.hf.base.enums.ChannelCode;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.MapUtils;
import com.hf.core.biz.service.CacheService;
import com.hf.core.dao.local.UserInfoDao;
import com.hf.core.dao.remote.PayClient;
import com.hf.core.dao.remote.ZfClient;
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
    private CacheService cacheService;
    @Autowired
    private ZfClient zfClient;

    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        ChannelCode channelCode = ChannelCode.parseFromCode(payRequest.getService());
        switch (channelCode) {
            case ALI_H5:
                payForAliH5(payRequest,request,response);
                break;
            case WY:
                payForWy(payRequest,request,response);
                break;
            default:
                throw new BizFailException("channel not supported"+channelCode.getCode());
        }
    }

    private void payForWy(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
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
            String productDescription = "991361077780001|00006751";
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

    private void payForAliH5(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        PayRequestBack payRequestBack = new PayRequestBack();
        payRequestBack.setMchId(payRequest.getMchId());
        payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());
        try {
            String merchantNo = "990581077770019";
            String terminalNo = "77700474";
            String key = "SU62HD9438";
            String attach = "991391077780003";
            String merchantURL = "http://huifufu.cn/openapi/zf/pay_notice";
            String productDescription = "991391077780003|00008573";
            String payMoney = String.valueOf(payRequest.getTotalFee());
            String productName = URLEncoder.encode(payRequest.getBody(),"UTF-8") ;
            String inTradeOrderNo = payRequest.getOutTradeNo();
            String payType = "18";
            String alipayPayType = "105";
            String validDate = "10m";
            String signMsg = DigestUtils.md5Hex(merchantNo+terminalNo+payMoney+inTradeOrderNo+payType+alipayPayType+key).toUpperCase();

            Map<String,Object> map = new HashMap<>();
            map.put("merchantNo",merchantNo);
            map.put("terminalNo",terminalNo);
            map.put("payMoney",payMoney);
            map.put("productName",productName);
            map.put("productDescription",URLEncoder.encode(productDescription,"UTF-8"));
            map.put("inTradeOrderNo",inTradeOrderNo);
            map.put("payType",payType);
            map.put("alipayPayType",alipayPayType);
            map.put("validDate",validDate);
            map.put("merchantURL",merchantURL);
            map.put("attach",attach);
            map.put("signMsg",signMsg);

            String url = "http://paygw.guangyinwangluo.com/swPayInterface/aliH5";

            for(int i=0;i<map.keySet().size();i++) {
                if(i==0) {
                    url = url+"?"+map.keySet().toArray()[i]+"="+map.get(map.keySet().toArray()[i]);
                } else {
                    url = url+"&"+map.keySet().toArray()[i]+"="+map.get(map.keySet().toArray()[i]);
                }
            }

//            Map<String,Object> result = zfClient.unifiedorder(map);
            response.sendRedirect(url);

            payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);
            payRequestBack.setMessage("下单成功");

            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            payService.remoteSuccess(payRequest,payRequestBack);
        } catch (Exception e) {
            logger.error("remote call failed,"+e.getMessage()+","+payRequest.getOutTradeNo());
            payRequestBack.setErrcode(CodeManager.PAY_FAILED);
            payRequestBack.setMessage("下单失败");

            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            payService.payFailed(payRequest.getOutTradeNo());
        }
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
