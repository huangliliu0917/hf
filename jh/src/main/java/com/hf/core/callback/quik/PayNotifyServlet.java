package com.hf.core.callback.quik;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hf.core.biz.service.CacheService;
import com.hf.core.utils.BeanContextUtils;
import com.hf.core.utils.ParamQuickUtil;
import org.apache.log4j.Logger;

import com.hfb.merchant.quick.sercret.CertUtil;
import com.hfb.merchant.quick.util.ParamUtil;

/**
 * 接受异步通知
 * @author FYW
 * @date 2015-12-9
 */
public class PayNotifyServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PayNotifyServlet.class);
	private CacheService cacheService = BeanContextUtils.getBean("cacheServiceImpl");
	
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		try{
			//利用treeMap对参数按key值进行排序
			Map<String, String> transMap = ParamQuickUtil.getParamMap(request);
			//获取签名
			String sign = (String) transMap.get("sign");
			sign = sign.replaceAll(" ", "+");
			transMap.remove("sign");
			//验签
			String transData = ParamUtil.getSignMsg(transMap);
			// 测试商户公私钥证书在本工程src/main/webapp/WEB-INF/cert下

			// 私钥文件路径
			String privateKey = cacheService.getRootPath()+"/certs/CS20180409031247_20180411131244514.pfx";
			// 公钥文件路径
			String publicKey = cacheService.getRootPath()+"/certs/SS20180409031247_20180411131244514.cer";
			// 密钥密码
			String KeyPass = "408916";
			

			boolean result = false;
			// 加密工具类的创建
			CertUtil certUtil = new CertUtil(publicKey, privateKey, KeyPass, true);
			
			// 验签
			result = certUtil.verify(transData, sign);
			if(!result){
				transMap.clear();
				transMap.put("tranData", transData);
				transMap.put("sign", sign);
				transMap.put("msg", "验签失败");
			}else{
				logger.info("=======================================================================");
				logger.info("接收到通知-->" + transMap);
				logger.info("=======================================================================");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			response.getWriter().print("YYYYYY");
		}
	}
	
	
}
