package com.hf.core.biz.trade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.contants.CodeManager;
import com.hf.base.enums.ChannelCode;
import com.hf.base.enums.ChannelProvider;
import com.hf.base.enums.PayRequestStatus;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.utils.Utils;
import com.hf.core.dao.remote.Md5Utils;
import com.hf.core.dao.remote.SendData;
import com.hf.core.dao.remote.WhpClient;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.PayRequestBack;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WhpTradingBiz extends AbstractTradingBiz {
    @Autowired
    private WhpClient whpClient;

    enum PayType {
        WX_H5("pay.weixin.h5",ChannelCode.WX_H5.getCode()),
        WX_SM("pay.weixin.qrcode",ChannelCode.WX_SM.getCode()),
        ALI_H5("pay.alipay.wap",ChannelCode.ALI_H5.getCode()),
        ALI_SM("pay.alipay.qrcode",ChannelCode.ALI_ZS.getCode());

        private String code;
        private String localCode;

        PayType(String code,String localCode) {
            this.code = code;
            this.localCode = localCode;
        }

        public static PayType parse(String localCode) {
            for(PayType payType:PayType.values()) {
                if(StringUtils.equals(payType.localCode,localCode)) {
                    return payType;
                }
            }
            return null;
        }
    }

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.WHP;
    }

    @Override
    public void doPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        if (payRequest.getService().equals(ChannelCode.QQ_SM.getCode())) {
            qqQrPay(payRequest,request,response);
            return;
        }

        Map<String,Object> map = new HashMap<>();
        String mchid = "26015";
        String cipherCode = "qgS0InuHKnaxb7t4HrePnpHLJQOYKtYZ";
        map.put("mchid",mchid);
        if(null == PayType.parse(payRequest.getService())) {
            throw new BizFailException("通道"+payRequest.getService()+"不支持");
        }
        map.put("paytype",PayType.parse(payRequest.getService()).code);
        map.put("time",new SimpleDateFormat("yyyyMMddHHmmss").format(payRequest.getCreateTime()));
        map.put("amount",payRequest.getTotalFee());
        map.put("out_tradeid",payRequest.getOutTradeNo());
        map.put("subject",payRequest.getBody());
        if(StringUtils.isNotEmpty(payRequest.getRemark())) {
            map.put("attach",payRequest.getRemark());
        }
        map.put("clientip",payRequest.getCreateIp());
        map.put("version","1.0");
        map.put("notifyurl","http://huifufu.cn/openapi/whp/pay_notice");

        //paytype+mchid+amount+out_tradeid+time+商户密钥
        String sign = DigestUtils.md5Hex(map.get("paytype")+mchid+map.get("amount")+map.get("out_tradeid")+map.get("time")+cipherCode);
        map.put("sign",sign);

        Map<String,Object> result = whpClient.unifiedorder(map);

        PayRequestBack payRequestBack = new PayRequestBack();
        payRequestBack.setMchId(payRequest.getMchId());
        payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());
        if(MapUtils.isEmpty(result)) {
            //failed
            payRequestBack.setErrcode(String.valueOf(result.get("code")));
            payRequestBack.setMessage(String.valueOf(result.get("message")));
            payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
            return;
        }

        int code = new BigDecimal(result.get("code").toString()).intValue();
        if(code == 1) {
            payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);
            payRequestBack.setMessage(String.valueOf(result.get("message")));
            payRequestBack.setPageContent(String.valueOf(result.get("type")));
            payRequestBack.setCodeUrl(String.valueOf(result.get("pay_info")));
            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            payService.remoteSuccess(payRequest,payRequestBack);
            try {
                logger.info("user-agent:"+request.getHeader("user-agent"));
                if(request.getHeader("user-agent").contains("Mozilla")) {
                    response.sendRedirect(String.valueOf(result.get("pay_info")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        payRequestBack.setErrcode(String.valueOf(result.get("code")));
        payRequestBack.setMessage(String.valueOf(result.get("message")));
        payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
        return;
    }

    private void qqQrPay(PayRequest payRequest, HttpServletRequest request, HttpServletResponse response) {
        SendData sd = new SendData();

        String url = "";
        Date d = new Date();
        /*********k27**********/
        String mid = "512004";
        String submid = "0180005131748";
        String userkey = "fab256a197494e64b447c8087d72e163";//密钥


        String strSign = "";
        Map<String, Object> map = new HashMap<>();

//		6.2下单接口
        url = "http://www.82ypay.com/qqpay/trade";
        map.put("accountType" , "");//用户类型  支付类型gateway该参数有效且必填
        map.put("amount" , new BigDecimal(payRequest.getTotalFee()).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_DOWN));//交易金额 y
        map.put("appId" , "");//公众号appId   支付类型wxPub该参数有效且必填
        map.put("authCode" , "");//支付授权码       以下支付类型该参数有效且必填：1.wxMicro	 2.alipayMicro	3.jdMicro	4. qqMicro
        map.put("bankType" , "");//银行类型   支付类型gateway、qpay该参数有效且必填
        map.put("body" , payRequest.getBody());//商品描述 y
        map.put("callbackUrl" , "");//前台同步回调地址
        map.put("cardInfo" , "");//卡信息     支付类型qpay该参数有效且必填;
        map.put("channel" , "qqQr");// 支付类型  y
        map.put("limitPay" , "");//限定支付方式   以下支付类型该参数有效：  1.wxPub  2.wxPubQR  3.wxMicro  4.wxApp  5.qqQr  6.gateway
        map.put("mchId" , mid);// 代理商号 y
        map.put("mobileAppId" , "");//移动APPID  支付类型wxApp该参数有效且必填
        map.put("mwebId" , "");//应用场景ID 支付类型wxH5该参数有效且必填;  IOS：IOS应用唯一标识  Android：应用在安卓分发市场中的应用名  WAP：WAP网站的首页URL
        map.put("mwebName" , "");//应用场景名称
        map.put("mwebType" , "");//应用场景类型
        map.put("notifyUrl" , "http://huifufu.cn/openapi/whp/pay_notice");//后台异步回调地址
        map.put("openId" , "");//公众号openId  支付类型wxPub该参数有效且必填
        map.put("outTradeNo" , payRequest.getOutTradeNo());//商户订单号 y
        map.put("scenesType" , "");//应用场景类型  支付类型gateway该参数有效且必填
        map.put("settleCycle" , "0");//结算周期
        map.put("subMchId" , submid);// 商户号 y
        map.put("timePaid" , "");//订单创建时间
        map.put("tradeType" , "cs.pay.submit");// 交易类型 y
        map.put("userId" , "");//支付宝userId  支付类型alipayH5该参数有效且必填
        map.put("version" , "1.5");// 版本 y

        SendData.removeNullValue(map);//除去空值

        String sign = Utils.encrypt(map,userkey);

        map.put("sign" , sign);//签名

        String result = sd.onlineBankJson(url, SendData.mapToJson(map,null).toString());

        Map<String,Object> res = new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());

        PayRequestBack payRequestBack = new PayRequestBack();
        payRequestBack.setMchId(payRequest.getMchId());
        payRequestBack.setOutTradeNo(payRequest.getOutTradeNo());

        if(MapUtils.isEmpty(res)) {
            //failed
            payRequestBack.setErrcode(CodeManager.FAILED);
            payRequestBack.setMessage("交易失败，无法回");
            payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
            return;
        }

        if(new BigDecimal(res.get("resultCode").toString()).intValue() == 0) {
            if(Utils.checkEncrypt(res,userkey)) {
                //验签失败
                payRequestBack.setErrcode(CodeManager.CIPHER_CHECK_FAILED);
                payRequestBack.setMessage("交易失败");
                logger.error("验签失败："+new Gson().toJson(res));
                payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
                return;
            }

            //success
            payRequestBack.setErrcode(CodeManager.PAY_SUCCESS);
            payRequestBack.setMessage(String.valueOf(res.get("errCodeDes")));
            payRequestBack.setCodeUrl(String.valueOf(res.get("codeUrl")));
            payRequest = payRequestDao.selectByTradeNo(payRequest.getOutTradeNo());
            payService.remoteSuccess(payRequest,payRequestBack);
        } else {
            //failed
            payRequestBack.setErrcode(CodeManager.FAILED);
            payRequestBack.setMessage(String.valueOf(res.get("errCodeDes")));
            payService.payFailed(payRequest.getOutTradeNo(),payRequestBack);
        }
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        if(ChannelCode.parseFromCode(map.get("service")) == ChannelCode.QQ_SM) {
            return handlerQqQrCallBack(map);
        }

        String version = map.get("version");
        String sign = map.get("sign");
        String pay_result = map.get("pay_result");
        String tradeid = map.get("tradeid");
        String out_tradeid = map.get("out_tradeid");
        String amount = map.get("amount");
        String attach = map.get("attach");
        String time = map.get("time");
        String out_transactionid = map.get("out_transactionid");

        PayRequest payRequest = payRequestDao.selectByTradeNo(out_tradeid);
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;

        if(payRequestStatus == PayRequestStatus.OPR_SUCCESS) {
            return new Gson().toJson(com.hf.base.utils.MapUtils.buildMap("resCode","0000"));
        }

        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        if(new BigDecimal(pay_result).intValue() == 0) {
            payService.paySuccess(out_tradeid);
        } else {
            payService.payFailed(out_tradeid);
        }

        return "SUCCESS";
    }

    private String handlerQqQrCallBack(Map<String, String> map) {
        map.remove("service");

        Map<String,Object> objMap = new HashMap<>();
        objMap.putAll(map);
        if(!Utils.checkEncrypt(objMap,"fab256a197494e64b447c8087d72e163")) {
            throw new BizFailException("支付结果验签失败");
        }

        int resultCode = new BigDecimal(map.get("resultCode")).intValue();
        String errCode = map.get("errCode");
        String errCodeDes = map.get("errCodeDes");
        String channel = map.get("channel");
        String mchId = map.get("mchId");
        String outTradeNo = map.get("outTradeNo");
        String outChannelNo = map.get("outChannelNo");

        PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
        PayRequestStatus payRequestStatus = PayRequestStatus.parse(payRequest.getStatus()) ;

        if(payRequestStatus == PayRequestStatus.OPR_SUCCESS) {
            return new Gson().toJson(com.hf.base.utils.MapUtils.buildMap("resCode","0000"));
        }

        if(payRequestStatus != PayRequestStatus.PROCESSING && payRequestStatus!=PayRequestStatus.OPR_GENERATED) {
            throw new BizFailException("status invalid");
        }

        if(resultCode == 0) {
            payService.paySuccess(outTradeNo);
        } else {
            payService.payFailed(outTradeNo);
        }
        return "SUCCESS";
    }

    @Override
    public Map<String, Object> query(PayRequest payRequest) {
        return null;
    }
}
