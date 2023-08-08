package com.harvest.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SignupRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotEmpty
    private List<String> roles;

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