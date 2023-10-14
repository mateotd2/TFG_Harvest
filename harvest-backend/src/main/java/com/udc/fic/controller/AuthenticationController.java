package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.*;
import com.udc.fic.harvest.controller.AutenticadoApi;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.model.Empleado;
import com.udc.fic.security.UserDetailsImpl;
import com.udc.fic.security.jwt.JwtGeneratorInfo;
import com.udc.fic.services.EmpleadoService;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.IncorrectPasswordException;
import com.udc.fic.services.exceptions.NoRoleException;
import com.udc.fic.services.exceptions.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.management.InstanceNotFoundException;
import java.net.URI;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class AuthenticationController implements AutenticadoApi {

    @Autowired
    EmpleadoService empleadoService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SourceTargetMapper mapper;


    @Autowired
    JwtGeneratorInfo jwtUtils;


    @Autowired
    private MessageSource messageSource;


    @Override
    public ResponseEntity<SignInResponseDTO> _signin(SignInRequestDTO signInRequestDTO) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequestDTO.getUsername(), signInRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        SignInResponseDTO response = new SignInResponseDTO();
        response.setId(userDetails.getId());
        response.setAccessToken(jwt);
        response.setRoles(roles);
        response.setTokenType("Bearer");
        response.setUsername(userDetails.getUsername());

        return ResponseEntity.ok(response);


    }

    @Override
    public ResponseEntity<MessageResponseDTO> _changePassword(Long id, ChangePasswordDTO changePasswordDTO) throws InstanceNotFoundException {
        try {

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                throw new PermissionException();
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            Long userId = (Long) request.getAttribute("userId");


            if (!id.equals(userId)) {
                throw new PermissionException();
            }


            empleadoService.changePassword(userId, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());

            MessageResponseDTO message = new MessageResponseDTO();
            message.message("Contraseña actualizada");

            return ResponseEntity.ok(message);
        } catch (PermissionException | IncorrectPasswordException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales no validos", e);
        }
    }


    @Override
    public ResponseEntity<MessageResponseDTO> _updateUser(Long id, UpdateUserDTO updateUserDTO) {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                throw new PermissionException();
            }
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            Long userId = (Long) request.getAttribute("userId");

            if (!id.equals(userId)) {
                throw new PermissionException();
            }

            empleadoService.updateProfile(id, mapper.toEmpleado(updateUserDTO));
            MessageResponseDTO message = new MessageResponseDTO();
            message.message("Usuario con username: " + updateUserDTO.getName() + " modificado");
            return ResponseEntity.ok(message);

        } catch (PermissionException | InstanceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales no validos", e);
        } catch (DuplicateInstanceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email no valido", e);
        }
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _signUp(NewUserDTO newUserDTO) {
        try {

            Empleado empleado = mapper.toEmpleado(newUserDTO);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(empleadoService.signUp(empleado, newUserDTO.getRoles()).getId())
                    .toUri();

            MessageResponseDTO message = new MessageResponseDTO();
            message.message("Usuario con username: " + empleado.getUsername() + " creado");
            return ResponseEntity.created(location).body(message);
        } catch (DuplicateInstanceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email o username duplicado", e);
        } catch (NoRoleException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empleado sin roles", e);
        }
    }
}
