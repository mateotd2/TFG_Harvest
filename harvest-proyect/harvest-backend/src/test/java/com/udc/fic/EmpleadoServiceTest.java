package com.udc.fic;

import com.udc.fic.model.Empleado;
import com.udc.fic.model.Rol;
import com.udc.fic.model.RolUser;
import com.udc.fic.repository.EmpleadoRepository;
import com.udc.fic.repository.RolRepository;
import com.udc.fic.services.EmpleadoServiceImpl;
import com.udc.fic.services.PermissionChecker;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EmpleadoServiceTest {

    @InjectMocks
    EmpleadoServiceImpl empleadoService;


    @Mock
    EmpleadoRepository empleadoRepository;

    @Mock
    RolRepository rolRepository;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Mock
    private PermissionChecker permissionChecker;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }


    //    @Test
    @ParameterizedTest
    @ValueSource(strings = {"admin", "tractorista", "capataz"})
    public void signUpTest(String rolParametrico) throws DuplicateInstanceException {
        Empleado empleado = new Empleado();
        empleado.setName("Mateo");
        empleado.setLastname("tilves");
        LocalDate birthdate = LocalDate.now();
        empleado.setBirthdate(birthdate);
        empleado.setNss("123456789012");
        empleado.setDni("12345678Q");
        empleado.setUsername("mateo");
        empleado.setPassword("password");
        empleado.setEmail("mateo@mateo.com");

        Rol rolMock = new Rol();
        rolMock.setId(1L);
        rolMock.setName(RolUser.ROLE_ADMIN);


        when(empleadoRepository.existsByUsername(anyString())).thenReturn(false);
        when(empleadoRepository.existsByEmail("mateo@mateo.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(rolRepository.findByName(any(RolUser.class))).thenReturn(Optional.of(rolMock));

        Set<Rol> roles = new HashSet<>();
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setName(RolUser.ROLE_ADMIN);
        roles.add(rol);
        empleado.setRoles(roles);

        List<String> rolesString = new ArrayList<>();
        rolesString.add(rolParametrico);

        Empleado empleadoObtained = empleadoService.signUp(empleado, rolesString);
        assertEquals(empleado, empleadoObtained);

    }

    @Test
    public void updateProfileTest() throws InstanceNotFoundException, DuplicateInstanceException {
        Empleado empleado = new Empleado();
        empleado.setName("Mateo");
        empleado.setLastname("tilves");
        LocalDate birthdate = LocalDate.now();
        empleado.setBirthdate(birthdate);
        empleado.setNss("123456789012");
        empleado.setDni("12345678Q");
        empleado.setUsername("mateo");
        empleado.setPassword("password");
        empleado.setEmail("mateo@mateo.com");

        when(permissionChecker.checkEmpleado(1l)).thenReturn(empleado);
        doNothing().when(permissionChecker).checkEmailExists("mateo@mateo.com");

        when(empleadoRepository.save(empleado)).thenReturn(empleado);

        assertEquals(empleado, empleadoService.updateProfile(1l, empleado));
    }


}
