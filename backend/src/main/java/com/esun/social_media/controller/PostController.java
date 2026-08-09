package com.esun.social_media.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.esun.social_media.dto.PostRequest;
import com.esun.social_media.model.Post;
import com.esun.social_media.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostRequest request) {
        try {
            // 從 Spring Security 的 Context 中，取得我們在 JWT Filter 塞進去的 userId
            Integer userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 呼叫 Service 發文
            postService.createPost(userId, request);

            return ResponseEntity.ok("發文成功！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("發文失敗：" + e.getMessage());
        }
    }

    // @GetMapping 沒有指定路徑，代表它對應的也是 /api/posts，只是 Method 變成了 GET
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        // 直接回傳狀態碼 200 與文章列表 (Spring Boot 會自動幫我們把 List 轉成 JSON 陣列)
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Integer postId) {
        try {
            // 從 JWT 手環拿出身分
            Integer userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 呼叫 Service 執行刪除
            postService.deletePost(userId, postId);

            return ResponseEntity.ok("文章刪除成功！");
        } catch (Exception e) {
            // 如果是權限不足或找不到文章，就會跑到這裡
            return ResponseEntity.badRequest().body("刪除失敗：" + e.getMessage());
        }
    }

    /*
    @PutMapping("/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable Integer postId, 
            @RequestBody PostRequest request) {
        try {
            Integer userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            postService.updatePost(userId, postId, request);
            return ResponseEntity.ok("文章更新成功！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新失敗：" + e.getMessage());
        }
    }
    */

    // 這裡從 @PutMapping 改成了 @PatchMapping
    @PatchMapping("/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable Integer postId, 
            @RequestBody PostRequest request) {
        try {
            Integer userId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            postService.updatePost(userId, postId, request);
            return ResponseEntity.ok("文章更新成功！");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新失敗：" + e.getMessage());
        }
    }

}
