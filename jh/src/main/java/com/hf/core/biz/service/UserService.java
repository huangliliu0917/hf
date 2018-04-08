package com.hf.core.biz.service;

import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserInfo;

import java.util.List;

public interface UserService {
    void register(UserGroup userGroup, UserInfo userInfo);
    List<UserGroup> getChildMchIds(Long groupId);
    List<UserGroup> getChildCompany(Long groupId);
}
