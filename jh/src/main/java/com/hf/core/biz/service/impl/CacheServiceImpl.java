package com.hf.core.biz.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hf.base.exceptions.BizFailException;
import com.hf.core.biz.service.CacheService;
import com.hf.core.dao.local.AccountDao;
import com.hf.core.dao.local.HfPropertiesDao;
import com.hf.core.dao.local.UserGroupDao;
import com.hf.core.dao.local.UserInfoDao;
import com.hf.core.model.po.Account;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private HfPropertiesDao hfPropertiesDao;

    private Map<String,String> cacheMap = new ConcurrentHashMap<>();

    private LoadingCache<Long,UserInfo> userCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<Long, UserInfo>() {
                @Override
                public UserInfo load(Long userId) throws Exception {
                    return userInfoDao.selectByPrimaryKey(userId);
                }
            });

    private LoadingCache<Long,UserGroup> groupCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10,TimeUnit.MINUTES)
            .maximumSize(1000)
            .refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<Long, UserGroup>() {
                @Override
                public UserGroup load(Long groupId) throws Exception {
                    return userGroupDao.selectByPrimaryKey(groupId);
                }
            });

    private LoadingCache<String,UserGroup> groupNoCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10,TimeUnit.MINUTES)
            .maximumSize(1000)
            .refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<String, UserGroup>() {
                @Override
                public UserGroup load(String groupNo) throws Exception {
                    return userGroupDao.selectByGroupNo(groupNo);
                }
            });

    private LoadingCache<String,String> propCache = CacheBuilder.newBuilder().expireAfterAccess(10,TimeUnit.MINUTES)
            .maximumSize(1000).refreshAfterWrite(10,TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return hfPropertiesDao.selectByKey(s);
                }
            });



    @Override
    public UserInfo getUserInfo(Long userId) {
        try {
            return userCache.get(userId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Account getAccount(Long userId) {
        try {
//            return accountCache.get(userId);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public UserGroup getGroup(Long groupId) {
        try {
            return groupCache.get(groupId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserGroup getGroup(String groupNo) {
        try {
            return groupNoCache.get(groupNo);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getProp(String key,String defaultValue) {
        try {
            return propCache.get(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String getPublicKey() {
        String key = cacheMap.get("public_key");
        if(!StringUtils.isEmpty(key)) {
            return key;
        }
        try {
            String file = "/META-INF/key/rsa_public_key.pem";
            InputStream inputStream = this.getClass().getResourceAsStream(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String s = br.readLine();
            StringBuffer publickey = new StringBuffer();
            s = br.readLine();
            while (s.charAt(0) != '-') {
                publickey = publickey.append(s).append("/");
                s = br.readLine();
            }

            cacheMap.put("public_key",publickey.toString());
            return publickey.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizFailException(e.getMessage());
        }
    }

    @Override
    public String getPrivateKey() {
        String key = cacheMap.get("private_key");
        if(!StringUtils.isEmpty(key)) {
            return key;
        }
        try {
            String file = "/META-INF/key/private_key.pem";
            InputStream inputStream = this.getClass().getResourceAsStream(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String s = br.readLine();
            StringBuffer privateKey = new StringBuffer();
            s = br.readLine();
            while (s.charAt(0) != '-') {
                privateKey = privateKey.append(s).append("/");
                s = br.readLine();
            }

            cacheMap.put("private_key",privateKey.toString());
            return privateKey.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizFailException(e.getMessage());
        }
    }
}
