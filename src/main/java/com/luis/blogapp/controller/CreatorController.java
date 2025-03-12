package com.luis.blogapp.controller;

import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.creator.CreatorRequestDTO;
import com.luis.blogapp.domain.creator.CreatorResponseDTO;
import com.luis.blogapp.repository.CreatorRepository;
import com.luis.blogapp.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/all-creators")
    public ResponseEntity<List<CreatorResponseDTO>> allCreators(){
        List<CreatorResponseDTO> listCreators = this.creatorService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(listCreators);
    }

    @GetMapping("/all-creators/{creatorId}")
    public ResponseEntity<CreatorResponseDTO> getCreatorById(@PathVariable(value = "creatorId") UUID creatorId){
        CreatorResponseDTO creator = this.creatorService.getCreatorById(creatorId);
        return ResponseEntity.status(HttpStatus.OK).body(creator);
    }
}
