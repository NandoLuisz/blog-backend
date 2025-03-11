package com.luis.blogapp.domain.creator;

import org.springframework.web.multipart.MultipartFile;

public record CreatorRequestDTO(
        String name,
        String email,
        MultipartFile imageProfile
    ) {
}
