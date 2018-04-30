package com.hf.core.dao.remote;

import org.apache.commons.httpclient.Header;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface PayClient {
    Map<String,Object> unifiedorder(Map<String, Object> params);
    Map<String,Object> unifiedorder(List<Header> headers, Map<String, Object> params);
    Map<String,Object> refundorder(Map<String, Object> params);
    Map<String,Object> reverseorder(Map<String, Object> params);
    Map<String,Object> orderinfo(Map<String, Object> params);
    Map<String,Object> refundorderinfo(Map<String, Object> params);
}
