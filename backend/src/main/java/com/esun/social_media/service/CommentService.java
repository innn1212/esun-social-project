package com.esun.social_media.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;  // 新增 Import

import com.esun.social_media.dto.CommentRequest;
import com.esun.social_media.repository.CommentRepository;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public void createComment(Integer userId, Integer postId, CommentRequest request) {
        //commentRepository.createCommentUsingSP(userId, postId, request.getContent());

        // 使用 HtmlUtils 將前端傳來的內容進行轉義，防止 XSS 攻擊
        String safeContent = HtmlUtils.htmlEscape(request.getContent());

        // 將過濾後的 safeContent 傳給資料庫
        commentRepository.createCommentUsingSP(userId, postId, safeContent);
    }
}
