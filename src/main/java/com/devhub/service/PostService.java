package com.devhub.service;
import com.devhub.dto.PostCreateDto;
import com.devhub.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Post createPost(PostCreateDto postCreateDto, String username);
    Page<Post> getAllPosts(Pageable pageable, String sortBy);
    Post getPostById(Long id);
}