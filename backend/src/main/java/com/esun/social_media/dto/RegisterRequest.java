package com.esun.social_media.dto;

public class RegisterRequest {
    private String phoneNumber;
    private String userName;
    private String email;
    private String password;

    // 請在這裡用 VS Code 自動產生這四個變數的 Getter 與 Setter！
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
}