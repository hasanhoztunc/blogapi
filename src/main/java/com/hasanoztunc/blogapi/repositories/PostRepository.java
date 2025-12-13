package com.hasanoztunc.blogapi.repositories;

import com.hasanoztunc.blogapi.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    @Query("SELECT p FROM Post p WHERE p.id = :postId AND p.user.id = :userId")
    Optional<Post> findByIdAndUserId(Long postId, Long userId);
}