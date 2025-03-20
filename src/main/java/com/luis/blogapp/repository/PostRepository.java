package com.luis.blogapp.repository;

import com.luis.blogapp.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByCreatorId(UUID creatorId);
    Post findFirstByOrderByCreatedAtDesc();

}
