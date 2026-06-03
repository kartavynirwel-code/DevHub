package com.devhub.service.impl;

import com.devhub.entity.Comment;
import com.devhub.entity.Post;
import com.devhub.entity.User;
import com.devhub.exception.ResourceNotFoundException;
import com.devhub.repository.CommentRepository;
import com.devhub.repository.PostRepository;
import com.devhub.repository.UserRepository;
import com.devhub.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, 
                             PostRepository postRepository, 
                             UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Comment addComment(Long postId, String content, Long parentId, String username) {
        log.info("User {} adding comment to post {}", username, postId);
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
        
        User author = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
        
        Comment parentComment = null;
        if (parentId != null) {
            parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        }
        
        Comment comment = Comment.builder()
            .content(content.trim())
            .author(author)
            .post(post)
            .parentComment(parentComment)
            .build();
        
        Comment saved = commentRepository.save(comment);
        log.info("Comment created with ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getPostComments(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return commentRepository.findByPost(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String username) {
        Comment comment = getCommentById(id);
        
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }
        
        commentRepository.delete(comment);
        log.info("Comment {} deleted by user {}", id, username);
    }
}
