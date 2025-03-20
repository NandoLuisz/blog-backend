package com.luis.blogapp.controller;

import com.luis.blogapp.domain.creator.CreatorResponseDTO;
import com.luis.blogapp.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/creator")
@RestController
public class CreatorController {

    @Autowired
    private CreatorService creatorService;

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
}
