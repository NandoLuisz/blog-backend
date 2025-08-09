package com.luis.blogapp.controller;

import com.luis.blogapp.domain.authentication.LoginDto;
import com.luis.blogapp.domain.authentication.LoginResponseDto;
import com.luis.blogapp.domain.authentication.RegisterRequestDto;
import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.creator.CreatorRole;
import com.luis.blogapp.infra.secutiry.TokenService;
import com.luis.blogapp.repository.CreatorRepository;
import com.luis.blogapp.service.CreatorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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


    @Operation(summary = "Login do criador", description = "Loga o criador a aplicação")
    @PostMapping("/login-creator")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto data){
        var creator = this.creatorRepository.findByUsername(data.username());
        if(creator == null) {
            return ResponseEntity.badRequest().body("Usuário não cadastrado.");
        }

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

    @Operation(summary = "Registra um novo criador", description = "Cria uma conta para um novo criador")
    @PostMapping("/register-creator")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto data) throws IOException {
        if (this.creatorRepository.findByUsername(data.username()) != null) {
            return ResponseEntity.badRequest().body("Usuário já cadastrado.");
        }

        if (this.creatorRepository.existsByEmail(data.email())) {
            return ResponseEntity.badRequest().body("Email já cadastrado.");
        }

        CreatorRole creatorRole;
        try {
            creatorRole = CreatorRole.valueOf(data.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Role inválida.");
        }

        String imageProfileUrl = this.creatorService.defaultProfileFileCreator();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(data.password());

        Creator newCreator = new Creator(
                data.username(),
                data.email(),
                imageProfileUrl,
                encryptedPassword,
                creatorRole);

        this.creatorRepository.save(newCreator);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCreator);
    }
}
