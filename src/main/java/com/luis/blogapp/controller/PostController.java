package com.luis.blogapp.controller;

import com.luis.blogapp.domain.creator.Creator;
import com.luis.blogapp.domain.post.Post;
import com.luis.blogapp.domain.post.PostResponseDTO;
import com.luis.blogapp.repository.CreatorRepository;
import com.luis.blogapp.repository.PostRepository;
import com.luis.blogapp.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Operation(summary = "Cria uma post", description = "Cria um post com os seguintes parâmetros: imagem do post, título, conteúdo, id do criador e tipo de conteúdo")
    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestParam("imagePost") MultipartFile imagePost,
                                        @RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        @RequestParam("creatorId") UUID creatorId,
                                        @RequestParam("type") String type) {

        if (imagePost.isEmpty()) {
            return ResponseEntity.badRequest().body("O arquivo não pode estar vazio!");
        }

        Optional<Creator> creatorRequest = this.creatorRepository.findById(creatorId);

        if(creatorRequest.isEmpty()) return ResponseEntity.badRequest().body("Usuário não encontrado!");

        Creator creator = creatorRequest.get();

        try {
            Post newPost = postService.createPost(imagePost, title, content, creator, type);
            return ResponseEntity.ok(this.postRepository.save(newPost));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @Operation(summary = "Lista todos os posts", description = "Retorna uma lista de todos os posts")
    @GetMapping("/all-posts")
    public ResponseEntity<List<PostResponseDTO>> allPost() {
        List<PostResponseDTO> listPosts = this.postService.getAll();

        if (listPosts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(listPosts);
    }

    @Operation(summary = "Lista todos os posts de um criador", description = "Retorna uma lista de posts de um criador")
    @GetMapping("/all-posts-by-creator/{createdId}")
    public ResponseEntity<List<PostResponseDTO>> allPostByCreator(@PathVariable UUID createdId) {
        List<PostResponseDTO> listPosts = this.postService.allPostByCreator(createdId);

        if (listPosts == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(listPosts);
    }

    @Operation(summary = "Lista um post pelo seu id", description = "Retorna um post buscado pelo seu id")
    @GetMapping("/all-post/{id}")
    public ResponseEntity<?> getPostById(@PathVariable(value = "id") UUID id){
        PostResponseDTO post = this.postService.getPostById(id);
        if(post == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @Operation(summary = "Lista o último post feito", description = "Retorna o último post")
    @GetMapping("/last-post")
    public ResponseEntity<?> getLastPost() {
        Post lastPost = this.postRepository.findFirstByOrderByCreatedAtDesc();

        if (lastPost == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(new PostResponseDTO(lastPost));
    }

    @Operation(summary = "Deleta um post pelo id", description = "Deleta um post pelo id")
    @DeleteMapping("/delete-post/{id}")
    public ResponseEntity<Object> deletePost(@PathVariable(value = "id") UUID id) {
        Optional<Post> post = this.postRepository.findById(id);
        if(!post.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post não encontrado!");
        }
        postRepository.delete(post.get());
        return ResponseEntity.status(HttpStatus.OK).body("Post deletado com sucesso!");
    }

}
