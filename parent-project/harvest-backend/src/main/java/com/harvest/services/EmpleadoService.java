package com.harvest.services;

import com.harvest.model.Empleado;
import com.harvest.services.exceptions.DuplicateInstanceException;
import com.harvest.services.exceptions.IncorrectPasswordException;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface EmpleadoService {


    void signUp(Empleado empleado, List<String> roles) throws DuplicateInstanceException;


    Empleado updateProfile(Long id, String name, String lastname, String phone, String email);

    void changePassword(Long id, String oldPassword, String newPassword) throws InstanceNotFoundException, IncorrectPasswordException;
}
