package com.hasanoztunc.blogapi.services;

import com.hasanoztunc.blogapi.security.requests.LoginRequest;
import com.hasanoztunc.blogapi.security.requests.RegisterRequest;
import com.hasanoztunc.blogapi.security.responses.MessageResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> registerUser(RegisterRequest registerRequest);

    ResponseEntity<?> loginUser(LoginRequest loginRequest);

    ResponseEntity<?> logoutUser();
}