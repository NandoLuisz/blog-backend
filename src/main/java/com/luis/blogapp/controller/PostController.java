package com.luis.blogapp.controller;

import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.creator.CreatorResponseDTO;
import com.luis.blogapp.domain.post.Post;
import com.luis.blogapp.domain.post.PostResponseDTO;
import com.luis.blogapp.repository.CreatorRepository;
import com.luis.blogapp.repository.PostRepository;
import com.luis.blogapp.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final CreatorRepository creatorRepository;

    public PostController(PostService postService, PostRepository postRepository, CreatorRepository creatorRepository) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.creatorRepository = creatorRepository;
    }

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestParam("file") MultipartFile file,
                                        @RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        @RequestParam("creatorId") UUID creatorId,
                                        @RequestParam("type") String type) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("O arquivo não pode estar vazio!");
        }

        Optional<Creator> creatorRequest = this.creatorRepository.findById(creatorId);

        if(creatorRequest.isEmpty()) return ResponseEntity.badRequest().body("Usuário não encontrado!");

        Creator creator = creatorRequest.get();

        try {
            Post newPost = postService.createPost(file, title, content, creator, type);
            return ResponseEntity.ok(this.postRepository.save(newPost));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @GetMapping("/all-posts")
    public ResponseEntity<List<PostResponseDTO>> allPost(){
        List<PostResponseDTO> listPosts = this.postService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(listPosts);
    }

    @GetMapping("/all-post/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable(value = "id") UUID id){
        PostResponseDTO post = this.postService.getPostById(id);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @GetMapping("/last-post")
    public ResponseEntity<PostResponseDTO> getLastPost(){
        Post lastPost = this.postRepository.findFirstByOrderByCreatedAtDesc();
        return ResponseEntity.status(HttpStatus.OK).body(new PostResponseDTO(lastPost));
    }

    @GetMapping("/arquivos")
    public List<String> listarArquivos() {
        return postService.listarArquivos();
    }


}
