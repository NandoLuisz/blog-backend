package com.luis.blogapp.domain.post;

import com.luis.blogapp.domain.creator.Creator;
import org.springframework.web.multipart.MultipartFile;


public record PostRequestDTO(
        String title,
        String content,
        MultipartFile imagePost,
        Creator creator
    ) {
}
