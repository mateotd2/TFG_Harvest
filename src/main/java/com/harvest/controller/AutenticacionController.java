package com.harvest.controller;


import com.harvest.DTOs.LoginRequest;
import com.harvest.DTOs.MessageResponse;
import com.harvest.DTOs.SignupRequest;
import com.harvest.DTOs.UserInfoResponse;
import com.harvest.empleado.Empleado;
import com.harvest.empleado.Rol;
import com.harvest.empleado.RolUser;
import com.harvest.repository.EmpleadoRepository;
import com.harvest.repository.RolRepository;
import com.harvest.security.UserDetailsImpl;
import com.harvest.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmpleadoRepository empleadoRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles.get(0)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (empleadoRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (empleadoRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Empleado empleado = new Empleado(signUpRequest.getName(), signUpRequest.getLastname(), signUpRequest.getDni(), signUpRequest.getNss(), signUpRequest.getPhone(), signUpRequest.getEmail(), signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()), signUpRequest.getBirthdate());

        List<String> strRoles = signUpRequest.getRoles();
        Set<Rol> rolesParaUser = new HashSet<>();

        strRoles.forEach(rol -> {
            switch (rol) {
                case "admin" -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_ADMIN).orElseThrow(() -> {
                        return new RuntimeException("Error: Role is not found.");
                    });
                    rolesParaUser.add(nuevoRol);
                }

                case "tractorista" -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_TRACTORISTA).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    rolesParaUser.add(nuevoRol);
                }

                default -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_CAPATAZ).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    rolesParaUser.add(nuevoRol);
                }
            }
        });


        empleado.setRoles(rolesParaUser);
        empleadoRepository.saveAndFlush(empleado);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You've been signed out!"));
    }
}
