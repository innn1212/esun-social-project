package com.esun.social_media.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.esun.social_media.dto.CommentRequest;
import com.esun.social_media.service.CommentService;

@RestController
@RequestMapping("/api/posts")
public class CommentController {
    
    @Autowired
    private CommentService commentService;

    // 捕捉網址路徑上的 {postId}
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> createComment(
            @PathVariable Integer postId,
            @RequestBody CommentRequest request) {
        try {
            // 從手環解析出使用者 ID
            Integer userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            commentService.createComment(userId, postId, request);
            return ResponseEntity.ok("留言成功！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("留言失敗：" + e.getMessage());
        }
    }
}
