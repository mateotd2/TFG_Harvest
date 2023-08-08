package com.harvest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// CONTROLADOR PARA TESTEO
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
@EnableWebSecurity
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/capataz")
    @PreAuthorize("hasRole('CAPATAZ') or hasRole('TRACTORISTA')")
//    @Secured("capataz")
    public String capatazAccess() {
        return "Capataz y Tractorista Content.";
    }

    @GetMapping("/tractor")
    @PreAuthorize("hasRole('TRACTORISTA')")
//    @Secured("capataz_tractorista")
    public String tractorAccess() {
        return "Tractorista access.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
//    @Secured("ADMIN")
    public String adminAccess() {
        return "Admin Board.";
    }
}