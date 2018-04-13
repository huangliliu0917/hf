package com.hf.core.callback.quik;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hf.core.biz.service.CacheService;
import com.hf.core.utils.BeanContextUtils;
import com.hfb.merchant.code.util.DateUtil;
import com.hfb.merchant.quick.common.Status;
import com.hfb.merchant.quick.entity.PayNotCard;
import com.hfb.merchant.quick.handler.QuickEntityPay;
import com.hfb.merchant.quick.sercret.CertUtil;
import org.apache.log4j.Logger;

/**
 * 签约绑卡
 * @author FYW
 * @date 2015-12-9
 */
public class PayNotCardServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PayNotCardServlet.class);
	private CacheService cacheService = BeanContextUtils.getBean("cacheServiceImpl");

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		System.out.println("请求过来啦");
		String redirectPath = "payNotCard.jsp";
		String msg = "处理失败，请重试";
		Map<String, String> resultMap = null;
		try{
			
			String merchantNo = request.getParameter("merchantNo");         //商户编号
			String notifyUrl = request.getParameter("notifyUrl");           //结果通知地址
			String paygateReqUrl = request.getParameter("tranReqUrl");      //网关地址
			String tranSerialNum = DateUtil.getTimeMillis();                //交易流水号
			String userId = request.getParameter("userId");                 //用户ID
			String tranDate = DateUtil.getDate();                           //交易日期
			String tranTime = DateUtil.getTime();                           //交易时间
			String cardNum = request.getParameter("cardNum");               //银行账号
			String userName = request.getParameter("userName");             //账户名称
			String certificateType = request.getParameter("certificateType");//证件类型
			String certificateNum = request.getParameter("certificateNum");  //证件号码
			String mobile = request.getParameter("mobile");                  //银行预留手机号
			String amount = request.getParameter("amount");                  //交易金额
			String bizType = request.getParameter("bizType");                //业务代码
			String valid = request.getParameter("valid");                    //有效期
			if(valid == null) {
				valid = "";
			}
			String cvn2 = request.getParameter("cvn2");                      //CVN2
			if(cvn2 == null) {
				cvn2 = "";
			}
			String goodsName = request.getParameter("goodsName");            //商品名称
			String goodsInfo = request.getParameter("goodsInfo");            //商品信息
			String buyerName = request.getParameter("buyerName");            //买家姓名
			String contact = request.getParameter("contact");                //买家联系方式
			
			String remark = request.getParameter("remark");                    //备注字段
			if(remark == null) {
				remark = "";
			}
			String ext1 = request.getParameter("ext1");                    //扩展字段1
			if(ext1 == null) {
				ext1 = "";
			}
			String ext2 = request.getParameter("ext2");                    //扩展字段2
			if(ext2 == null) {
				ext2 = "";
			}
			String yUL1 = request.getParameter("YUL1");                    //预留字段1
			if(yUL1 == null) {
				yUL1 = "";
			}
			String yUL2 = request.getParameter("YUL2");                    //预留字段2
			if(yUL2 == null) {
				yUL2 = "";
			}
			String yUL3 = request.getParameter("YUL2");                    //预留字段3
			if(yUL3 == null) {
				yUL3 = "";
			}

			// 测试商户公私钥证书在本工程src/main/webapp/WEB-INF/cert下

			// 私钥文件路径
			String privateKey = cacheService.getRootPath()+"/certs/CS20180409031247_20180411131244514.pfx";
			// 公钥文件路径
			String publicKey = cacheService.getRootPath()+"/certs/SS20180409031247_20180411131244514.cer";
			// 密钥密码
			String KeyPass = "408916";
			
			
			// 加密工具类的创建
			CertUtil certUtil = new CertUtil(publicKey, privateKey, KeyPass, true);
			
			// 实体类数据的封装 并进行数据校验
			PayNotCard payNotCard = new PayNotCard(merchantNo, tranDate, tranTime, remark, ext1, ext2, yUL1, yUL2, yUL3, tranSerialNum, userId,
					cardNum, userName, certificateType, certificateNum, mobile, amount, bizType, valid, cvn2, goodsName, goodsInfo, notifyUrl, buyerName, contact);
			
			// 对发送的信息，进行加密，加签，发送至网关，并对网关返回的信息内容进行解析，验签操作
			resultMap = QuickEntityPay.sendModelPay(certUtil, payNotCard, paygateReqUrl);
			
			if(!Status.STATUS_0023.equals(resultMap.get("rtnCode").toString())){
				msg = resultMap.get("rtnMsg").toString();
				redirectPath = "result.jsp";
			}
			
			
			if(Status.STATUS_0038.equals(resultMap.get("rtnCode").toString())){
				redirectPath = "verify.jsp";
				
				request.setAttribute("tranSerialNum", tranSerialNum);
				request.setAttribute("oriTranCode", Status.bind_and_pay_trancode);
				request.setAttribute("cardNum", cardNum);
			}else{
				msg = resultMap.get("rtnMsg").toString();
				redirectPath = "result.jsp";
			}			
		}catch(Exception e){
			e.printStackTrace();
			redirectPath = "result.jsp";
		}finally{
			request.setAttribute("asynMsg", resultMap);
			request.setAttribute("errorMsg", msg);
			request.getRequestDispatcher(redirectPath).forward(request, response);
		}
		return;
	}
}
