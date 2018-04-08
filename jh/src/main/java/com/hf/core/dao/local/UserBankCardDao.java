package com.hf.core.dao.local;

import com.hf.core.model.po.UserBankCard;

import java.util.List;

public interface UserBankCardDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserBankCard record);

    int insertSelective(UserBankCard record);

    UserBankCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBankCard record);

    int updateByPrimaryKey(UserBankCard record);

    List<UserBankCard> selectByUser(Long groupId);
}