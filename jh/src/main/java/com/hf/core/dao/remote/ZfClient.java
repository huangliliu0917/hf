package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hf.base.client.BaseClient;
import com.hf.base.model.RemoteParams;
import com.hf.core.api.JhTest;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ZfClient extends BaseClient implements PayClient {

    protected static final Logger logger = LoggerFactory.getLogger(ZfClient.class);

    @Override
    public Map<String, Object> unifiedorder(Map<String, Object> params) {
        String url = "http://paygw.guangyinwangluo.com/swPayInterface/aliH5";
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

    public Map<String, Object> payForWx(Map<String, Object> params) {
        String url = "http://paygw.guangyinwangluo.com/swPayInterface/weChatQR";
        try {
            RemoteParams remoteParams = new RemoteParams(url).withParams(params);
            String result = super.post(remoteParams);
            logger.info("zf wx pay result:"+result);
            return new Gson().fromJson(result,new TypeToken<Map<String,Object>>(){}.getType());
        } catch (Exception e) {
            logger.error(new Gson().toJson(params)+","+e.getMessage());
            return new HashMap<>();
        }

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
