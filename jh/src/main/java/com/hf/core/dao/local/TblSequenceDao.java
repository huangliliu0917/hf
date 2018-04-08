package com.hf.core.dao.local;

import com.hf.core.model.po.TblSequence;
import org.apache.ibatis.annotations.Param;

public interface TblSequenceDao {
    int deleteByPrimaryKey(String seqName);

    int insert(TblSequence record);

    int insertSelective(TblSequence record);

    TblSequence selectByPrimaryKey(String seqName);

    int updateByPrimaryKeySelective(TblSequence record);

    int updateByPrimaryKey(TblSequence record);

    int updateCurrentVal(@Param("seqName") String seqName, @Param("currentVal") Integer currentVal, @Param("targetVal") Integer targetVal);
}