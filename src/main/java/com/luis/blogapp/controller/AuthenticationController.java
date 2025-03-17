package com.luis.blogapp.controller;

import com.luis.blogapp.domain.authentication.LoginDto;
import com.luis.blogapp.domain.authentication.LoginResponseDto;
import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.creator.CreatorRole;
import com.luis.blogapp.infra.secutiry.TokenService;
import com.luis.blogapp.repository.CreatorRepository;
import com.luis.blogapp.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CreatorRepository creatorRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CreatorService creatorService;


    @PostMapping("/login-creator")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto data){
        var creator = this.creatorRepository.findByUsername(data.username());
        if(creator == null)
            return ResponseEntity.badRequest().body("Usuário não cadastrado.");

        Creator creatorExists = this.creatorRepository.getCreatorByUsername(creator.getUsername());
        UUID id = creatorExists.getId();
        String imageProfileUrl = creatorExists.getImageProfileUrl();

        try{
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((Creator) auth.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDto(token, id, data.username(), imageProfileUrl));
        } catch (Exception error) {
            return ResponseEntity.badRequest().body("Senha incorreta.");
        }
    }

    @PostMapping("/register-creator")
    public ResponseEntity<?> registerUser(@RequestParam("username") String username,
                                          @RequestParam("email") String email,
                                          @RequestParam("imageProfile") MultipartFile imageProfile,
                                          @RequestParam("password") String password,
                                          @RequestParam("role") String role) throws IOException {
        if (this.creatorRepository.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body("Usuário já cadastrado.");
        }

        if (this.creatorRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }

        CreatorRole creatorRole;
        try {
            creatorRole = CreatorRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Role inválida.");
        }

        String imageProfileUrl = this.creatorService.uploadFile(imageProfile);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);

        Creator newCreator = new Creator(
                username,
                email,
                imageProfileUrl,
                encryptedPassword,
                creatorRole);

        this.creatorRepository.save(newCreator);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCreator);
    }
}
