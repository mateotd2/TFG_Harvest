package com.harvest.controller;


import com.harvest.DTOs.*;
import com.harvest.model.Empleado;
import com.harvest.repository.EmpleadoRepository;
import com.harvest.repository.RolRepository;
import com.harvest.security.UserDetailsImpl;
import com.harvest.security.jwt.JwtUtils;
import com.harvest.services.EmpleadoServiceImpl;
import com.harvest.services.exceptions.DuplicateInstanceException;
import com.harvest.services.exceptions.IncorrectPasswordException;
import com.harvest.services.exceptions.IncorrectSignInException;
import com.harvest.services.exceptions.PermissionException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmpleadoRepository empleadoRepository;

    @Autowired
    EmpleadoServiceImpl empleadoServiceImpl;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")

    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws IncorrectSignInException {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws DuplicateInstanceException {


        // TODO: MEJORAR CON MAPSTRUCT
        Empleado empleado = new Empleado();
        empleado.setDni(signUpRequest.getDni());
        empleado.setBirthdate(signUpRequest.getBirthdate());
        empleado.setLastname(signUpRequest.getLastname());
        empleado.setPhone(signUpRequest.getPhone());
        empleado.setPassword(signUpRequest.getPassword());
        empleado.setNss(signUpRequest.getNss());
        empleado.setUsername(signUpRequest.getUsername());
        empleado.setName(signUpRequest.getName());
        empleado.setEmail(signUpRequest.getEmail());


        empleadoServiceImpl.signUp(empleado, signUpRequest.getRoles());

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * Change password.
     *
     * @param userId the user id
     * @param id     the id
     * @param params the params
     * @throws PermissionException        the permission exception
     * @throws InstanceNotFoundException  the instance not found exception
     * @throws IncorrectPasswordException the incorrect password exception
     */
    @PostMapping("/{id}/changePassword")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('CAPATAZ') or hasRole('TRACTORISTA') ")
    public void changePassword(@RequestAttribute Long userId, @PathVariable Long id,
                               @Validated @RequestBody ChangePasswordParamsDto params)
            throws PermissionException, InstanceNotFoundException, IncorrectPasswordException {

//        Authentication authentication = authenticationManager.authenticate()

        // TODO: Buscar el username del token con JwUtils buscarlo en base de datos y modificar la contraseña
        if (!id.equals(userId)) {
            throw new PermissionException();
        }

        empleadoServiceImpl.changePassword(id, params.getOldPassword(), params.getNewPassword());

    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("You've been signed out!"));
    }

}
