package com.udc.fic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udc.fic.harvest.DTOs.WorkerDTO;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.services.TrabajadorService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TrabajadoresControllerTest {

    @Autowired
    TrabajadorService trabajadorService;


    @Autowired
    SourceTargetMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private WorkerDTO crearTrabajador() {
        WorkerDTO trabajador = new WorkerDTO();
        trabajador.setDni("74628119Q");
        trabajador.setName("Trabajador1");
        trabajador.setLastname("Trabajador1");
        trabajador.setAddress("Calle");
        trabajador.setNss("183740294712");
        trabajador.setPhone("826730846");
        trabajador.setBirthdate(LocalDate.now());
        return trabajador;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearTrabajadorAdmin() throws Exception {
        WorkerDTO trabajador = crearTrabajador();


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/workers").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(trabajador))).andExpect(status().isCreated());
    }

    @Test
    @WithAnonymousUser
    void crearTrabajadorAnonymous() throws Exception {
        WorkerDTO trabajador = crearTrabajador();


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/workers").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(trabajador))).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void crearTrabajadorSinPermiso() throws Exception {
        WorkerDTO trabajador = crearTrabajador();


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/workers").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(trabajador))).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTrabajadores() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(get("/api/workers")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerTrabajadoresNotAdmin() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(get("/api/workers")
        ).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void obtenerTrabajadoresAnonymous() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(get("/api/workers")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTrabajadorAdmin() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(get("/api/workers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))

                .andExpect(jsonPath("$.name").value("trabajador1"));
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerTrabajadorCapataz() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(get("/api/workers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))

                .andExpect(jsonPath("$.name").value("trabajador1"));
    }

    @Test
    @WithAnonymousUser
    void obtenerTrabajadorAnonymous() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(get("/api/workers/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarTrabajadorAdmin() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        trabajador.setName("TrabajadorActualizado");
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(put("/api/workers/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(trabajador))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void actualizarTrabajadorCapataz() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(put("/api/workers/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(trabajador))).andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void actualizarTrabajadorAnonymous() throws Exception {
        WorkerDTO trabajador = crearTrabajador();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);
        this.mockMvc.perform(put("/api/workers/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(trabajador))).andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void bajaTrabajadorAnonymous() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);
        this.mockMvc.perform(delete("/api/workers/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/api/workers/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }




}
