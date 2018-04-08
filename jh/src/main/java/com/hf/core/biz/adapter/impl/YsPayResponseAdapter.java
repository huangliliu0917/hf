package com.hf.core.biz.adapter.impl;

import com.hf.core.biz.adapter.Adapter;
import com.hf.core.model.dto.trade.unifiedorder.HfPayResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class YsPayResponseAdapter implements Adapter<HfPayResponse> {

    @Override
    public HfPayResponse adpat(Map<String, Object> request) {
        return null;
    }
}
