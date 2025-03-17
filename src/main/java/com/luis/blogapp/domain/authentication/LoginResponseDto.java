package com.luis.blogapp.domain.authentication;

import java.util.UUID;

public record LoginResponseDto(
        String token,
        UUID id,
        String username,
        String imageProfileUrl){
}
