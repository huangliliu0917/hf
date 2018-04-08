package com.hf.base.model;

import java.util.List;

public class UserChannelPage {
    private UserGroupExt userGroupExt;
    private List<UserChannel> userChannels;

    public UserGroupExt getUserGroupExt() {
        return userGroupExt;
    }

    public void setUserGroupExt(UserGroupExt userGroupExt) {
        this.userGroupExt = userGroupExt;
    }

    public List<UserChannel> getUserChannels() {
        return userChannels;
    }

    public void setUserChannels(List<UserChannel> userChannels) {
        this.userChannels = userChannels;
    }
}
