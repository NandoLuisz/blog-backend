package com.luis.blogapp.domain.authentication;

public record RegisterRequestDto(String username, String email, String password, String role) {
}
