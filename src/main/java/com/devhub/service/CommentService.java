package com.devhub.service;

import com.devhub.entity.Comment;
import com.devhub.entity.Post;
import java.util.List;

public interface CommentService {
    Comment addComment(Long postId, String content, Long parentId, String username);
    List<Comment> getPostComments(Long postId);
    Comment getCommentById(Long id);
    void deleteComment(Long id, String username);
}
