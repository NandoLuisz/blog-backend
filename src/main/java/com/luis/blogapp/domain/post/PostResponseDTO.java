package com.luis.blogapp.domain.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luis.blogapp.domain.creator.Creator;

import java.time.LocalDateTime;
import java.util.UUID;

public class PostResponseDTO {
    private UUID id;
    private String title;
    private String content;
    private String imagePostUrl;
    private Creator creator;
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public PostResponseDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imagePostUrl = post.getImagePostUrl();
        this.creator = post.getCreator();
        this.type = post.getType();
        this.createdAt = post.getCreatedAt();
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImagePostUrl() { return imagePostUrl; }
    public Creator getCreator() { return creator; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
