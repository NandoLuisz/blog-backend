package com.luis.blogapp.service;

import com.luis.blogapp.domain.post.Post;
import com.luis.blogapp.domain.post.PostRequestDTO;
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

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public PostService(S3Client s3Client) {
        this.s3Client = s3Client;
    }


    public Post createPost(MultipartFile file, String title, String content, UUID creatorId) throws IOException {
        String imgURL = null;

        if(file != null){
//            imgURL = this.uploadFile(data.imgURL());
            imgURL = "https://img.webp";
        }

        Post newPost = new Post();
        newPost.setImageURL(imgURL);
        newPost.setTitle(title);
        newPost.setContent(content);
        newPost.setCreatorId(creatorId);

        return newPost;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromByteBuffer(ByteBuffer.wrap(file.getBytes()))
        );

        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
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

//    private String uploadImg(MultipartFile multipartFile) {
//        String filename = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
//
//        try {
//            PutObjectRequest putOb = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(filename)
//                    .build();
//            s3Client.putObject(putOb, RequestBody.fromByteBuffer(ByteBuffer.wrap(multipartFile.getBytes())));
//            GetUrlRequest request = GetUrlRequest.builder()
//                    .bucket(bucketName)
//                    .key(filename)
//                    .build();
//            return s3Client.utilities().getUrl(request).toString();
//        } catch (Exception e) {
//            log.error("erro ao subir arquivo: {}", e.getMessage());
//            return "";
//        }
//    }
}
