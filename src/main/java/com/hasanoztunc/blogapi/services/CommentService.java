package com.hasanoztunc.blogapi.services;

import com.hasanoztunc.blogapi.payloads.CommentDTO;
import com.hasanoztunc.blogapi.payloads.CommentResponse;

public interface CommentService {

    CommentDTO createComment(CommentDTO commentDTO, Long postId);

    CommentResponse getCommentsByPost(
            Long postId,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    );

    CommentDTO deleteComment(Long commentId);
}