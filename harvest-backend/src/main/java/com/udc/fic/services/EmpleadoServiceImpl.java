package com.udc.fic.services;

import com.udc.fic.model.Empleado;
import com.udc.fic.model.Rol;
import com.udc.fic.model.RolUser;
import com.udc.fic.repository.EmpleadoRepository;
import com.udc.fic.repository.RolRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.IncorrectPasswordException;
import com.udc.fic.services.exceptions.NoRoleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {
    public static final String ROLE_NOT_VALID = "Registro de usuario fallido, ROL invalido";
    public static final String ROLE_IS_NOT_FOUND = "Error: Role is not found.";
    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoServiceImpl.class);
    @Autowired
    EmpleadoRepository empleadoRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PermissionChecker permissionChecker;


    @Override
    public Empleado signUp(Empleado empleado, List<String> roles) throws DuplicateInstanceException, NoRoleException {

        if (roles.isEmpty()) {
            LOGGER.error("Registro de usuario fallido, ningun rol.");
            throw new NoRoleException("Registro de usuario fallido, ningun rol ");
        }

        LOGGER.info("Registro de usuario {} ", empleado.getUsername());
        if (empleadoRepository.existsByUsername(empleado.getUsername())) {
            LOGGER.error("Registro de usuario fallido Username no disponible");
            throw new DuplicateInstanceException("Username already exists", empleado.getUsername());
        }

        if (empleadoRepository.existsByEmail(empleado.getEmail())) {
            LOGGER.error("Registro de usuario fallido Email duplicado");
            throw new DuplicateInstanceException("Username already exists", empleado.getUsername());
        }


        empleado.setPassword(bCryptPasswordEncoder.encode(empleado.getPassword()));
        Set<Rol> rolesParaUser = new HashSet<>();

        roles.forEach(rol -> {
            switch (rol) {
                case "admin" -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_ADMIN).orElseThrow(() -> {
                        LOGGER.error(ROLE_NOT_VALID);
                        return new RuntimeException(ROLE_IS_NOT_FOUND);
                    });
                    rolesParaUser.add(nuevoRol);
                }

                case "tractorista" -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_TRACTORISTA).orElseThrow(() -> {
                        LOGGER.error(ROLE_NOT_VALID);
                        return new RuntimeException(ROLE_IS_NOT_FOUND);
                    });
                    rolesParaUser.add(nuevoRol);
                }
                case "capataz" -> {
                    Rol nuevoRol = rolRepository.findByName(RolUser.ROLE_CAPATAZ).orElseThrow(() -> {
                        LOGGER.error(ROLE_NOT_VALID);
                        return new RuntimeException(ROLE_IS_NOT_FOUND);

                    });
                    rolesParaUser.add(nuevoRol);
                }
                default -> {
                }


            }
        });

        if (rolesParaUser.isEmpty()) {
            throw new NoRoleException("Registro de usuario fallido, ningun rol ");
        }

        empleado.setRoles(rolesParaUser);
        empleadoRepository.save(empleado);
        LOGGER.info("Empleado registrado correctamente");

        return empleado;
    }


    @Override
    public Empleado updateProfile(Long id, Empleado datosEmpleado) throws InstanceNotFoundException, DuplicateInstanceException {
        Empleado empleado = permissionChecker.checkEmpleado(id);

        LOGGER.info("Actualizacion de informacion de usuario {}", id);
        if (!empleado.getEmail().equals(datosEmpleado.getEmail())) {
            permissionChecker.checkEmailExists(datosEmpleado.getEmail());
        }


        empleado.setName(datosEmpleado.getName());
        empleado.setEmail(datosEmpleado.getEmail());
        empleado.setLastname(datosEmpleado.getLastname());
        empleado.setDni(datosEmpleado.getDni());
        empleado.setNss(datosEmpleado.getNss());
        empleado.setPhone(datosEmpleado.getPhone());
        empleado.setBirthdate(datosEmpleado.getBirthdate());

        empleadoRepository.save(empleado);
        return empleado;
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) throws InstanceNotFoundException, IncorrectPasswordException {

        Empleado empleado = permissionChecker.checkEmpleado(id);
        LOGGER.info("Intento de cambio de contraseña de usuario: {}", id);

        if (!bCryptPasswordEncoder.matches(oldPassword, empleado.getPassword())) {
            LOGGER.error("Contraseña incorrecta");
            throw new IncorrectPasswordException();
        } else {
            empleado.setPassword(bCryptPasswordEncoder.encode(newPassword));
        }

    }
}
