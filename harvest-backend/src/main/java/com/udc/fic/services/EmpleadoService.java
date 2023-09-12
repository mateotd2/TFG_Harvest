package com.udc.fic.services;

import com.udc.fic.model.Empleado;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.IncorrectPasswordException;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface EmpleadoService {


    Empleado signUp(Empleado empleado, List<String> roles) throws DuplicateInstanceException;


    Empleado updateProfile(Long id, Empleado empleado) throws InstanceNotFoundException, DuplicateInstanceException;

    void changePassword(Long id, String oldPassword, String newPassword) throws InstanceNotFoundException, IncorrectPasswordException;
}
