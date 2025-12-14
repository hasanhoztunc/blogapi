package com.hasanoztunc.blogapi.services;

import com.hasanoztunc.blogapi.exceptions.ResourceNotFoundException;
import com.hasanoztunc.blogapi.models.Comment;
import com.hasanoztunc.blogapi.payloads.CommentDTO;
import com.hasanoztunc.blogapi.payloads.CommentResponse;
import com.hasanoztunc.blogapi.repositories.CommentRepository;
import com.hasanoztunc.blogapi.repositories.PostRepository;
import com.hasanoztunc.blogapi.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            PostRepository postRepository,
            AuthUtil authUtil,
            ModelMapper modelMapper
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.authUtil = authUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO, Long postId) {
        var loggedInUser = authUtil.loggedInUser();

        var post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Post", "postId", postId)
                );

        var comment = new Comment(
                commentDTO.getContent(),
                post,
                loggedInUser
        );

        var savedComment = commentRepository.save(comment);

        return modelMapper.map(savedComment, CommentDTO.class);
    }

    @Override
    public CommentResponse getCommentsByPost(
            Long postId,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    ) {
        var sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        var pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        var pageComments = commentRepository.findByPostId(pageDetails, postId);
        var comments = pageComments.getContent();

        var commentDTOs = comments.stream()
                .map(comment -> modelMapper.map(comment, CommentDTO.class))
                .toList();

        var commentResponse = new CommentResponse();
        commentResponse.setContent(commentDTOs);
        commentResponse.setPageNumber(pageComments.getNumber());
        commentResponse.setPageSize(pageComments.getSize());
        commentResponse.setTotalElements(pageComments.getTotalElements());
        commentResponse.setTotalPages(pageComments.getTotalPages());
        commentResponse.setLastPage(pageComments.isLast());

        return commentResponse;
    }

    @Override
    @Transactional
    public CommentDTO deleteComment(Long commentId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Comment", "commentId", commentId)
                );

        commentRepository.delete(comment);

        return modelMapper.map(comment, CommentDTO.class);
    }
}