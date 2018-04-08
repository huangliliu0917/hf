package com.hf.core.dao.local;

import com.hf.core.model.po.PayProof;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface PayProofDao {
    int insert(PayProof record);

    int insertSelective(PayProof record);

    PayProof selectByTrdNo(@Param("outTradeNo") String trdNo);

    int update(Map<String, Object> params);
}