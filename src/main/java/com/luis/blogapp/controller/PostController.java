package com.luis.blogapp.controller;

import com.luis.blogapp.domain.post.Post;
import com.luis.blogapp.repository.PostRepository;
import com.luis.blogapp.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestParam("file") MultipartFile file,
                                        @RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        @RequestParam("creatorId") UUID creatorId) {
        try {
            Post newPost = postService.createPost(file, title, content, creatorId);
            return ResponseEntity.ok(this.postRepository.save(newPost));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @GetMapping("/arquivos")
    public List<String> listarArquivos() {
        return postService.listarArquivos();
    }


}
