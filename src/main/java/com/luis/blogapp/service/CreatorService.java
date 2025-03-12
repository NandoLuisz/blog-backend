package com.luis.blogapp.service;

import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.creator.CreatorRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class CreatorService {

    private final S3Client s3Client;
    private final String bucketName;

    public CreatorService(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public Creator createCreator(String name, String email, MultipartFile imageProfile) {
        String imgUrl = null;

        if (imageProfile != null) {
            try {
                imgUrl = this.uploadFile(imageProfile);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da imagem do perfil.", e);
            }
        }

        return new Creator(name, email, imgUrl);
    }

    private String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
            );
        } catch (Exception e) {
            throw new IOException("Falha no upload do arquivo para o S3", e);
        }

        System.out.println("Deu certo o envio da imagem!");
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }
}
