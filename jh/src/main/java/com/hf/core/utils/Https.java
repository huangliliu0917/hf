package com.hf.core.utils;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Https {

    private Logger logger = LoggerFactory.getLogger(Https.class);

    public String sendAsPost(String url, NameValuePair[] data) {
        return sendAsPost(url,data, Collections.EMPTY_LIST);
    }

    public String sendAsPost(String url, NameValuePair[] data, List<Header> headers) {
        String response= "";
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);

        for(Header header:headers) {
            postMethod.addRequestHeader(header);
        }

        postMethod.setRequestBody(data);

        int statusCode = 0;
        try {
            statusCode = httpClient.executeMethod(postMethod);
        } catch (HttpException e) {
            logger.warn(e.getMessage());
            return response;
        } catch (IOException e) {
            logger.warn(e.getMessage());
            return response;
        }

        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
            // 从头中取出转向的地址
            Header locationHeader = postMethod.getResponseHeader("location");
            String location = null;
            if (locationHeader != null) {
                location = locationHeader.getValue();
                logger.warn("The page was redirected to:" + location);
                response = sendAsPost(location,data,headers);//用跳转后的页面重新请求。
            } else {
                logger.warn("Location field value is null.");
            }
        } else {
            logger.info("remove invoke status : "+postMethod.getStatusLine());
            try {
                response= postMethod.getResponseBodyAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            postMethod.releaseConnection();
        }
        return response;
    }
}
