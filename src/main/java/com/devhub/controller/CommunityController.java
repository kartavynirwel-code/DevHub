package com.devhub.controller;

import com.devhub.dto.CommunityCreateDto;
import com.devhub.entity.Community;
import com.devhub.repository.CommunityRepository;
import com.devhub.repository.UserRepository;
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
@RequestMapping("/communities")
public class CommunityController {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public CommunityController(CommunityRepository communityRepository, UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    /**
     * Display community creation form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("communityCreateDto", new CommunityCreateDto());
        return "create-community";
    }

    /**
     * Handle community creation
     */
    @PostMapping("/create")
    public String createCommunity(@Valid @ModelAttribute("communityCreateDto") CommunityCreateDto dto,
                                   BindingResult result,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        
        log.info("Community creation request from user: {}", userDetails.getUsername());
        
        if (result.hasErrors()) {
            log.warn("Community creation validation failed for user: {}. Errors: {}", 
                userDetails.getUsername(), result.getAllErrors());
            return "create-community";
        }
        
        try {
            // Check if community name already exists
            if (communityRepository.existsByName(dto.getName())) {
                log.warn("Community name already exists: {}", dto.getName());
                model.addAttribute("error", "Community name '" + dto.getName() + "' already exists!");
                return "create-community";
            }
            
            // Get current user
            var creator = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Create community
            Community community = Community.builder()
                .name(dto.getName().trim())
                .description(dto.getDescription().trim())
                .createdBy(creator)
                .build();
            
            Community savedCommunity = communityRepository.save(community);
            log.info("Community created successfully with ID: {} by user: {}", 
                savedCommunity.getId(), userDetails.getUsername());
            
            return "redirect:/";
        } catch (Exception e) {
            log.error("Error creating community for user {}: {}", userDetails.getUsername(), e.getMessage(), e);
            model.addAttribute("error", "Failed to create community: " + e.getMessage());
            return "create-community";
        }
    }

    /**
     * View community if needed (optional endpoint for future use)
     */
    @GetMapping("/{id}")
    public String viewCommunity(@PathVariable Long id, Model model) {
        Community community = communityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Community not found"));
        model.addAttribute("community", community);
        return "community-details";
    }
}
