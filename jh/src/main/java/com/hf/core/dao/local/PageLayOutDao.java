package com.hf.core.dao.local;

import com.hf.core.model.po.PageLayOut;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PageLayOutDao {
    int deleteByPrimaryKey(Long id);

    int insert(PageLayOut record);

    int insertSelective(PageLayOut record);

    PageLayOut selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PageLayOut record);

    int updateByPrimaryKey(PageLayOut record);

    List<PageLayOut> selectBySuperId(@Param("superId") Long superId);
}