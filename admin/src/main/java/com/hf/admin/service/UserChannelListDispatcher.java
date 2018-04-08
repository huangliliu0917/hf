package com.hf.admin.service;

import com.hf.admin.rpc.AdminClient;
import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserChannelPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserChannelListDispatcher implements Dispatcher {
    @Autowired
    private AdminClient adminClient;
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getParameter("id");

        List<UserChannelPage> list = client.getUserChannelInfo(groupId);
//        List<UserChannel> list = adminClient.getUserChannelList(groupId);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
//        dispatchResult.addObject("channels",list);
        dispatchResult.addObject("providers",list);
        dispatchResult.addObject("group",groupId);
        return dispatchResult;
    }
}
