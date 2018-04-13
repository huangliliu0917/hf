package com.hf.core.callback.quik;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.utils.BeanContextUtils;
import org.apache.log4j.Logger;

import com.hfb.merchant.quick.common.Status;
import com.hfb.merchant.quick.entity.Pay;
import com.hfb.merchant.quick.handler.QuickEntityPay;
import com.hfb.merchant.quick.sercret.CertUtil;
import com.hfb.merchant.quick.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 支付处理
 * @author HFB
 *
 */
public class PayServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PayServlet.class);
	private CacheService cacheService = BeanContextUtils.getBean("cacheServiceImpl");
	@Autowired
	@Qualifier("hfbTradingBiz")
	private TradingBiz tradingBiz;
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		System.out.println("请求过来啦");
		String redirectPath = "pay.jsp";
		String msg = "处理失败，请重试";
		Map<String, String> resultMap = null;
		Map<String,Object> requestMap = new HashMap<>();

		try{
			String merchantNo = request.getParameter("merchantNo");          //商户编号
			String notifyUrl = request.getParameter("notifyUrl");            //结果通知地址
			String paygateReqUrl = request.getParameter("tranReqUrl");       //网关地址
			String tranSerialNum = DateUtil.getTimeMillis();                 //交易流水号
			String tranDate = DateUtil.getDate();                            //交易日期
			String tranTime = DateUtil.getTime();                            //交易时间
			String bindId = request.getParameter("bindId");                  //绑卡ID
			String amount = request.getParameter("amount");                  //交易金额
			String bizType = request.getParameter("bizType");                //业务代码
			String buyerName = request.getParameter("buyerName");            //买家姓名
			String contact = request.getParameter("contact");                //买家联系方式
			String goodsName = request.getParameter("goodsName");            //商品名称
			String goodsInfo = request.getParameter("goodsInfo");            //商品信息
			
			String valid = request.getParameter("valid");                    //有效期
			if(valid == null) {
				valid = "";
			}
			String cvn2 = request.getParameter("cvn2");                      //CVN2
			if(cvn2 == null) {
				cvn2 = "";
			}
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
			Pay pay = new Pay(merchantNo, tranDate, tranTime, remark, ext1, ext2, yUL1, yUL2, yUL3, tranSerialNum, bindId, amount, bizType, 
					valid, cvn2, goodsName, goodsInfo, notifyUrl, buyerName, contact);
					
			//如果绑卡，则直接支付
			resultMap = QuickEntityPay.sendModelPay(certUtil, pay, paygateReqUrl);
				
			if(Status.STATUS_0038.equals(resultMap.get("rtnCode").toString())){
				redirectPath = "verify.jsp";
				
				request.setAttribute("tranSerialNum", tranSerialNum);
				request.setAttribute("amount", amount);
				request.setAttribute("oriTranCode", Status.pay_trancode);
			}else{
				msg = resultMap.get("rtnMsg").toString();
				redirectPath = "result.jsp";
			}
		}catch(Exception e){
			e.printStackTrace();
			redirectPath = "result.jsp";
		}finally{
			request.setAttribute("resultMap", resultMap);
			request.setAttribute("errorMsg", msg);
			request.getRequestDispatcher(redirectPath).forward(request, response);
		}
	}
}
