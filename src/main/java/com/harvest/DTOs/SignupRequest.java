package com.harvest.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignupRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    private String role;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String lastname;

    @NotBlank
    private String dni;

    @NotBlank
    private String nss;

    @NotBlank
    private String phone;

    private LocalDate birthdate;


}