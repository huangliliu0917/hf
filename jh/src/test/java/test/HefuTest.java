package test;

import com.google.gson.Gson;
//import com.hfb.merchant.code.handler.ModelPay;
//import com.hfb.merchant.code.model.Barcode;
//import com.hfb.merchant.code.sercret.CertUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class HefuTest {
    /**
     * 条码支付demo
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


		/*切换正式环境商户号需要到正式环境商户后台 安全中心--证书管理 功能中下载正式并启用商户证书秘钥替换掉DEMO中的证书秘钥
		进入证书管理页面下载证步骤
		1、点击  生成授权码按钮 生成授权码并复制
		2、点击 授权码后的生成新证书按钮跳转到证书生成页面
		3、输入上一步操作的授权码、商户号、密钥(证书生成页面密钥及为私钥证书密码)等相关信息
		4、点击提交可下载公私钥证书
		5、下载完证书后回到证书管理列表，在列表上查看刚才下载的证书
		6、点击启用操作 启用刚才下载的证书*/


        //公钥私钥这两个文件在这个项目中WebContent中WEB-INF中certs中
        // 私钥文件路径
        String privateKey = "/Users/tengfei/projects/hf/jh/src/main/webapp/WEB-INF/certs/CS20170907011890_20170907170049931.pfx";
        // 公钥文件路径
        String publicKey = "/Users/tengfei/projects/hf/jh/src/main/webapp/WEB-INF/certs/SS20170907011890_20170907170049931.cer";
        // 密钥密码
        String KeyPass = "a111111";
        // 网关路径
        String paygateReqUrl = "http://paygate.hefupal.cn/paygate/v1/smpay";

        // 必输参数
        // 商户编号
        String merchantNo = "S20170907011890";
        // 交易日期
        String tranDate = DateFormatUtils.format(new Date(),"yyyyMMdd");
        // 交易时间
        String tranTime = DateFormatUtils.format(new Date(),"HHmmss");
        // 交易金额
        String amount = "11";
        // 付款摘要
        String remark = "买东西";
        // 用户设备外网IP
        String yUL3 = "47.52.111.205";
        // 交易流水号
        String tranFlow = String.valueOf(new Date().getTime());
        // 支付类型
        String payType = "15";
        // 入驻ID
        String bindId = "YSM201709141140144958595809101";
        // 结果通知地址
        String notifyUrl = "http://c0d74991.ngrok.io/hfbcode/notify.do";
        // 业务代码
        String bizType = "01";
        // 商品名称
        String goodsName = "西瓜";
        // 买家姓名
        String buyerName = "张三";
        // 买家ID
        String buyerId = "2345678";
        // 支付场景
        String scene = "-2";
        // 授权码
        String authCode = "11";

        // 可选参数
        // 商品信息
        String goodsInfo = "水果商品";
        // 商品数量
        String goodsNum = "10";
        // 商户扩展
        String ext1 = "22";
        // 商户扩展
        String ext2 = "22";
        // 平台拓展
        String yUL1 = "22";
        // 平台拓展
        String yUL2 = "22";
        // 买家联系方式
        String contact = "13734107865";

        // 创建加密的工具类
//        CertUtil certUtil = new CertUtil(publicKey, privateKey, KeyPass, true);
//
//        // 实体化对象数据封装，并对数据进行校验
//        Barcode barcode = new Barcode(merchantNo, tranDate, tranTime, amount, remark, ext1, ext2, yUL1, yUL2, yUL3,
//                tranFlow, payType, bindId, scene, authCode, notifyUrl, bizType, goodsName, goodsInfo, goodsNum,
//                buyerName, contact, buyerId);
//
//        // 对发送的信息，进行加密，加签，发送至网关，并对网关返回的信息内容进行解析，验签操作
//        Map<String, String> map = ModelPay.sendModelPay(certUtil, barcode, paygateReqUrl);

        //输出网关返回的内容
//        System.out.println("条码支付的响应报文"+new Gson().toJson(map));
    }

    @Test
    public void testWyPay() {

    }
}
