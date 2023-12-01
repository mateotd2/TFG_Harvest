package com.udc.fic.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udc.fic.harvest.DTOs.ChangePasswordDTO;
import com.udc.fic.harvest.DTOs.NewUserDTO;
import com.udc.fic.harvest.DTOs.SignInRequestDTO;
import com.udc.fic.harvest.DTOs.UpdateUserDTO;
import com.udc.fic.model.Empleado;
import com.udc.fic.security.UserDetailsImpl;
import com.udc.fic.security.jwt.JwtGeneratorInfo;
import com.udc.fic.services.EmpleadoService;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.NoRoleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerTest {

    @Autowired
    EmpleadoService empleadoService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtGeneratorInfo jwtUtils;
    @Autowired
    private MockMvc mockMvc;


    private Empleado crearAdmin() throws DuplicateInstanceException, NoRoleException {

        Empleado empleado = new Empleado(null, "admin", "admin", "123456789Q", "123456789012",
                "123456789", "admin@prueba.com", "admin", "admin", LocalDate.now(), null, "Direccion");

        List<String> roles = new ArrayList<>();
        roles.add("admin");

        return empleadoService.signUp(empleado, roles);
    }

    private Empleado crearCapataz() throws DuplicateInstanceException, NoRoleException {

        Empleado empleado = new Empleado(null, "capataz", "capataz", "123456789Q", "123456789012",
                "123456789", "capataz@prueba.com", "capataz", "capataz", LocalDate.now(), null, "Direccion");

        List<String> roles = new ArrayList<>();
        roles.add("capataz");

        return empleadoService.signUp(empleado, roles);
    }

    private Empleado crearTractorista() throws DuplicateInstanceException, NoRoleException {

        Empleado empleado = new Empleado(null, "tractorista", "tractorista", "123456789Q", "123456789012",
                "123456789", "tractorista@prueba.com", "tractorista", "tractorista", LocalDate.now(), null, "Direccion");

        List<String> roles = new ArrayList<>();
        roles.add("tractorista");

        return empleadoService.signUp(empleado, roles);
    }

    @Test
    void postSignin_Ok() throws Exception {
        Empleado admin = crearAdmin();
        SignInRequestDTO signInRequestDTO = new SignInRequestDTO();
        signInRequestDTO.setPassword("admin");
        signInRequestDTO.setUsername(admin.getUsername());

        ObjectMapper mapper = new ObjectMapper();


        this.mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(signInRequestDTO))).andExpect(status().isOk());
    }

    @Test
    void postSignin_BadCredentials() throws Exception {
//        Empleado admin = crearAdmin();
        SignInRequestDTO signInRequestDTO = new SignInRequestDTO();
        signInRequestDTO.setPassword("bad");
        signInRequestDTO.setUsername("bad");

        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(signInRequestDTO))).andExpect(status().isUnauthorized());
    }


    private NewUserDTO nuevoUser(String username, String email) {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setBirthdate(LocalDate.now());
        newUserDTO.setDni("98765432Q");
        newUserDTO.setName("prueba");
        newUserDTO.setPassword("password");
        newUserDTO.setLastname("lastname");
        newUserDTO.setEmail(email);
        newUserDTO.setNss("098765432123");
        newUserDTO.setPhone("987654321");
        newUserDTO.setUsername(username);
        newUserDTO.setAddress("13 Rua del percebe");
        List<String> roles = new ArrayList<>();
        roles.add("tractorista");
        newUserDTO.setRoles(roles);
        return newUserDTO;
    }

    @Test
    void postSignup_Ok() throws Exception {
        Empleado admin = crearAdmin();
        NewUserDTO newUserDto = nuevoUser("prueba", "prueba@prueba.com");

        ObjectMapper mapper = new ObjectMapper();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);


        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);


        this.mockMvc.perform(post("/api/auth/signup").header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newUserDto))).andExpect(status().isCreated());


    }

    @Test
    void postSignup_Duplicate() throws Exception {
        Empleado admin = crearAdmin();
        NewUserDTO newUserDto = nuevoUser("prueba", "admin@prueba.com");

        ObjectMapper mapper = new ObjectMapper();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);


        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);


        this.mockMvc.perform(post("/api/auth/signup").header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newUserDto))).andExpect(status().isConflict());


    }


    @Test
    void postSignup_BadCredentials() throws Exception {
        Empleado admin = crearAdmin();
        NewUserDTO newUserDTO = nuevoUser("prueba", "prueba@prueba.com");


        ObjectMapper mapper = new ObjectMapper();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);


        this.mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(newUserDTO))).andExpect(status().isUnauthorized());


    }

    @Test
    void changePassword_Ok() throws Exception {
        Empleado admin = crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();

        changePasswordDTO.setNewPassword("nuevaContraseña1234");
        changePasswordDTO.setOldPassword("admin");

        ObjectMapper mapper = new ObjectMapper();


        this.mockMvc.perform(post("/api/auth/{0}/changePassword", admin.getId()).header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(changePasswordDTO))).andExpect(status().isOk());
    }

    @Test
    void changePassword_Unauthorized() throws Exception {
        Empleado admin = crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();

        changePasswordDTO.setNewPassword("nuevaContraseña1234");
        changePasswordDTO.setOldPassword("admin");

        ObjectMapper mapper = new ObjectMapper();


        // ID que no es del admin con id 2
        this.mockMvc.perform(post("/api/auth/1/changePassword", admin.getId()).header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(changePasswordDTO))).andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_notvalidPassword_Unauthorized() throws Exception {
        Empleado admin = crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();

        changePasswordDTO.setNewPassword("nuevaContraseña1234");
        changePasswordDTO.setOldPassword("otraquenoes");

        ObjectMapper mapper = new ObjectMapper();


        // ID que no es del admin con id 2
        this.mockMvc.perform(post("/api/auth/{0}/changePassword", admin.getId()).header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(changePasswordDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_Ok() throws Exception {
        Empleado admin = crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setBirthdate(admin.getBirthdate());
        updateUserDTO.setDni("12345879Q");
        updateUserDTO.setEmail("nuevo@email.com");
        updateUserDTO.setLastname("lastname");
        updateUserDTO.setName(admin.getName());
        updateUserDTO.setNss(admin.getNss());
        updateUserDTO.setPhone("987654321");
        updateUserDTO.setAddress("Rua percebe");

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/auth/{0}/updateUser", admin.getId()).header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateUserDTO))).andExpect(status().isOk());
    }

    @Test
    void updateUserSameEmail_Ok() throws Exception {
        Empleado admin = crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setBirthdate(admin.getBirthdate());
        updateUserDTO.setDni("12345879Q");
        updateUserDTO.setEmail("admin@prueba.com");
        updateUserDTO.setLastname("lastname");
        updateUserDTO.setName(admin.getName());
        updateUserDTO.setNss(admin.getNss());
        updateUserDTO.setPhone("987654321");
        updateUserDTO.setAddress("Percebeiro");

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/auth/{0}/updateUser", admin.getId()).header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateUserDTO))).andExpect(status().isOk());
    }

    @Test
    void updateUser_Unauthorized() throws Exception {
        Empleado admin = crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setBirthdate(admin.getBirthdate());
        updateUserDTO.setDni("12345879Q");
        updateUserDTO.setEmail("nuevo@email.com");
        updateUserDTO.setLastname("lastname");
        updateUserDTO.setName(admin.getName());
        updateUserDTO.setNss(admin.getNss());
        updateUserDTO.setPhone("987654321");
        updateUserDTO.setAddress("Percebeiro");

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        // ID que no es del admin con id 2
        this.mockMvc.perform(post("/api/auth/1/updateUser", admin.getId()).header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateUserDTO))).andExpect(status().isUnauthorized());
    }

    @Test
    void updateUser_Conflict() throws Exception {
        Empleado admin = crearAdmin();
        Empleado tractorista = crearTractorista();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setBirthdate(admin.getBirthdate());
        updateUserDTO.setDni("12345879Q");
        updateUserDTO.setEmail("tractorista@prueba.com"); // EMAIL QUE YA EXISTE
        updateUserDTO.setLastname("lastname");
        updateUserDTO.setName(admin.getName());
        updateUserDTO.setNss(admin.getNss());
        updateUserDTO.setPhone("987654321");
        updateUserDTO.setAddress("Percebeiro");

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/auth/{0}/updateUser", admin.getId()).header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateUserDTO))).andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void signupUserNoRolesAdmin() throws Exception {
        NewUserDTO newUserDTO = nuevoUser("prueba", "prueba@prueba.com");
        newUserDTO.setRoles(new ArrayList<>());

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(newUserDTO))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void signupUserNoRolesCapataz() throws Exception {
        NewUserDTO newUserDTO = nuevoUser("prueba", "prueba@prueba.com");
        newUserDTO.setRoles(new ArrayList<>());

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(newUserDTO))).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void signupUserNotValidRoles() throws Exception {
        NewUserDTO newUserDTO = nuevoUser("prueba", "prueba@prueba.com");
        List<String> invalidRoles = new ArrayList<>();
        invalidRoles.add("invalido");
        newUserDTO.setRoles(invalidRoles);

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(newUserDTO))).andExpect(status().isBadRequest());
    }


}





