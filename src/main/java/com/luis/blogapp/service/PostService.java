package com.luis.blogapp.service;

import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.post.Post;
import com.luis.blogapp.domain.post.PostResponseDTO;
import com.luis.blogapp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
            imagePostUrl = this.uploadFile(imagePost);
        }

        Post newPost = new Post();
        newPost.setImagePostUrl(imagePostUrl);
        newPost.setTitle(title);
        newPost.setContent(content);
        newPost.setCreator(creator);
        newPost.setType(type);

        return newPost;
    }

    private String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename().replaceAll("\\s", "_");

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromByteBuffer(ByteBuffer.wrap(file.getBytes()))
        );

        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }

    public List<PostResponseDTO> getAll(){
        return postRepository.findAll().stream().map(PostResponseDTO::new).toList();
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
