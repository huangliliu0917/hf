package com.hf.base.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DispatchResult {
    private String page;
    private Map<String,Object> data;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public DispatchResult addObject(String key,Object obj) {
        if(Objects.isNull(data)) {
            data = new HashMap<>();
        }
        data.put(key,obj);
        return this;
    }
}
