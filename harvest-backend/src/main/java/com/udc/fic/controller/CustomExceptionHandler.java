package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.Error;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.IncorrectPasswordException;
import com.udc.fic.services.exceptions.NoRoleException;
import com.udc.fic.services.exceptions.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.management.InstanceNotFoundException;

@ControllerAdvice
public class CustomExceptionHandler {

//    @Autowired
//    private MessageSource messageSource;

    @ExceptionHandler(DuplicateInstanceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Error handleDuplicateInstance(DuplicateInstanceException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError("Duplicate");
        error.setMessage("Conflicto de instancias");
        error.setPath(uri);
        error.setStatus(409);
        return error;

    }

    @ExceptionHandler(InstanceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Error handleInstanceNotFound(InstanceNotFoundException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError("Not Found");
        error.setMessage("Instancia no encontrada");
        error.setPath(uri);
        error.setStatus(404);
        return error;

    }

    @ExceptionHandler(NoRoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleNoRoleException(NoRoleException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError("Bad request");
        error.setMessage("Ningun rol asignado");
        error.setPath(uri);
        error.setStatus(400);
        return error;

    }

    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Error handlePermissionException(PermissionException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError("No autorizado");
        error.setMessage("Ningun rol asignado");
        error.setPath(uri);
        error.setStatus(401);
        return error;

    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleIncorrectPasswordException(IncorrectPasswordException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError("No autorizado");
        error.setMessage("Contraseña no valida");
        error.setPath(uri);
        error.setStatus(400);
        return error;

    }


}