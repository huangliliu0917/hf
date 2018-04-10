package com.hf.core.biz.service;

import com.hf.core.model.po.Account;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserInfo;

public interface CacheService {

    UserInfo getUserInfo(Long userId);

    Account getAccount(Long userId);

    UserGroup getGroup(Long groupId);

    UserGroup getGroup(String groupNo);

    String getProp(String key, String defaultValue);

    String getPublicKey();

    String getPrivateKey();

    String getRootPath();
}
