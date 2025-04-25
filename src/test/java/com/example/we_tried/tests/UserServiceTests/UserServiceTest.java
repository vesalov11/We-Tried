package com.example.we_tried.tests.UserServiceTests;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.we_tried.login.LoginRequest;
import com.example.we_tried.register.RegisterRequest;
import com.example.we_tried.security.AuthenticationMetaData;
import com.example.we_tried.user.model.User;
import com.example.we_tried.user.repository.UserRepository;
import com.example.we_tried.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_shouldSaveNewUser() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(registerRequest);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
    }

    @Test
    void register_shouldThrowWhenPasswordsDontMatch() {
        registerRequest.setConfirmPassword("differentPassword");

        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
    }

    @Test
    void login_shouldReturnUserWhenCredentialsMatch() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        User result = userService.login(loginRequest);

        assertEquals(user, result);
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
    }

    @Test
    void login_shouldThrowWhenPasswordInvalid() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
    }

    @Test
    void getById_shouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getById(userId);

        assertEquals(user, result);
    }

    @Test
    void getById_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getById(userId));
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("testuser");

        assertTrue(result instanceof AuthenticationMetaData);
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void loadUserByUsername_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("testuser"));
    }
}
