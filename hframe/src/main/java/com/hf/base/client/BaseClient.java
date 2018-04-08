package com.hf.base.client;

import com.hf.base.model.RemoteParams;
import com.hf.base.utils.Utils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    private static final String APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded";
    private RestTemplate restTemplate = new RestTemplate();

    protected <T> T get(RemoteParams params, Class<T> dataType) {
        return restTemplate.getForObject(getUrl(params),dataType,null == params.getParams()?params.getParamObj():params.getParams());
    }

    protected String get(RemoteParams params) {
        return restTemplate.getForObject(getUrl(params),String.class,null == params.getParams()?params.getParamObj():params.getParams());
    }

    protected String post(RemoteParams params) {
        return restTemplate.postForObject(getUrl(params),null == params.getParams()?params.getParamObj():params.getParams(),String.class);
    }

    protected String post(RemoteParams params, MediaType mediaType) {
        LinkedMultiValueMap map=new LinkedMultiValueMap();
        for(String key:params.getParams().keySet()) {
            map.add(key,params.getParams().get(key));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        HttpEntity<LinkedMultiValueMap> httpEntity = new HttpEntity<>(map,headers);
        ResponseEntity<String> res = restTemplate.postForEntity(getUrl(params),httpEntity,String.class);
        return res.getBody();
    }

    private String getUrl(RemoteParams params) {
        String url = params.getUrl();
        if(!Utils.isEmpty(params.getPath())) {
            url = params.getUrl()+params.getPath();
        }
        return url;
    }
}
