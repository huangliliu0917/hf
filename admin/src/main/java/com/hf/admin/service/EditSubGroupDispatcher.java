package com.hf.admin.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class EditSubGroupDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getParameter("editedGroupId");
        UserGroup userGroup = client.getUserGroupById(Long.parseLong(groupId));
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("userGroup",userGroup);

        List<Map<String,Object>> subGroups = client.getSubGroups(groupId);
        dispatchResult.addObject("subGroups",subGroups);
        dispatchResult.addObject("subGroupId",String.valueOf(userGroup.getSubGroupId()));

        return dispatchResult;
    }
}
