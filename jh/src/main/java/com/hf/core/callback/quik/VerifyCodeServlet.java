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
import com.hfb.merchant.quick.entity.VerifyCode;
import com.hfb.merchant.quick.handler.QuickEntityPay;
import com.hfb.merchant.quick.sercret.CertUtil;
import com.hfb.merchant.quick.util.DateUtil;

/**
 * 快捷支付-验证手机验证码
 * @author FYW
 * @date 2016/07/22
 */
public class VerifyCodeServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(VerifyCodeServlet.class);
	private CacheService cacheService = BeanContextUtils.getBean("cacheServiceImpl");
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		String redirectPath = "result.jsp";
		String msg = "处理失败，请重试";
		Map<String, String> map = null;
		try{
			
			String merchantNo = request.getParameter("merchantNo");          //商户编号
			String paygateReqUrl = request.getParameter("tranReqUrl");       //交易地址（网关）
			String tranSerialNum = request.getParameter("tranSerialNum");    //上一轮交易流水号
			String phoneCode = request.getParameter("phoneCode");            //手机验证码
			String oriTranCode = request.getParameter("oriTranCode");        //原交易码
			String tranDate = DateUtil.getDate();                            //交易日期
			String tranTime = DateUtil.getTime();                            //交易时间
			String yUL1 = request.getParameter("YUL1");                    //预留字段1
			if(yUL1 == null) {
				yUL1 = "";
			}
			String yUL2 = request.getParameter("YUL2");                    //预留字段2
			if(yUL2 == null) {
				yUL2 = "";
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
			VerifyCode verifyCode = new VerifyCode(merchantNo, oriTranCode, tranSerialNum, tranDate, tranTime, phoneCode, yUL1, yUL2);
			
			// 对发送的信息，进行加密，加签，发送至网关，并对网关返回的信息内容进行解析，验签操作
			map = QuickEntityPay.sendModelPay(certUtil, verifyCode, paygateReqUrl);
			msg = map.get("rtnMsg").toString();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			request.setAttribute("resultMap", map);
			request.setAttribute("errorMsg", msg);
			request.getRequestDispatcher(redirectPath).forward(request, response);
		}
	}
	
	
}
