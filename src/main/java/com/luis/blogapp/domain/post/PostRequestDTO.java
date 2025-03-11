package com.luis.blogapp.domain.post;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record PostRequestDTO(
        String title,
        String content,
        MultipartFile file,
        UUID creatorId
    ) {
}
