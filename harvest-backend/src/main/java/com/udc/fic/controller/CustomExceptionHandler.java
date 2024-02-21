package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.Error;
import com.udc.fic.services.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.management.InstanceNotFoundException;

@ControllerAdvice
public class CustomExceptionHandler {

    static final String BAD_REQUEST = "Bad Request";

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

    @ExceptionHandler(InvalidChecksException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleInvalidChecksExceptionInstance(InvalidChecksException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError(BAD_REQUEST);
        error.setMessage("CheckIn o Checkout Invalido");
        error.setPath(uri);
        error.setStatus(400);
        return error;

    }

    @ExceptionHandler(TaskAlreadyEndedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleTaskAlreadyEndedExceptionInstance(TaskAlreadyEndedException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError(BAD_REQUEST);
        error.setMessage("La tarea finalizada anteriormente");
        error.setPath(uri);
        error.setStatus(400);
        return error;

    }

    @ExceptionHandler(TaskAlreadyStartedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleTaskAlreadyStartedExceptionInstance(TaskAlreadyStartedException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError(BAD_REQUEST);
        error.setMessage("La tarea iniciada anteriormente");
        error.setPath(uri);
        error.setStatus(400);
        return error;

    }

    @ExceptionHandler(TaskNotStartedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleTaskNotStartedExceptionInstance(TaskNotStartedException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError(BAD_REQUEST);
        error.setMessage("Tarea sin iniciar");
        error.setPath(uri);
        error.setStatus(400);
        return error;

    }

    @ExceptionHandler(InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleInvalidDateExceptionInstance(InvalidDateException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError(BAD_REQUEST);
        error.setMessage("Dia de trabajo invalido");
        error.setPath(uri);
        error.setStatus(400);
        return error;

    }

    @ExceptionHandler(WorkerNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error handleWorkerNotAvailableExceptionInstance(WorkerNotAvailableException exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Error error = new Error();
        error.setError(BAD_REQUEST);
        error.setMessage("Trabajador no esta habilitado");
        error.setPath(uri);
        error.setStatus(400);
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

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Error handleUsernameNotFound(UsernameNotFoundException exception, HttpServletRequest request) {
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
        error.setError(BAD_REQUEST);
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
