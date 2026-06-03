package com.devhub.controller;

import com.devhub.dto.PostCreateDto;
import com.devhub.entity.Post;
import com.devhub.entity.Vote;
import com.devhub.repository.CommunityRepository;
import com.devhub.service.CommentService;
import com.devhub.service.PostService;
import com.devhub.service.VoteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final VoteService voteService;
    private final CommunityRepository communityRepository;

    public PostController(PostService postService, 
                         CommentService commentService,
                         VoteService voteService,
                         CommunityRepository communityRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.voteService = voteService;
        this.communityRepository = communityRepository;
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post-details";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("postCreateDto", new PostCreateDto());
        model.addAttribute("communities", communityRepository.findAll());
        return "create-post";
    }

    @PostMapping("/create")
    public String createPost(@Valid @ModelAttribute("postCreateDto") PostCreateDto dto,
                             BindingResult result,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        
        log.info("Post creation request from user: {}", userDetails.getUsername());
        
        model.addAttribute("communities", communityRepository.findAll());
        
        if (result.hasErrors()) {
            log.warn("Post creation validation failed for user: {}. Errors: {}", 
                userDetails.getUsername(), result.getAllErrors());
            return "create-post";
        }
        
        try {
            Post created = postService.createPost(dto, userDetails.getUsername());
            log.info("Post created successfully with ID: {} by user: {}", created.getId(), userDetails.getUsername());
            return "redirect:/posts/" + created.getId();
        } catch (Exception e) {
            log.error("Error creating post for user {}: {}", userDetails.getUsername(), e.getMessage(), e);
            model.addAttribute("error", "Failed to create post: " + e.getMessage());
            return "create-post";
        }
    }

    @PostMapping("/{id}/vote")
    public String handleVote(@PathVariable Long id, 
                             @RequestParam("type") Vote.VoteType voteType,
                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} voted {} on post ID: {}", userDetails.getUsername(), voteType, id);
        try {
            voteService.castVote(id, userDetails.getUsername(), voteType);
        } catch (Exception e) {
            log.error("Error casting vote: {}", e.getMessage());
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam("content") String content,
                             @RequestParam(value = "parentId", required = false) Long parentId,
                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} adding comment to post ID: {}", userDetails.getUsername(), id);
        try {
            commentService.addComment(id, content, parentId, userDetails.getUsername());
        } catch (Exception e) {
            log.error("Error adding comment: {}", e.getMessage());
        }
        return "redirect:/posts/" + id;
    }
}
