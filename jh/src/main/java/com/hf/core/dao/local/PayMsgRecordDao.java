package com.hf.core.dao.local;

import com.hf.core.model.po.PayMsgRecord;
import org.apache.ibatis.annotations.Param;

public interface PayMsgRecordDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayMsgRecord record);

    int insertSelective(PayMsgRecord record);

    PayMsgRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayMsgRecord record);

    int updateByPrimaryKeyWithBLOBs(PayMsgRecord record);

    int updateByPrimaryKey(PayMsgRecord record);

    PayMsgRecord selectByTradeNo(@Param("outTradeNo") String outTradeNo, @Param("operateType") Integer operateType, @Param("tradeType") Integer tradeType);
}