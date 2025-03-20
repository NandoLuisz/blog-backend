package com.luis.blogapp.controller;

import com.luis.blogapp.service.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("aws/s3")
@RestController
public class AwsS3Controller {

    @Autowired
    private AwsS3Service awsS3Service;

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(@RequestParam String imageUrl,
                                              @RequestParam String folder){
        String responseMessage = awsS3Service.deleteFile(imageUrl, folder);

        if (responseMessage.contains("Imagem deletada com sucesso")) {
            return ResponseEntity.ok(responseMessage);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
        }
    }
}
