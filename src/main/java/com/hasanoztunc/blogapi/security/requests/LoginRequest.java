package com.hasanoztunc.blogapi.security.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
final public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
