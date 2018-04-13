package com.hf.core.callback.quik;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hf.core.biz.service.CacheService;
import com.hf.core.utils.BeanContextUtils;
import org.apache.log4j.Logger;

import com.hfb.merchant.quick.common.Status;
import com.hfb.merchant.quick.entity.Refund;
import com.hfb.merchant.quick.handler.QuickEntityPay;
import com.hfb.merchant.quick.sercret.CertUtil;
import com.hfb.merchant.quick.util.DateUtil;
/**
 * 退款交易
 * @author FYW
 * @date 2015-12-9
 */
public class RefundServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RefundServlet.class);
	private CacheService cacheService = BeanContextUtils.getBean("cacheServiceImpl");
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		System.out.println("请求过来啦");
		String redirectPath = "refund.jsp";
		String msg = "处理失败，请重试";
		Map<String, String> resultMap = null;
		try{

			String merchantNo = request.getParameter("merchantNo");
			String tranSerialNum = DateUtil.getTimeMillis();
			String tranDate = DateUtil.getDate();
			String tranTime = DateUtil.getTime();
			String paygateReqUrl = request.getParameter("tranReqUrl");
			String oldTranSerialNum = request.getParameter("oldTranSerialNum");
			String amount = request.getParameter("amount");
			String refundReason = request.getParameter("refundReason");
			
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
			Refund refund = new Refund(merchantNo, tranDate, tranTime, remark, ext1, ext2, yUL1, yUL2, yUL3, tranSerialNum, amount, refundReason, oldTranSerialNum);
			
			resultMap = QuickEntityPay.sendModelPay(certUtil, refund, paygateReqUrl);
			
			msg = resultMap.get("rtnMsg").toString();
			redirectPath = "result.jsp";
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
