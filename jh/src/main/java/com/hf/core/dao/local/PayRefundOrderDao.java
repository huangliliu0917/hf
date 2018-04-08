package com.hf.core.dao.local;

import com.hf.core.model.po.PayRefundOrder;
import org.apache.ibatis.annotations.Param;

public interface PayRefundOrderDao {
    int deleteByPrimaryKey(Long id);

    int insert(PayRefundOrder record);

    int insertSelective(PayRefundOrder record);

    PayRefundOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayRefundOrder record);

    int updateByPrimaryKey(PayRefundOrder record);

    PayRefundOrder selectByRefundNo(@Param("refundNo") String refundNo, @Param("refundType") Integer refundType);

    int updateStatus(@Param("id") Long id, @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);

    PayRefundOrder selectByNo(@Param("no") String no);
}