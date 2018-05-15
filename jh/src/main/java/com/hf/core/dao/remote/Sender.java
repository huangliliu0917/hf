package com.hf.core.dao.remote;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 报文发送类
 * 
 * @author LHL
 * 
 */
public class Sender {

	public static final Logger log = LoggerFactory.getLogger(Sender.class);

	private String cookies;//

	public static HttpURLConnection createUrl(String url) {
		URL httpurl = null;
		HttpURLConnection http = null;
		try {
			httpurl = new URL(url);
			http = (HttpURLConnection) httpurl.openConnection();
		} catch (Exception e) {
			log.error("open connection error!");
		}
		http.setConnectTimeout(20000);
		http.setReadTimeout(20000);
		return http;
	}

	public static String sendHttpReq(String url, JSONObject obj, String charset) {

		StringBuffer buffer = new StringBuffer();
		HttpURLConnection httpurl = null;
		String charEncoding = "UTF-8";
		if (!charset.equals("")) {
			charEncoding = charset;
		}

		try {
			httpurl = createUrl(url);
			httpurl.setDoOutput(true);
			httpurl.setRequestMethod("POST");
			httpurl.setRequestProperty("Content-Type", "application/json");
			httpurl.setRequestProperty("Accept-Charset", "utf-8");
			DataOutputStream out = new DataOutputStream(httpurl.getOutputStream());
			out.write(obj.toString().getBytes("utf-8"));
			out.flush();
			out.close();

			InputStream in = httpurl.getInputStream();
			int code = httpurl.getResponseCode();
			if (code == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, charEncoding));
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
			} else {
				log.error("no response");
			}
		} catch (Exception e) {
			log.error("operate failed! url is {}", url, e);
			return null;
		} finally {
			closeHttpRequest(httpurl);
		}
		return buffer.toString();
	}

	public static void closeHttpRequest(HttpURLConnection httpReq) {
		if (httpReq != null) {
			httpReq.disconnect();
		}
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	public static String sendHttpForm(String url, String param) {
		URL u = null;
		HttpURLConnection con = null;

		// 尝试发送请求
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			// // POST 只能为大写，严格限制，post会不识别
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			osw.write(param);
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}

		// 读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			// 一定要有返回值，否则无法把请求发送给server端。
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}
}
