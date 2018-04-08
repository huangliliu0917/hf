package com.hf.core.biz;


import com.hf.core.model.po.Channel;
import com.hf.core.model.po.UserChannel;

import java.util.List;

public interface ChannelBiz {

    List<Channel> getChanneList();

    void saveUserChannel(UserChannel userChannel);
}
