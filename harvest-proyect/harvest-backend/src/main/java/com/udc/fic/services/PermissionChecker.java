package com.udc.fic.services;

import com.udc.fic.model.Empleado;
import com.udc.fic.services.exceptions.DuplicateInstanceException;

import javax.management.InstanceNotFoundException;

public interface PermissionChecker {

    boolean checkUsernameExist(String username);

    void checkEmailExists(String email) throws DuplicateInstanceException;

    Empleado checkEmpleado(Long id) throws InstanceNotFoundException;

}