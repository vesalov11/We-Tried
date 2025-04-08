package com.example.we_tried.user.service;

import com.example.we_tried.exception.UsernameAlreadyExistsException;
import com.example.we_tried.exception.UsernameNotFoundException;
import com.example.we_tried.exception.UsernameOrPasswordNotFoundException;
import com.example.we_tried.user.model.Role;
import com.example.we_tried.user.model.User;
import com.example.we_tried.user.repository.UserRepository;
import com.example.we_tried.web.dto.LoginRequest;
import com.example.we_tried.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());
        if(optionalUser.isPresent()) {
            throw new UsernameAlreadyExistsException("This username already exists");
        }
        User user = userRepository.save(initializeUser(registerRequest));
        return user;
    }

    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .confirmPassword(passwordEncoder.encode(registerRequest.getConfirmPassword()))
                .role(Role.USER)
                .build();
    }

    public User login(LoginRequest loginRequest){
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        if(optionalUser.isEmpty()){
            throw new UsernameNotFoundException("Username not found");
        }
        User user = optionalUser.get();

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UsernameOrPasswordNotFoundException("Incorect password");
        }
        return user;
    }
}
