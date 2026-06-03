package com.devhub.service.impl;

import com.devhub.entity.Post;
import com.devhub.entity.User;
import com.devhub.entity.Vote;
import com.devhub.exception.ResourceNotFoundException;
import com.devhub.repository.PostRepository;
import com.devhub.repository.UserRepository;
import com.devhub.repository.VoteRepository;
import com.devhub.service.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public VoteServiceImpl(VoteRepository voteRepository, 
                         PostRepository postRepository, 
                         UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Vote castVote(Long postId, String username, Vote.VoteType voteType) {
        log.info("User {} casting {} vote on post {}", username, voteType, postId);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
        
        // Check if user already voted
        var existingVote = voteRepository.findByUserAndPost(user, post);
        
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            // Same vote type - remove it (toggle)
            if (vote.getVoteType() == voteType) {
                voteRepository.delete(vote);
                updatePostScore(post);
                return null;
            }
            // Different vote type - update it
            vote.setVoteType(voteType);
            updatePostScore(post);
            return voteRepository.save(vote);
        }
        
        // New vote
        Vote vote = Vote.builder()
            .user(user)
            .post(post)
            .voteType(voteType)
            .build();
        
        updatePostScore(post);
        return voteRepository.save(vote);
    }

    @Override
    @Transactional
    public void removeVote(Long postId, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        voteRepository.findByUserAndPost(user, post)
            .ifPresent(vote -> {
                voteRepository.delete(vote);
                updatePostScore(post);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getPostScore(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        Long upvotes = voteRepository.countByPostAndVoteType(post, Vote.VoteType.UPVOTE);
        Long downvotes = voteRepository.countByPostAndVoteType(post, Vote.VoteType.DOWNVOTE);
        
        return upvotes.intValue() - downvotes.intValue();
    }

    private void updatePostScore(Post post) {
        Long upvotes = voteRepository.countByPostAndVoteType(post, Vote.VoteType.UPVOTE);
        Long downvotes = voteRepository.countByPostAndVoteType(post, Vote.VoteType.DOWNVOTE);
        
        post.setUpvotes(upvotes.intValue());
        post.setDownvotes(downvotes.intValue());
        post.setScore(upvotes.intValue() - downvotes.intValue());
        
        postRepository.save(post);
    }
}
