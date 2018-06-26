package com.hf.core.dao.remote;

import com.google.gson.Gson;
import com.hf.base.client.BaseClient;
import com.hf.base.model.RemoteParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class CallBackClient extends BaseClient {
    protected Logger logger = LoggerFactory.getLogger(CallBackClient.class);

    public boolean post(String url,Map<String,Object> params) {
        for(int i=0;i<params.keySet().size();i++) {
            if(i==0) {
                url = url+"?"+params.keySet().toArray()[i]+"="+params.get(params.keySet().toArray()[i]);
            } else {
                url = url+"&"+params.keySet().toArray()[i]+"="+params.get(params.keySet().toArray()[i]);
            }
        }
        RemoteParams remoteParams = new RemoteParams(url).withParams(new HashMap<>());
        try {
            String result = super.post(remoteParams, MediaType.APPLICATION_FORM_URLENCODED);
            logger.info(String.format("remote call back result:%s,%s",result,new Gson().toJson(params)));
            return StringUtils.equalsIgnoreCase(result,"SUCCESS");
        } catch (Exception e) {
            return false;
        }
    }
}
