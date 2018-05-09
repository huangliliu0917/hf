package com.hf.admin.api;

import com.hf.base.client.AdminClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.DispatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private AdminClient adminClient;

    @Autowired
    private DispatcherFactory dispatcherFactory;

    @RequestMapping(value="/{page}")
    public ModelAndView dispatch(HttpServletRequest request, @PathVariable("page")String page) {
        ModelAndView modelAndView = new ModelAndView();
        DispatchResult dispatchResult = dispatcherFactory.getDispatcher(request,page).dispatch(request,page);
        modelAndView.setViewName(String.format("/%s",dispatchResult.getPage()));

        if(!org.apache.commons.collections.MapUtils.isEmpty(dispatchResult.getData())) {
            for(String key: dispatchResult.getData().keySet()) {
                modelAndView.addObject(key,dispatchResult.getData().get(key));
            }
        }

        return modelAndView;
    }

//    @RequestMapping(value="/{page}")
//    public ModelAndView dispatch(HttpServletRequest request, @PathVariable("page")String page) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName(String.format("/%s",page));
//
//        if(StringUtils.equals(page,"index")) {
//            Map<String,Object> map = (Map<String,Object>)request.getSession().getAttribute(Constants.USER_LOGIN_INFO);
//            modelAndView.addObject("name",map.get("name"));
//        }
//        if(StringUtils.equals(page,"admin_user_index")) {
//            String groupId = String.valueOf(request.getSession().getAttribute("groupId"));
//            UserGroupRequest userGroupRequest = new UserGroupRequest();
//            userGroupRequest.setCompanyId(groupId);
//            List<UserGroupDto> list = adminClient.getUserGroupList(userGroupRequest);
//
//            modelAndView = new ModelAndView("admin_user_index");
//            modelAndView.addObject("users",list);
//            return modelAndView;
//        }
//
//        if(StringUtils.equals(page,"admin_user_index2")) {
//            String groupId = String.valueOf(request.getSession().getAttribute("groupId"));
//            UserInfoRequest userInfoRequest = new UserInfoRequest();
//            userInfoRequest.setAdminId(groupId);
//            List<UserInfoDto> list = adminClient.getUserListForAdmin(userInfoRequest);
//            modelAndView.addObject("users",list);
//        }
//
//        if(StringUtils.equals(page,"admin_channel_index")) {
//            List<Channel> list = adminClient.getChannelList();
//
//            list.stream().forEach(channel -> {
//                    channel.setChannelTypeDesc(Channel.ChannelType.parse(channel.getChannelType()).getDesc());
//                    channel.setStatusDesc(Channel.ChannelStatus.parse(channel.getStatus()).getDesc());
//                }
//            );
//
//            modelAndView.addObject("channels",list);
//        }
//
//        return modelAndView;
//    }
}
