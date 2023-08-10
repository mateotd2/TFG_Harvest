package com.harvest.controller;


import com.harvest.DTOs.*;
import com.harvest.model.Empleado;
import com.harvest.model.RefreshToken;
import com.harvest.repository.EmpleadoRepository;
import com.harvest.repository.RolRepository;
import com.harvest.security.UserDetailsImpl;
import com.harvest.security.jwt.JwtGeneratorInfo;
import com.harvest.security.jwt.RefreshTokenService;
import com.harvest.services.EmpleadoServiceImpl;
import com.harvest.services.exceptions.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.util.stream.Collectors;

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
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtGeneratorInfo jwtUtils;

    @PostMapping("/signin")

    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws IncorrectSignInException {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));
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

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getEmpleado)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

}
