package com.hasanoztunc.blogapi.services;

import com.hasanoztunc.blogapi.exceptions.ApiException;
import com.hasanoztunc.blogapi.models.Role;
import com.hasanoztunc.blogapi.models.User;
import com.hasanoztunc.blogapi.models.UserRole;
import com.hasanoztunc.blogapi.repositories.RoleRepository;
import com.hasanoztunc.blogapi.repositories.UserRepository;
import com.hasanoztunc.blogapi.security.jwt.JwtUtils;
import com.hasanoztunc.blogapi.security.requests.LoginRequest;
import com.hasanoztunc.blogapi.security.requests.RegisterRequest;
import com.hasanoztunc.blogapi.security.responses.MessageResponse;
import com.hasanoztunc.blogapi.security.responses.UserInfoResponse;
import com.hasanoztunc.blogapi.security.services.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUserName(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        var user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );

        Set<String> rolesString = registerRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (rolesString == null) {
            Role userRole = roleRepository.findByRoleName(UserRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            rolesString.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByRoleName(UserRole.ROLE_ADMIN)
                                .orElseThrow(() -> new ApiException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    case "author" -> {
                        Role modRole = roleRepository.findByRoleName(UserRole.ROLE_AUTHOR)
                                .orElseThrow(() -> new ApiException("Error: Role is not found."));
                        roles.add(modRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByRoleName(UserRole.ROLE_USER)
                                .orElseThrow(() -> new ApiException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity
                .ok(new MessageResponse("User registered successfully!"));
    }

    @Override
    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            return new ResponseEntity(
                    new MessageResponse("Error: Invalid username or password!"),
                    HttpStatus.UNAUTHORIZED
            );
        }

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        var roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var userInfoResponse = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        );

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(userInfoResponse);
    }

    @Override
    public ResponseEntity<?> logoutUser() {
        var cookie = jwtUtils.getCleanJwtCookie();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been logged out!"));
    }
}