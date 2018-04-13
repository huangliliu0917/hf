package com.hf.core.biz.trade;

import com.hf.base.enums.ChannelProvider;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.model.po.PayRequest;
import com.hf.core.model.po.UserGroup;
import com.hfb.merchant.code.handler.ModelPay;
import com.hfb.merchant.code.model.Barcode;
import com.hfb.merchant.code.model.ScanPay;
import com.hfb.merchant.code.sercret.CertUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HfbTradingBiz extends AbstractTradingBiz {

    @Override
    public ChannelProvider getChannelProvider() {
        return ChannelProvider.HFB;
    }

    @Override
    public void doPay(PayRequest payRequest) {
        UserGroup userGroup = cacheService.getGroup(payRequest.getMchId());
        // 私钥文件路径
        String privateKey = cacheService.getRootPath()+"/certs/CS20180409031247_20180410075945426.pfx";
        // 公钥文件路径
        String publicKey = cacheService.getRootPath()+"/certs/SS20180409031247_20180410075945426.cer";
        // 密钥密码
        String KeyPass = "408916";
        // 网关路径
        String paygateReqUrl = "https://cashier.hefupal.com/paygate/v1/smpay";
        //todo 动态选择
        String merchantNo = "S20180409031247";
        String version = "v1";
        String channelNo = "05";
        String tranCode = "YS1003";
        // 交易流水号
        String tranFlow = payRequest.getOutTradeNo();
        Date date = new Date();
        // 交易日期
        String tranDate = DateFormatUtils.format(date,"yyyyMMdd");
        // 交易时间
        String tranTime = DateFormatUtils.format(date,"HHmmss");
        // 交易金额
        String amount = String.valueOf(payRequest.getTotalFee());
        String payType = PayType.QQ.code;
        String bindId = "YSM201804091743160525443053023";
        String notifyUrl = StringUtils.isEmpty(payRequest.getOutNotifyUrl())?userGroup.getCallbackUrl():payRequest.getOutNotifyUrl();
        String bizType = "01";
        String goodsName = StringUtils.isEmpty(payRequest.getRemark())?"支付"+amount:payRequest.getRemark();
        String goodsInfo = payRequest.getBody();
        String buyerName = userGroup.getOwnerName();
        String buyerId = String.valueOf(userGroup.getId());
        String remark = payRequest.getBody();
        String yUL3 = payRequest.getCreateIp();
        String contact = userGroup.getTel();

        // 创建加密的工具类
        CertUtil certUtil = new CertUtil(publicKey, privateKey, KeyPass, true);
        String ext1 = "";
        String ext2 = "";
        String yUrl1 = "";
        String yUL2 = "";
        try {
            ScanPay scanPay = new ScanPay(merchantNo,tranDate,tranTime,amount,remark,ext1,ext2,yUrl1,yUL2,yUL3,tranFlow,payType,bindId,notifyUrl,bizType,goodsName,goodsInfo,"1",buyerName,contact,buyerId);
            // 对发送的信息，进行加密，加签，发送至网关，并对网关返回的信息内容进行解析，验签操作
            Map<String, String> map = ModelPay.sendModelPay(certUtil, scanPay, paygateReqUrl);

            String rtnCode = map.get("rtnCode");
            String rtnMsg = map.get("rtnMsg");
            String qrCodeURL = map.get("qrCodeURL");


        } catch (Exception e) {
            logger.error(e.getMessage()+",tradeNo:"+payRequest.getOutTradeNo());
            throw new BizFailException(e.getMessage());
        }
    }

    @Override
    public String handleCallBack(Map<String, String> map) {
        return null;
    }

    @Override
    public void notice(PayRequest payRequest) {

    }

    enum PayType {
        WX("1","微信扫码支付"),
        ALI("2","支付宝扫码支付"),
        WX_P("3","微信公众号支付"),
        ALI_S("4","支付宝服务窗支付"),
        WX_T("5","微信条码支付"),
        ALI_T("6","支付宝条码支付"),
        QQ("7","钱包扫码支付"),
        WX_H5("8","H5/WAP 支付"),
        ALI_H5("9","支付宝 H5/WAP 支付"),
        WX_APP("10","微信 APP 支付"),
        QQ_T("11","QQ 钱包条码支付"),
        ALI_APP("12","支付宝 APP 支付"),
        QQ_WAP("13","QQ 钱包 WAP 支付"),
        QQ_P("14","QQ 钱包公众号支付"),
        YL("15","银联钱包支付"),
        JD("16","京东扫码支付"),
        JD_WAP("17","京东 WAP 支付"),
        Y("18","翼支付 JS 支付"),
        BAIDU("19","百度钱包扫码支付"),
        BAIDU_T("20","百度钱包条码支付"),
        YL_T("21","银联钱包条码支付"),
        YMDF("22","一码多付"),
        CARD("23","刷卡支付");

        private String code;
        private String name;

        PayType(String code,String name) {
            this.code = code;
            this.name = name;
        }
    }

    enum RtnCode {
        _000("S","0000","交易成功"),
        _001("E","0001","交易失败"),
        _002("R","0002","交易处理中"),
        _003("R","0003","连接超时"),
        _004("E","0004","系统维护中，暂停交易"),
        _005("E","0005","交易流水号重复"),
        _006("E","0006","查询的交易不存在"),
        _007("E","0007","原支付流水不存在"),
        _008("E","0008","结算账户错误/商户鉴权失败"),
        _009("E","0009","结算账户户名错误"),
        _010("E","0010","结算账户状态不正常"),
        _011("E","0011","证件类型错误"),
        _012("E","0012","证件号码错误"),
        _013("E","0013","身份证号码不匹配"),
        _014("E","0014","手机号错误"),
        _015("E","0015","退款金额超过剩余可退金额"),
        _016("E","0016","支付金额超限"),
        _017("E","0017","余额不足"),
        _018("E","0018","不支持该卡交易"),
        _019("E","0019","支付密码输入超限"),
        _020("E","0020","其他错误"),
        _021("E","0021","交易已完结，不允许退款"),
        _022("E","0022","金额有误"),
        _023("E","0023","支付授权码无效"),
        _024("E","0024","商户重复入驻"),
        _025("E","0025","未查到商户入驻信息，请确认入驻ID是否正确"),
        _026("E","0026","报文不合法，包括格式不合法、必输请求项为空等"),
        _027("E","0027","商户入驻信息过期"),
        _028("E","0028","结算账户不支持，请确认帐户是否正常"),
        _029("R","0029","交易已受理"),
        _030("R","0030","交易处理异常");

        private String type;
        private String code;
        private String desc;

        RtnCode(String type,String code,String desc) {
            this.type = type;
            this.code = code;
            this.desc = desc;
        }

        public List<RtnCode> successCode() {
            return Arrays.asList(_000,_002,_021,_029);
        }

        public List<RtnCode> failedCode() {
            return Arrays.asList(_001,_004,_008,_009,_010,_011,_012,_013,_014,_015,_016,_017,_018,_019,_020,_022,_023,_024,_025,_026);
        }
    }
}
