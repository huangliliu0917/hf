package com.hf.base.biz;

public interface CacheService {
    boolean checkLogin(String userId, String sessionId);
    boolean login(String userId, String sessionId);
}
