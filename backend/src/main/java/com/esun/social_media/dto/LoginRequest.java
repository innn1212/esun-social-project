package com.esun.social_media.dto;

public class LoginRequest {
    private String phoneNumber;
    private String password;

    // 請在這裡用 VS Code 自動產生這兩個變數的 Getter 與 Setter！
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
