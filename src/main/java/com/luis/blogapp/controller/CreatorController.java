package com.luis.blogapp.controller;

import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.creator.CreatorRequestDTO;
import com.luis.blogapp.repository.CreatorRepository;
import com.luis.blogapp.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RequestMapping("/creator")
@RestController
public class CreatorController {

    @Autowired
    private CreatorService creatorService;

    @Autowired
    private CreatorRepository creatorRepository;

    @PostMapping("/create-creator")
    public ResponseEntity<?> create(@RequestParam("name") String name,
                                    @RequestParam("email") String email,
                                    @RequestParam("imageProfile") MultipartFile imageProfile) {

        if(this.creatorRepository.findCreatorByName(name) != null){
            return ResponseEntity.badRequest().body("Usuário já cadastrado!");
        }
        if(this.creatorRepository.findCreatorByEmail(email) != null){
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        Creator newCreator = this.creatorService.createCreator(name, email, imageProfile);

        return ResponseEntity.ok(this.creatorRepository.save(newCreator));
    }
}
