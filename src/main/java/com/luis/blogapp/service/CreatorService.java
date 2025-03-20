package com.luis.blogapp.service;

import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.creator.CreatorResponseDTO;
import com.luis.blogapp.domain.creator.CreatorRole;
import com.luis.blogapp.repository.CreatorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CreatorService {

    private final S3Client s3Client;
    private final String bucketName;
    private final CreatorRepository creatorRepository;

    public CreatorService(S3Client s3Client,
                          @Value("${aws.s3.bucket-name}")
                          String bucketName, CreatorRepository creatorRepository) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.creatorRepository = creatorRepository;
    }

    public List<CreatorResponseDTO> getAll(){
        return creatorRepository.findAll().stream().map(CreatorResponseDTO::new).toList();
    }

    public CreatorResponseDTO getCreatorById(UUID creatorId){
        Creator creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        return new CreatorResponseDTO(creator);
    }

    public String uploadFileCreator(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo vazio.");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("Arquivo muito grande. O tamanho máximo permitido é 10 MB.");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IOException("Apenas arquivos de imagem são permitidos.");
        }

        String prefix = "creators/";

        String fileName = prefix + UUID.randomUUID() + "-" + file.getOriginalFilename().replaceAll("\\s", "_");

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            System.out.println("Upload realizado com sucesso: " + fileName);
        } catch (S3Exception e) {
            throw new IOException("Erro ao enviar arquivo para o S3: " + e.getMessage(), e);
        }
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }

    public String defaultProfileFileCreator(){
        return "https://upload-images-app-blog.s3.amazonaws.com/creators/779b7d20-ddbd-4ab8-b249-94bc23e55d7d-defaultProfile.jpg";
    }
}
