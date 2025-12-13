package com.hasanoztunc.blogapi.controllers;

import com.hasanoztunc.blogapi.configs.AppConstants;
import com.hasanoztunc.blogapi.payloads.PostDTO;
import com.hasanoztunc.blogapi.payloads.PostResponse;
import com.hasanoztunc.blogapi.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<PostDTO> createPost(
            @RequestBody @Valid PostDTO postDTO
    ) {
        PostDTO createdPost = postService.createPost(postDTO);
        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/")
    public ResponseEntity<PostResponse> getAllPosts(
            @RequestParam(
                    value = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER,
                    required = false
            )
            Integer pageNumber,
            @RequestParam(
                    value = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE,
                    required = false
            )
            Integer pageSize,
            @RequestParam(
                    value = "sortBy",
                    defaultValue = AppConstants.SORT_POST_BY,
                    required = false
            )
            String sortBy,
            @RequestParam(
                    value = "sortOrder",
                    defaultValue = AppConstants.SORT_ORDER,
                    required = false
            )
            String sortOrder
    ) {
        PostResponse postResponse = postService.getAllPosts(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDTO> getPostBySlug(
            @PathVariable String slug
    ) {
        PostDTO postDTO = postService.getPostBySlug(slug);
        return ResponseEntity.ok(postDTO);
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostDTO postDTO
    ) {
        PostDTO updatedPost = postService.updatePost(postId, postDTO);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('AUTHOR') OR hasRole('ADMIN')")
    public ResponseEntity<PostDTO> deletePost(
            @PathVariable Long postId
    ) {
        PostDTO deletedPost = postService.deletePost(postId);
        return ResponseEntity.ok(deletedPost);
    }
}
