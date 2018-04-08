package com.hf.user.model;

public class RegisterRequest {

    private String loginId;
    private String password;
    private String email;
    private String tel;
    private String inviteCode;

    public RegisterRequest(String loginId, String password, String email, String tel, String inviteCode) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.tel = tel;
        this.inviteCode = inviteCode;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
