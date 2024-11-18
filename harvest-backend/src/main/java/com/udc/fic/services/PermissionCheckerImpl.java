package com.udc.fic.services;

import com.udc.fic.model.Empleado;
import com.udc.fic.model.Rol;
import com.udc.fic.model.RolUser;
import com.udc.fic.repository.EmpleadoRepository;
import com.udc.fic.repository.RolRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PermissionCheckerImpl implements PermissionChecker {

    @Autowired
    EmpleadoRepository empleadoRepository;

    @Autowired
    RolRepository rolRepository;

    @Override
    public boolean checkUsernameExist(String username) {
        return empleadoRepository.existsByUsername(username);
    }

    @Override
    public void checkEmailExists(String email) throws DuplicateInstanceException {
        if (empleadoRepository.existsByEmail(email)) {
            throw new DuplicateInstanceException("email already exists", email);
        }
    }

    @Override
    public Empleado checkEmpleado(Long id) throws InstanceNotFoundException {
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        if (empleado.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return empleado.get();
    }

    @Override
    public Empleado checkTractorista(Long id) throws InstanceNotFoundException, PermissionException {
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        if (empleado.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        Optional<Rol> optionalRol = rolRepository.findByName(RolUser.ROLE_TRACTORISTA);
        if (optionalRol.isEmpty()) throw new InstanceNotFoundException();
        if (!empleado.get().getRoles().contains(optionalRol.get())) {
            throw new PermissionException();
        }
        return empleado.get();
    }
}