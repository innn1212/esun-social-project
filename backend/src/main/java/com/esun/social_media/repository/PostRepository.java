package com.esun.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.esun.social_media.model.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    
    @Modifying
    @Query(value = "CALL SP_CreatePost(:userId, :content, :image)", nativeQuery = true)
    void createPostUsingSP(
        @Param("userId") Integer userId, 
        @Param("content") String content, 
        @Param("image") String image
    );

    // 【新增這段】呼叫查詢的 SP
    // Spring Data JPA 支援直接將查詢結果映射為 List<Post>
    @Query(value = "CALL SP_GetAllPosts()", nativeQuery = true)
    List<Post> getAllPostsUsingSP();

    @Modifying
    @Query(value = "CALL SP_DeletePost(:postId)", nativeQuery = true)
    void deletePostUsingSP(@Param("postId") Integer postId);

    @Modifying
    @Query(value = "CALL SP_UpdatePost(:postId, :content, :image)", nativeQuery = true)
    void updatePostUsingSP(
        @Param("postId") Integer postId, 
        @Param("content") String content, 
        @Param("image") String image
    );
}
