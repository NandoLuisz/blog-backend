package com.luis.blogapp.service;

import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.post.Post;
import com.luis.blogapp.domain.post.PostResponseDTO;
import com.luis.blogapp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final S3Client s3Client;
    private final PostRepository postRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public PostService(S3Client s3Client, PostRepository postRepository) {
        this.s3Client = s3Client;
        this.postRepository = postRepository;
    }

    public Post createPost(MultipartFile imagePost, String title, String content, Creator creator, String type) throws IOException {
        String imagePostUrl = null;

        if(imagePost != null){
            imagePostUrl = this.uploadFilePost(imagePost);
        }

        Post newPost = new Post();
        newPost.setImagePostUrl(imagePostUrl);
        newPost.setTitle(title);
        newPost.setContent(content);
        newPost.setCreator(creator);
        newPost.setType(type);

        return newPost;
    }

    public String uploadFilePost(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo vazio.");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new IOException("Apenas arquivos de imagem são permitidos.");
        }

        String prefix = "posts/";

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

    public List<PostResponseDTO> getAll(){
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDTO::new).toList();
    }

    public List<PostResponseDTO> allPostByCreator(UUID creatorId){
        List<Post> allPostByCreator = postRepository.findAllByCreatorId(creatorId);
        if(allPostByCreator.isEmpty()) return null;
        return allPostByCreator.stream().map(PostResponseDTO::new).toList();
    }

    public PostResponseDTO getPostById(UUID postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado!"));
        return new PostResponseDTO(post);
    }

    public List<String> listarArquivos() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(request);

        return result.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

}
