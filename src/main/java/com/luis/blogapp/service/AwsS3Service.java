package com.luis.blogapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.Objects;

@Service
public class AwsS3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final CreatorService creatorService;

    public AwsS3Service(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName, CreatorService creatorService) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.creatorService = creatorService;
    }

    public String deleteFile(String imageUrl, String folder) {
        if (imageUrl == null || !imageUrl.contains(bucketName + ".s3.amazonaws.com")) {
            return "URL inválida: não pertence ao bucket configurado.";
        }

        String imageProfileDefault = this.creatorService.defaultProfileFileCreator();

        if (imageUrl.equals(imageProfileDefault)) {
            return "Primeiro update.";
        }

        folder = (folder == null) ? "" : folder.trim();

        String prefix = folder.isEmpty() ? "" : folder + "/";
        String baseUrl = "https://" + bucketName + ".s3.amazonaws.com/" + prefix;

        String objectKey = imageUrl.replace(baseUrl, "");

        if (objectKey.isEmpty() || objectKey.equals(imageUrl)) {
            return "Erro: não foi possível extrair a chave do objeto.";
        }

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(prefix + objectKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
            return "Imagem deletada com sucesso: " + prefix + objectKey;
        } catch (Exception e) {
            return "Imagem não deletada. Erro: " + e.getMessage();
        }
    }
}
