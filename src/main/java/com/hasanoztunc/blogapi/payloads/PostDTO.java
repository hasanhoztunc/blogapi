package com.hasanoztunc.blogapi.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long postId;
    private String title;
    private String content;
    private String slug;
    private String createdAt;
    private String updatedAt;
}
