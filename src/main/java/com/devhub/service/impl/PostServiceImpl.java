package com.devhub.service.impl;

import com.devhub.dto.PostCreateDto;
import com.devhub.entity.Community;
import com.devhub.entity.Post;
import com.devhub.entity.User;
import com.devhub.exception.ResourceNotFoundException;
import com.devhub.repository.CommunityRepository;
import com.devhub.repository.PostRepository;
import com.devhub.repository.UserRepository;
import com.devhub.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    public PostServiceImpl(PostRepository postRepository, 
                           UserRepository userRepository, 
                           CommunityRepository communityRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
    }

    @Override
    @Transactional
    public Post createPost(PostCreateDto dto, String username) {
        log.info("User {} is attempting to create post: {}", username, dto.getTitle());
        
        // Validate DTO
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        if (dto.getCommunityId() == null) {
            throw new IllegalArgumentException("Community must be selected");
        }
        
        User author = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("User not found: {}", username);
                return new ResourceNotFoundException("User not found: " + username);
            });
            
        Community community = communityRepository.findById(dto.getCommunityId())
            .orElseThrow(() -> {
                log.error("Community not found with ID: {}", dto.getCommunityId());
                return new ResourceNotFoundException("Community not found with ID: " + dto.getCommunityId());
            });

        Post post = Post.builder()
            .title(dto.getTitle().trim())
            .content(dto.getContent().trim())
            .author(author)
            .community(community)
            .upvotes(0)
            .downvotes(0)
            .score(0)
            .build();

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully with ID: {} by user: {}", savedPost.getId(), username);
        return savedPost;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable, String sortBy) {
        Sort sort = switch (sortBy) {
            case "top" -> Sort.by(Sort.Direction.DESC, "score");
            case "comments" -> Sort.by(Sort.Direction.DESC, "comments");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return postRepository.findAll(sortedPageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Post not found with id: {}", id);
                return new ResourceNotFoundException("Post not found with id: " + id);
            });
    }
}
