package com.hasanoztunc.blogapi.controllers;

import com.hasanoztunc.blogapi.configs.AppConstants;
import com.hasanoztunc.blogapi.payloads.CommentDTO;
import com.hasanoztunc.blogapi.payloads.CommentResponse;
import com.hasanoztunc.blogapi.services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments" )
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDTO commentDTO
    ) {
        var savedComment = commentService.createComment(commentDTO, postId);

        return ResponseEntity.ok(savedComment);
    }

    @GetMapping("/posts/{postId}/comments" )
    public ResponseEntity<CommentResponse> getComments(
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
                    defaultValue = AppConstants.SORT_COMMENT_BY,
                    required = false
            )
            String sortBy,
            @RequestParam(
                    value = "sortOrder",
                    defaultValue = AppConstants.SORT_ORDER,
                    required = false
            )
            String sortOrder,
            @PathVariable Long postId
    ) {
        var commentResponse = commentService.getCommentsByPost(
                postId,
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );

        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/comments/{commentId}" )
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable Long commentId) {
        var comment = commentService.deleteComment(commentId);

        return ResponseEntity.ok(comment);
    }
}
