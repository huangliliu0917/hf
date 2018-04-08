package com.hf.base.interceptors;

import com.hf.base.biz.CacheService;
import com.hf.base.contants.Constants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

public abstract class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private CacheService cacheService;

    public abstract String getSystem();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String loginId = request.getParameter("loginId");
        if(StringUtils.isEmpty(loginId) && request.getRequestURI().contains("login")) {
            response.sendRedirect(String.format("/%s/login.jsp",getSystem()));
            return false;
        }

        String sessionId = request.getSession().getId();
        if(Objects.isNull(request.getSession().getAttribute(Constants.USER_LOGIN_INFO))) {
            response.sendRedirect(String.format("/%s/login.jsp",getSystem()));
            return false;
        }
        Map<String,Object> map = (Map<String,Object>)request.getSession().getAttribute(Constants.USER_LOGIN_INFO);

        if(MapUtils.isEmpty(map)) {
            response.sendRedirect(String.format("/%s/login.jsp",getSystem()));
            return false;
        }

        if(request.getRequestURI().contains("login") && !StringUtils.equals(loginId,String.valueOf(map.get("loginId")))) {
            response.sendRedirect(String.format("/%s/login.jsp",getSystem()));
            return false;
        }

        String userId = ((Map<String,Object>)request.getSession().getAttribute(Constants.USER_LOGIN_INFO)).get("id").toString();
        if(!cacheService.checkLogin(userId,sessionId)) {
            response.sendRedirect(String.format("/%s/login.jsp",getSystem()));
            return false;
        }

        request.setAttribute(Constants.CURRENT_USER_ID,userId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
