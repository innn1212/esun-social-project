package com.esun.social_media.dto;

public class PostRequest {
    private String content;
    private String image;

    // 請在這裡用 VS Code 自動產生這兩個變數的 Getter 與 Setter！
    
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

}
