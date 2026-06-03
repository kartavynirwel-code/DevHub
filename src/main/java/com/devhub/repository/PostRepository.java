package com.devhub.repository;

import com.devhub.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 1. Community Feed Support:
     * Kisi specific community ke saare posts fetch karne ke liye (with Pagination & Sorting).
     */
    Page<Post> findByCommunityId(Long communityId, Pageable pageable);

    /**
     * 2. User Feed Support:
     * Kisi specific user (author) ke likhe hue saare posts fetch karne ke liye.
     */
    Page<Post> findByAuthorUsername(String username, Pageable pageable);

    /**
     * 3. Search Module:
     * Title ya content ke andar keyword match karne ke liye Case-Insensitive search functionality.
     * MySQL ke 'LIKE' operator ka use karke partial matches filter karta hai.
     */
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable);

    /**
     * 4. Advanced Search (Optional Custom JPQL Query):
     * Agar aapko explicit structural control chahiye, toh aap search operations ke liye 
     * custom query ka use bhi kar sakte hain.
     */
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}