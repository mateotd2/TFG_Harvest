package com.harvest.services;


import com.harvest.model.Empleado;

import javax.management.InstanceNotFoundException;


public interface PermissionChecker {

    boolean checkUsernameExist(String username);

    boolean checkEmailExists(String email);

    Empleado checkEmpleado(Long id) throws InstanceNotFoundException;

}
