package com.luis.blogapp.domain.post;

import com.luis.blogapp.domain.creator.Creator;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponseDTO(
        UUID id,
        String title,
        String content,
        String imagePostUrl,
        Creator creator,
        LocalDateTime createdAt,
        String type) {
    public PostResponseDTO(Post post){
        this(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getImagePostUrl(),
            post.getCreator(),
            post.getCreatedAt(),
            post.getType()
        );
    }
}
