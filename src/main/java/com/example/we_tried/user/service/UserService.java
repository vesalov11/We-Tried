package com.example.we_tried.user.service;

import com.example.we_tried.user.model.User;
import com.example.we_tried.user.repository.UserRepository;
import com.example.we_tried.login.LoginRequest;
import com.example.we_tried.register.RegisterRequest;
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

    public void register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());
        if(optionalUser.isPresent()) {
            throw new RuntimeException("This username already exists");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match!");
        }

        userRepository.save(initializeUser(registerRequest));
    }

    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();
    }

    public User login(LoginRequest loginRequest){

        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        if(optionalUser.isEmpty()){
            throw new RuntimeException("Invalid username or password");
        }
        User user = optionalUser.get();

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }
}
