package com.esun.social_media.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Comment_ID")
    private Integer commentId;

    @Column(name = "User_ID", nullable = false)
    private Integer userId;

    @Column(name = "Post_ID", nullable = false)
    private Integer postId;

    @Column(name = "Content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "Created_At", insertable = false, updatable = false)
    private Timestamp createdAt;

    // 請在這裡用 VS Code 自動產生 Getter 與 Setter！

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
