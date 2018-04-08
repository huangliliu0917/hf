package com.hf.core.biz.adapter;

import com.hf.core.model.dto.trade.IEntity;

import java.util.Map;

public interface Adapter<M extends IEntity> {
    M adpat(Map<String, Object> request);
}
