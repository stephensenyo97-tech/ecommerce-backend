package com.example.ecommerce.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequest {
    @NotBlank(message = "required")
    @Size(min = 3, max =50, message = "name must be between 2 and 51")
    private String firstname;

    @NotBlank(message = "required")
    @Size(min = 3, max =50, message = "name must be between 2 and 51")
    private String lastname;

    @Email(message = "invalid email format")
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message =  "password is required")
    @Size(min=8,  max= 72, message = "password cannot be less than 8 characters")
    private String password;

}
