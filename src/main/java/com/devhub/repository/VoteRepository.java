package com.devhub.repository;

import com.devhub.entity.Post;
import com.devhub.entity.User;
import com.devhub.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndPost(User user, Post post);
    Long countByPostAndVoteType(Post post, Vote.VoteType voteType);
}
