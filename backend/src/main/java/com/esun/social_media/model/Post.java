package com.esun.social_media.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Post_ID")
    private Integer postId;

    @Column(name = "User_ID", nullable = false)
    private Integer userId;

    @Column(name = "Content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "Image")
    private String image;

    @Column(name = "Created_At", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 請在這裡用 VS Code 自動產生這五個變數的 Getter 與 Setter！

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    
    @Transient
    private List<Comment> comments;

    // 請記得產生 getComments() 與 setComments()！

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}


