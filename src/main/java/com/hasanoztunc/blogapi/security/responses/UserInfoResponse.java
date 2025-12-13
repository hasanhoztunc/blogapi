package com.hasanoztunc.blogapi.security.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
final public class UserInfoResponse {
    private Long id;
    private String username;
    private List<String> roles;
}