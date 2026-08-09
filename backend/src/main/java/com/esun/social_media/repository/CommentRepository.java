package com.esun.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.esun.social_media.model.Comment;

import java.util.List; // 記得引入 List

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Modifying
    @Query(value = "CALL SP_CreateComment(:userId, :postId, :content)", nativeQuery = true)
    void createCommentUsingSP(
        @Param("userId") Integer userId,
        @Param("postId") Integer postId,
        @Param("content") String content
    );

    @Modifying
    @Query(value = "CALL SP_DeleteCommentsByPost(:postId)", nativeQuery = true)
    void deleteCommentsByPostIdUsingSP(@Param("postId") Integer postId);

    @Query(value = "CALL SP_GetAllComments()", nativeQuery = true)
    List<Comment> getAllCommentsUsingSP();
}
