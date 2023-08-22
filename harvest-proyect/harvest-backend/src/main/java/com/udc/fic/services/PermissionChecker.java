package com.udc.fic.services;

import com.udc.fic.model.Empleado;

import javax.management.InstanceNotFoundException;

public interface PermissionChecker {

    boolean checkUsernameExist(String username);

    boolean checkEmailExists(String email);

    Empleado checkEmpleado(Long id) throws InstanceNotFoundException;

}