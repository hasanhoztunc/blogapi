package com.hasanoztunc.blogapi.utils;

import com.hasanoztunc.blogapi.models.User;
import com.hasanoztunc.blogapi.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
final public class AuthUtil {

    private final UserRepository userRepository;

    public AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String loggedInEmail() {
        return loggedInUser().getEmail();
    }

    public Long loggedInUserId() {
        return loggedInUser().getUserId();
    }

    public User loggedInUser() {
        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + authentication.getName())
                );
    }
}