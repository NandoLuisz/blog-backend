package com.luis.blogapp.domain.creator;

import java.util.UUID;

public record CreatorResponseDTO(
        UUID id,
        String username,
        String email,
        String imageProfileUrl,
        CreatorRole role) {
    public CreatorResponseDTO(Creator creator){
        this(creator.getId(), creator.getUsername(), creator.getEmail(), creator.getImageProfileUrl(), creator.getRole());
    }
}
