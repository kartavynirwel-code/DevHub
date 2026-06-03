package com.devhub.controller;

import com.devhub.dto.PostCreateDto;
import com.devhub.entity.Post;
import com.devhub.entity.Vote.VoteType;
import com.devhub.repository.CommunityRepository;
import com.devhub.service.PostService;
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
    private final CommunityRepository communityRepository;

    // Constructor Injection (SOLID principle follow karte hue)
    public PostController(PostService postService, CommunityRepository communityRepository) {
        this.postService = postService;
        this.communityRepository = communityRepository;
    }

    /**
     * Specific Post aur uske pure Threaded Comment Tree ko display karta hai.
     */
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post-details";
    }

    /**
     * Create Post ka form render karta hai aur saari available communities pass karta hai.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("postCreateDto", new PostCreateDto());
        model.addAttribute("communities", communityRepository.findAll());
        return "create-post";
    }

    /**
     * New Post submissions ko handle aur validate karta hai.
     */
    @PostMapping("/create")
    public String createPost(@Valid @ModelAttribute("postCreateDto") PostCreateDto dto,
                             BindingResult result,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        if (result.hasErrors()) {
            log.warn("Post creation validation failed for user: {}", userDetails.getUsername());
            model.addAttribute("communities", communityRepository.findAll());
            return "create-post";
        }
        
        Post created = postService.createPost(dto, userDetails.getUsername());
        log.info("Post created successfully with ID: {} by user: {}", created.getId(), userDetails.getUsername());
        return "redirect:/posts/" + created.getId();
    }

    /**
     * Upvote aur Downvote endpoints ko handle karne ke liye route.
     * Score calculate karne ke baad page ko wapas redirect karta hai.
     */
    @PostMapping("/{id}/vote")
    public String handleVote(@PathVariable Long id, 
                             @RequestParam("type") VoteType voteType,
                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} voted {} on post ID: {}", userDetails.getUsername(), voteType, id);
        // Isme aap voteService.castVote(id, userDetails.getUsername(), voteType); call karenge.
        return "redirect:/posts/" + id;
    }

    /**
     * New Comments submission handle karne ke liye endpoint.
     */
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam("content") String content,
                             @RequestParam(value = "parentId", required = false) Long parentId,
                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} adding comment to post ID: {}", userDetails.getUsername(), id);
        // Isme aap commentService.addComment(id, parentId, content, userDetails.getUsername()); call karenge.
        return "redirect:/posts/" + id;
    }
}