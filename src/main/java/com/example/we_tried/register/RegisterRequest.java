package com.example.we_tried.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 30, message = "The length must be between 2 and 30 symbols")
    private String username;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "The length must be between 6 and 20 symbols")
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 6, max = 20, message = "The length must be between 6 and 20 symbols")
    private String confirmPassword;
}
