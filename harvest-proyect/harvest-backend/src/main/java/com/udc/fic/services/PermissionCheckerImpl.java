package com.udc.fic.services;

import com.udc.fic.model.Empleado;
import com.udc.fic.repository.EmpleadoRepository;
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

    @Override
    public boolean checkUsernameExist(String username) {
        return empleadoRepository.existsByUsername(username);
    }

    @Override
    public boolean checkEmailExists(String email) {
        return empleadoRepository.existsByEmail(email);
    }

    @Override
    public Empleado checkEmpleado(Long id) throws InstanceNotFoundException {
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        if (empleado.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        return empleado.get();
    }
}