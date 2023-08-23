package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.*;
import com.udc.fic.harvest.controller.AutenticadoApi;
import com.udc.fic.model.Empleado;
import com.udc.fic.security.UserDetailsImpl;
import com.udc.fic.security.jwt.JwtGeneratorInfo;
import com.udc.fic.services.EmpleadoService;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.IncorrectPasswordException;
import com.udc.fic.services.exceptions.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AuthenticationController implements AutenticadoApi {

    /**
     * The Constant INCORRECT_LOGIN_EXCEPTION_CODE.
     */
    private static final String INCORRECT_LOGIN_EXCEPTION_CODE = "project.exceptions.IncorrectLoginException";

    /**
     * The Constant INCORRECT_PASSWORD_EXCEPTION_CODE.
     */
    private static final String INCORRECT_PASS_EXCEPTION_CODE = "project.exceptions.IncorrectPasswordException";

    @Autowired
    EmpleadoService empleadoService;

    @Autowired
    AuthenticationManager authenticationManager;


    @Autowired
    JwtGeneratorInfo jwtUtils;


    @Autowired
    private MessageSource messageSource;

//    @ExceptionHandler(IncorrectSignInException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ResponseBody
//    public Error handleIncorrectSignInException(IncorrectSignInException exception, Locale locale) {
//
//        String errorMessage = messageSource.getMessage(INCORRECT_LOGIN_EXCEPTION_CODE, null,
//                INCORRECT_LOGIN_EXCEPTION_CODE, locale);
//        Error error = new Error();
//        error.message(errorMessage);
//        return error;
//
//    }
//
//
//    @ExceptionHandler(IncorrectPasswordException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ResponseBody
//    public Error handleIncorrectPasswordException(IncorrectPasswordException exception, Locale locale) {
//
//        String errorMessage = messageSource.getMessage(INCORRECT_PASS_EXCEPTION_CODE, null,
//                INCORRECT_PASS_EXCEPTION_CODE, locale);
//
//        Error error = new Error();
//        error.message(errorMessage);
//        return error;
//
//    }

    //    @ExceptionHandler({IncorrectPasswordException.class, InstanceNotFoundException.class})
//    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Override
    public ResponseEntity<MessageResponseDTO> _changePassword(Long id, ChangePasswordDTO changePasswordDTO) throws IncorrectPasswordException, InstanceNotFoundException, PermissionException {

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails userDetails = (UserDetails) auth.getDetails();
//        userDetails.getUsername();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Long userId = (Long) request.getAttribute("userId");


        if (!id.equals(userId)) {
            throw new PermissionException();
        }


        empleadoService.changePassword(userId, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());

        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Contraseña actualizada");

        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _signOut() {
        return null;
    }

    //    @ExceptionHandler({DuplicateInstanceException.class})
//    @ResponseStatus(HttpStatus.CONFLICT)
    @Override
    public ResponseEntity<SignInResponseDTO> _signin(SignInRequestDTO signInRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequestDTO.getUsername(), signInRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        SignInResponseDTO response = new SignInResponseDTO();
        response.setId(userDetails.getId());
        response.setAccessToken(jwt);
        response.setRoles(roles);
        response.setTokenType("Bearer");
        response.setUsername(userDetails.getUsername());

        return ResponseEntity.ok(response);

    }


    @Override
//    @PostMapping("Use")
//    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_OFFICEADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageResponseDTO> _signUp(NewUserDTO newUserDTO) throws DuplicateInstanceException {
        // TODO: MEJORAR CON MAPSTRUCT
        Empleado empleado = new Empleado();
        empleado.setDni(newUserDTO.getDni());
        empleado.setBirthdate(newUserDTO.getBirthdate());
        empleado.setLastname(newUserDTO.getLastname());
        empleado.setPhone(newUserDTO.getPhone());
        empleado.setPassword(newUserDTO.getPassword());
        empleado.setNss(newUserDTO.getNss());
        empleado.setUsername(newUserDTO.getUsername());
        empleado.setName(newUserDTO.getName());
        empleado.setEmail(newUserDTO.getEmail());


        empleadoService.signUp(empleado, newUserDTO.getRoles());

        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Usuario con username: " + empleado.getUsername() + " creado");
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _updateUser(Long id, UpdateUserDTO updateUserDTO) throws PermissionException, InstanceNotFoundException, DuplicateInstanceException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (!id.equals(userId)) {
            throw new PermissionException();
        }

        empleadoService.updateProfile(id, updateUserDTO.getName(), updateUserDTO.getLastname(), updateUserDTO.getPhone(), updateUserDTO.getEmail(), updateUserDTO.getNss(), updateUserDTO.getDni(), updateUserDTO.getBirthdate());
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Usuario con username: " + updateUserDTO.getName() + " creado");
        return ResponseEntity.ok(message);
    }
}
