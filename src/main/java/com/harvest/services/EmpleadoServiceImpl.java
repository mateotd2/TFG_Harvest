package com.harvest.services;

import com.harvest.model.Empleado;
import com.harvest.model.Rol;
import com.harvest.model.RolUser;
import com.harvest.repository.EmpleadoRepository;
import com.harvest.repository.RolRepository;
import com.harvest.services.exceptions.DuplicateInstanceException;
import com.harvest.services.exceptions.IncorrectPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    EmpleadoRepository empleadoRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PermissionChecker permissionChecker;

    @Override
    public void signUp(Empleado empleado, List<String> roles) throws DuplicateInstanceException {
        if (empleadoRepository.existsByUsername(empleado.getUsername())) {
            throw new DuplicateInstanceException("Username already exists", empleado.getUsername());
        }

        if (empleadoRepository.existsByEmail(empleado.getEmail())) {
            throw new DuplicateInstanceException("Username already exists", empleado.getUsername());
        }

//        Empleado empleado = new Empleado(signUpRequest.getName(), signUpRequest.getLastname(), signUpRequest.getDni(), signUpRequest.getNss(), signUpRequest.getPhone(), signUpRequest.getEmail(), signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()), signUpRequest.getBirthdate());
        empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
//        List<String> strRoles = empleado.getRoles();
        Set<Rol> rolesParaUser = new HashSet<>();

        roles.forEach(rol -> {
            switch (rol) {
                case "admin" -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_ADMIN).orElseThrow(() -> {
                        return new RuntimeException("Error: Role is not found.");
                    });
                    rolesParaUser.add(nuevoRol);
                }

                case "tractorista" -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_TRACTORISTA).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    rolesParaUser.add(nuevoRol);
                }
                case "capataz" -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_CAPATAZ).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    rolesParaUser.add(nuevoRol);
                }

            }
        });


        empleado.setRoles(rolesParaUser);
//        empleadoRepository.saveAndFlush(empleado);
        empleadoRepository.save(empleado);
    }


    @Override
    public Empleado updateProfile(Long id, String name, String lastname, String phone, String email) {
        return null;
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) throws InstanceNotFoundException, IncorrectPasswordException {
        Empleado empleado = permissionChecker.checkEmpleado(id);

        if (!passwordEncoder.matches(oldPassword, empleado.getPassword())) {
            throw new IncorrectPasswordException();
        } else {
            empleado.setPassword(newPassword);
        }

    }
}
