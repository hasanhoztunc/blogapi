package com.hasanoztunc.blogapi.services;

import com.hasanoztunc.blogapi.exceptions.ApiException;
import com.hasanoztunc.blogapi.exceptions.ResourceNotFoundException;
import com.hasanoztunc.blogapi.models.Post;
import com.hasanoztunc.blogapi.payloads.PostDTO;
import com.hasanoztunc.blogapi.payloads.PostResponse;
import com.hasanoztunc.blogapi.repositories.PostRepository;
import com.hasanoztunc.blogapi.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    public PostServiceImpl(
            PostRepository postRepository,
            AuthUtil authUtil,
            ModelMapper modelMapper
    ) {
        this.postRepository = postRepository;
        this.authUtil = authUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public PostDTO createPost(PostDTO postDTO) {
        var isPostExist = postRepository
                .findBySlug(postDTO.getSlug());

        if (isPostExist.isPresent()) {
            throw new ApiException("Post with the same slug already exists");
        }

        var loggedInUser = authUtil.loggedInUser();

        var post = new Post(
                postDTO.getTitle(),
                postDTO.getContent(),
                postDTO.getSlug(),
                loggedInUser
        );

        var savedPost = postRepository.save(post);

        return modelMapper.map(savedPost, PostDTO.class);
    }

    @Override
    public PostDTO getPostBySlug(String slug) {
        var post = postRepository
                .findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Post", "slug", slug)
                );

        return modelMapper.map(post, PostDTO.class);
    }

    @Override
    public PostResponse getAllPosts(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    ) {
        var sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        var pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        var pagePosts = postRepository.findAll(pageDetails);

        var posts = pagePosts.getContent();

        var postsDTOs = posts.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .toList();

        var postResponse = new PostResponse();
        postResponse.setContent(postsDTOs);
        postResponse.setPageNumber(pagePosts.getNumber());
        postResponse.setPageSize(pagePosts.getSize());
        postResponse.setTotalElements(pagePosts.getTotalElements());
        postResponse.setTotalPages(pagePosts.getTotalPages());
        postResponse.setLastPage(pagePosts.isLast());

        return postResponse;
    }

    @Override
    public PostDTO updatePost(Long postId, PostDTO postDTO) {
        var loggedInUserId = authUtil.loggedInUserId();
        var postFromDb = postRepository.findByIdAndUserId(postId, loggedInUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Post", "postId", postId)
                );

        postFromDb.setTitle(postDTO.getTitle());
        postFromDb.setContent(postDTO.getContent());
        postFromDb.setSlug(postDTO.getSlug());

        postRepository.save(postFromDb);

        return modelMapper.map(postFromDb, PostDTO.class);
    }

    @Override
    public PostDTO deletePost(Long postId) {
        var loggedInUserId = authUtil.loggedInUserId();
        var postFromDb = postRepository.findByIdAndUserId(postId, loggedInUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Post", "postId", postId)
                );

        postRepository.delete(postFromDb);

        return modelMapper.map(postFromDb, PostDTO.class);
    }
}