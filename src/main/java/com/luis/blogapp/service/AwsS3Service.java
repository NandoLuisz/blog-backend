package com.luis.blogapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class AwsS3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public AwsS3Service(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String deleteFile(String imageUrl) {
        if (!imageUrl.contains(bucketName + ".s3.amazonaws.com")) {
            return "URL inválida: não pertence ao bucket configurado.";
        }

        String objectKey = imageUrl.replace("https://" + bucketName + ".s3.amazonaws.com/", "");

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
            System.out.println("Imagem deletada com sucesso: " + objectKey);
            return "Imagem deletada com sucesso: " + objectKey;
        } catch (Exception e) {
            System.out.println("Erro ao deletar imagem: " + e.getMessage());
            return "Imagem não deletada. Erro: " + e.getMessage();
        }
    }
}
