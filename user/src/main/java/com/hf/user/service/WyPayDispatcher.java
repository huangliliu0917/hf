package com.hf.user.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.contants.Constants;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WyPayDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getSession().getAttribute("groupId").toString();
        List<UserChannel> channels = client.getUserChannelList(Long.parseLong(groupId));
        List<UserChannel> wePayChannels = channels.parallelStream().filter(userChannel -> StringUtils.equalsIgnoreCase(userChannel.getChannelCode(),"09")).collect(Collectors.toList());

        DispatchResult dispatchResult = new DispatchResult();

        if(CollectionUtils.isEmpty(wePayChannels)) {
            page = "channel_no_right";
            dispatchResult.addObject("pageName","网银支付");
            dispatchResult.addObject("channelName","网银支付");
            dispatchResult.setPage(page);
            return dispatchResult;
        }
        List<Map<String,String>> bankCodes = new ArrayList<>();

        for(Map<String,String> bankCodeMap: Constants.bankCodeList) {
            Map<String,String> codeMap = new HashMap<>();
            bankCodes.add(codeMap);
            for(String key:bankCodeMap.keySet()) {
                codeMap.put("code",key);
                codeMap.put("name",bankCodeMap.get(key));
            }
        }

        dispatchResult.addObject("bankCodes",bankCodes);
        dispatchResult.setPage(page);
        return dispatchResult;
    }
}
