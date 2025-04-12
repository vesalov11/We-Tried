package com.example.we_tried.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull
    @Size(min = 2, max = 30, message = "The length must be between 2 and 30 symbols")
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 6, max = 20, message = "The length must be between 6 and 20 symbols")
    private String password;

    @NotNull
    @Size(min = 6, max = 20, message = "The length must be between 6 and 20 symbols")
    private String confirmPassword;
}
