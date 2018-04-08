package com.hf.core.dao.local;

import com.hf.core.model.po.PayReverseOrder;
import org.apache.ibatis.annotations.Param;

public interface PayReverseOrderDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayReverseOrder record);

    int insertSelective(PayReverseOrder record);

    PayReverseOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayReverseOrder record);

    int updateByPrimaryKey(PayReverseOrder record);

    PayReverseOrder selectByTrdOrder(@Param("outTradeNo") String outTradeNo);

    int updateStatus(@Param("id") Long id, @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);
}