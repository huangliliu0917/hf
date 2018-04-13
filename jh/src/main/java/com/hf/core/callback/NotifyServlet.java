package com.hf.core.callback;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.trade.TradingBiz;
import com.hf.core.dao.local.PayRequestDao;
import com.hf.core.model.po.PayRequest;
import com.hf.core.utils.BeanContextUtils;
import com.hfb.merchant.code.sercret.CertUtil;
import com.hfb.merchant.code.util.ResponseUtil;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author qml
 * 前台通知
 *
 */
public class NotifyServlet extends HttpServlet {
	protected org.slf4j.Logger logger = LoggerFactory.getLogger(NotifyServlet.class);
	private CacheService cacheService = BeanContextUtils.getBean("cacheServiceImpl");
	private TradingBiz hfbTradingBiz = BeanContextUtils.getBean("hfbTradingBiz");
	private PayRequestDao payRequestDao = BeanContextUtils.getBean("payRequestDao");

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		logger.info("hfb msg received");
		// 私钥文件路径
		String privateKey = cacheService.getRootPath()+"/certs/CS20180409031247_20180411131244514.pfx";
		// 公钥文件路径
		String publicKey = cacheService.getRootPath()+"/certs/SS20180409031247_20180411131244514.cer";
		// 密钥密码
		String KeyPass = "408916";
		// 加密工具类的创建
		CertUtil certUtil = new CertUtil(publicKey, privateKey, KeyPass, true);

		TreeMap<String, String> transMap = new TreeMap<String, String>();
		Enumeration<String> enu = request.getParameterNames();
		String t = null;
		while (enu.hasMoreElements()) {
			t = enu.nextElement();
			transMap.put(t, request.getParameter(t));
		}

		Map<String, String> map=null;
		try {
			//对通知进行验签操作
			map = ResponseUtil.parseResponse(transMap, certUtil);
			hfbTradingBiz.handleCallBack(map);
			String outTradeNo = map.get("tranFlow");
			PayRequest payRequest = payRequestDao.selectByTradeNo(outTradeNo);
			hfbTradingBiz.notice(payRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("前台通知的响应报文"+map);
	}

}
