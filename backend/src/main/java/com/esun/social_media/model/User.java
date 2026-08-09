package com.esun.social_media.model;

import jakarta.persistence.*;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_ID")
    private Integer userId;

    @Column(name = "Phone_Number", unique = true, nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "User_Name", nullable = false, length = 50)
    private String userName;

    @Column(name = "Email", nullable = false, length = 100)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Cover_Image")
    private String coverImage;

    @Column(name = "Biography", columnDefinition = "TEXT")
    private String biography;

    // 預設建構子 (JPA 規定必須要有)
    public User() {}

    // 為了節省版面，我先示範前兩個變數的 Getter/Setter
    // 請看下方說明，在 VS Code 裡面自動產生其餘的！
    
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // ... (請補齊 userName, email, password, coverImage, biography 的 Getter 和 Setter)

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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
    
}
