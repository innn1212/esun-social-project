package com.esun.social_media.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;  // 新增 Import

import com.esun.social_media.dto.PostRequest;
import com.esun.social_media.model.Post;
import com.esun.social_media.model.Comment;
import com.esun.social_media.repository.PostRepository;
import com.esun.social_media.repository.CommentRepository; // 新增 Import

import java.util.List;
import java.util.stream.Collectors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // 注入 CommentRepository
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public void createPost(Integer userId, PostRequest request) {
        // 呼叫 SP，將 userId 和前端傳來的內容寫入資料庫
        //postRepository.createPostUsingSP(userId, request.getContent(), request.getImage());

        // 使用 HtmlUtils 將前端傳來的內容進行轉義，防止 XSS 攻擊
        String safeContent = HtmlUtils.htmlEscape(request.getContent());

        // 將過濾後的 safeContent 傳給資料庫
        postRepository.createPostUsingSP(userId, safeContent, request.getImage());
    }

    /* 
    public List<Post> getAllPosts() {
        return postRepository.getAllPostsUsingSP();
    }
    */
    
    public List<Post> getAllPosts() {
        // 1. 撈出所有文章
        List<Post> posts = postRepository.getAllPostsUsingSP();
        // 2. 撈出所有留言
        List<Comment> allComments = commentRepository.getAllCommentsUsingSP();

        // 3. 把留言分發給對應的文章
        for (Post post : posts) {
            // 利用 Java Stream 將屬於這篇文章的留言過濾出來
            List<Comment> postComments = allComments.stream()
                .filter(c -> c.getPostId().equals(post.getPostId()))
                .collect(Collectors.toList());
            
            // 把過濾好的留言陣列，塞進我們剛剛建立的 @Transient 欄位裡
            post.setComments(postComments);
        }
        
        return posts;
    }

    // 實作刪除功能，加上 @Transactional 確保兩個 SP 同生共死
    @Transactional
    public void deletePost(Integer currentUserId, Integer postId) {
        
        // 1. 先從資料庫把這篇文章找出來 (findById 是 Spring Data JPA 內建功能)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("找不到該篇文章！"));

        // 2. 【資安防禦】檢查當前登入者是否為文章作者
        // 注意：Integer 物件比對要用 equals，不能用 ==
        if (!post.getUserId().equals(currentUserId)) {
            throw new RuntimeException("權限不足：你只能刪除自己的文章！");
        }

        // 3. 執行刪除 (若在刪除過程中發生資料庫異常，@Transactional 會自動把已刪除的資料救回來)
        commentRepository.deleteCommentsByPostIdUsingSP(postId); // 先刪除底下的留言
        postRepository.deletePostUsingSP(postId);                // 再刪除文章本身
    }

    /*
    @Transactional
    public void updatePost(Integer currentUserId, Integer postId, PostRequest request) {

        // 1. 找文章
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("找不到該篇文章！"));

        // 2. 檢查權限 (防禦越權攻擊)
        if (!post.getUserId().equals(currentUserId)) {
            throw new RuntimeException("權限不足：你只能編輯自己的文章！");
        }

        // 3. 防禦 XSS
        String safeContent = HtmlUtils.htmlEscape(request.getContent());

        // 4. 更新資料
        postRepository.updatePostUsingSP(postId, safeContent, request.getImage());
    }
     */

    /*
    @Transactional
    public void updatePost(Integer currentUserId, Integer postId, PostRequest request) {
        // 1. 拿出資料庫裡的舊文章
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("找不到該篇文章！"));

        // 2. 檢查權限 (防禦越權攻擊)
        if (!existingPost.getUserId().equals(currentUserId)) {
            throw new RuntimeException("權限不足：你只能編輯自己的文章！");
        }

        // 3. 【PATCH 合併邏輯】
        // 處理文字內容 (Content)：
        // 如果前端有傳值進來，我們就做 XSS 過濾並使用新值；如果沒傳，就保持原本的內容
        String finalContent = existingPost.getContent(); // 預設為舊內容
        if (request.getContent() != null) {
            finalContent = HtmlUtils.htmlEscape(request.getContent());
        }

        // 處理圖片 (Image)：
        // 如果前端有傳值進來就處理
        String finalImage = existingPost.getImage(); // 預設為舊圖片
        if (request.getImage() != null) {
            // 實務小技巧：如果前端傳來的是「空字串("")」，通常代表使用者想把圖片刪除
            // 所以如果是空字串我們就存 null，否則存入新圖片的網址
            finalImage = request.getImage().isEmpty() ? null : request.getImage();
        }

        // 4. 將合併後的最終資料，送進資料庫更新
        postRepository.updatePostUsingSP(postId, finalContent, finalImage);
    }
    */

    @Transactional
    public void updatePost(Integer currentUserId, Integer postId, PostRequest request) {
        // 拿出資料庫裡的舊文章
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("找不到該篇文章！"));

        // 檢查權限 (防禦越權攻擊)
        if (!existingPost.getUserId().equals(currentUserId)) {
            throw new RuntimeException("權限不足：你只能編輯自己的文章！");
        }

        // 處理文字內容 (Content)：
        // 如果前端有傳值進來，我們就做 XSS 過濾並使用新值；如果沒傳，就保持原本的內容
        String finalContent = existingPost.getContent(); // 預設為舊內容
        if (request.getContent() != null) {
            finalContent = HtmlUtils.htmlEscape(request.getContent());
        }

        // 處理圖片 (Image)：
        String finalImage = existingPost.getImage(); // 預設為舊圖片
        // request.getImage() != null 代表前端有針對圖片做動作 (換新圖 或 傳入""刪除)
        if (request.getImage() != null) {

            // 【新增邏輯】如果舊文章本來就有圖片，那張舊圖已經沒用了，先把它從硬碟刪掉！
            if (existingPost.getImage() != null) {
                deletePhysicalFile(existingPost.getImage());
            }
            // 決定新的圖片欄位要是什麼 (空字串轉 null，否則存新網址)
            finalImage = request.getImage().isEmpty() ? null : request.getImage();
        }

        // 將合併後的最終資料，送進資料庫更新
        postRepository.updatePostUsingSP(postId, finalContent, finalImage);
    }

    /**
     * 刪除實體圖片檔案的共用方法
    */
    private void deletePhysicalFile(String imageUrl) {
        // 如果原本就沒有圖片，直接結束
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        
        try {
            // 這裡的情境假設圖片存在伺服器本地端，例如專案根目錄的 uploads 資料夾
            // 實務上，imageUrl 可能是 "uploads/image-123.jpg"
            Path filePath = Paths.get(imageUrl); 
            
            // deleteIfExists 是很安全的寫法，如果檔案存在就刪除，不存在也不會報錯
            boolean isDeleted = Files.deleteIfExists(filePath);
            
            if (isDeleted) {
                System.out.println(" 成功刪除舊圖片實體檔案：" + imageUrl);
            }
        } catch (Exception e) {
            // 實務上這裡會寫入 Log (日誌) 系統，而不是單純 print
            System.err.println(" 刪除圖片實體檔案發生錯誤：" + e.getMessage());
        }
    }
}
