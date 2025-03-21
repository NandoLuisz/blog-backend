package com.luis.blogapp.controller;

import com.luis.blogapp.JwtUtils.JwtService;
import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.creator.CreatorResponseDTO;
import com.luis.blogapp.repository.CreatorRepository;
import com.luis.blogapp.service.AwsS3Service;
import com.luis.blogapp.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("/creator")
@RestController
public class CreatorController {

    @Autowired
    private CreatorService creatorService;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private JwtService jwtService;

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

    @PostMapping("/updateFileDefaultCreator")
    public String updateFileDefaultCreator(@RequestParam("file") MultipartFile file) throws IOException {
        return this.creatorService.uploadFileCreator(file);
    }

    @PutMapping("/update-image-creator-profile")
    public ResponseEntity<?> updateImageCreatorProfile(
            @RequestParam("imageProfile") MultipartFile imageProfile,
            @RequestParam("id") UUID id,
            @RequestParam("imageUrlProfile") String imageUrlProfile) throws IOException {

        Optional<Creator> creatorOptional = this.creatorRepository.findById(id);
        if (creatorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Creator não encontrado.");
        }

        String responseDelete = this.awsS3Service.deleteFile(imageUrlProfile, "creators");
        if (responseDelete.contains("Imagem não deletada. Erro:")) {
            return ResponseEntity.badRequest().body("Erro ao deletar imagem antiga: " + responseDelete);
        }

        String newImageProfileUrl = this.creatorService.uploadFileCreator(imageProfile);
        if (newImageProfileUrl == null || newImageProfileUrl.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao fazer upload da nova imagem.");
        }

        Creator creator = creatorOptional.get();
        creator.setImageProfileUrl(newImageProfileUrl);
        Creator updatedCreator = this.creatorRepository.save(creator);

        return ResponseEntity.ok(updatedCreator);
    }

    @GetMapping("/data-creator-by-token/{token}")
    public ResponseEntity<?> getDataCreatorByToken(@PathVariable(value = "token") String token){
        String username = this.jwtService.extractUsername(token);
        if(username.isEmpty()) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Creator não encontrado.");
        CreatorResponseDTO creator = this.creatorService.creatorByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(creator);
    }
}
