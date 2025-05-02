package com.example.we_tried.login;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotNull
    @Size(min = 2, max = 30, message = "The length must be between 2 and 30 symbols")
    private String username;

    @NotNull
    @Size(min = 6, max = 20, message = "The length must be between 6 and 20 symbols")
    private String password;

}
