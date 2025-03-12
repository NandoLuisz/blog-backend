package com.luis.blogapp.domain.creator;

import java.util.UUID;

public record CreatorResponseDTO(
        UUID id,
        String name,
        String email,
        String imageProfile) {
    public CreatorResponseDTO(Creator creator){
        this(creator.getId(), creator.getName(), creator.getEmail(), creator.getImageProfile());
    }
}
