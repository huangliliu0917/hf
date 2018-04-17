package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.model.RemoteParams;
import org.apache.commons.httpclient.Header;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ZfClient extends BaseClient implements PayClient {

    private String url = "http://test1.guangyinwangluo.com:9999/swPayInterface/CHPay";

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        for(int i=0;i<params.keySet().size();i++) {
            if(i==0) {
                url = url+"?"+params.keySet().toArray()[i]+"="+params.get(params.keySet().toArray()[i]);
            } else {
                url = url+"&"+params.keySet().toArray()[i]+"="+params.get(params.keySet().toArray()[i]);
            }
        }
        RemoteParams remoteParams = new RemoteParams(url).withParams(new HashMap<>());
        String result = super.get(remoteParams);
        return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
    }

    @Override
    public Map<String, Object> unifiedorder(List<Header> headers, Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> refundorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> reverseorder(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> orderinfo(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> refundorderinfo(Map<String, Object> params) {
        return null;
    }
}
