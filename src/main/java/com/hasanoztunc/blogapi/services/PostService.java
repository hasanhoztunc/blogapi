package com.hasanoztunc.blogapi.services;

import com.hasanoztunc.blogapi.payloads.PostDTO;
import com.hasanoztunc.blogapi.payloads.PostResponse;

public interface PostService {
    PostDTO createPost(PostDTO postDTO);

    PostDTO getPostBySlug(String slug);

    PostResponse getAllPosts(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    );

    PostDTO updatePost(Long postId, PostDTO postDTO);

    PostDTO deletePost(Long postId);
}