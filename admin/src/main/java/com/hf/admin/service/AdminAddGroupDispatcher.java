package com.hf.admin.service;

import com.hf.base.client.DefaultClient;
import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.Dispatcher;
import com.hf.base.model.UserGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class AdminAddGroupDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient defaultClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);

        String groupId = request.getParameter("editedGroupId");
        if(StringUtils.isNotEmpty(groupId)) {
            UserGroup userGroup = defaultClient.getUserGroupById(Long.parseLong(groupId));
            dispatchResult.addObject("userGroup",userGroup);
            dispatchResult.addObject("subGroupId",String.valueOf(userGroup.getSubGroupId()));
        }

        List<Map<String,Object>> subGroups = defaultClient.getSubGroups(groupId);
        dispatchResult.addObject("subGroups",subGroups);
        return dispatchResult;
    }
}
