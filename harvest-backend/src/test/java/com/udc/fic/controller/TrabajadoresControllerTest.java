package com.udc.fic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udc.fic.harvest.DTOs.CallDTO;
import com.udc.fic.harvest.DTOs.WorkerDTO;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TrabajadoresControllerTest {


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

        this.mockMvc.perform(get("/api/workers")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerTrabajadoresNotAdmin() throws Exception {

        this.mockMvc.perform(get("/api/workers")
        ).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void obtenerTrabajadoresAnonymous() throws Exception {

        this.mockMvc.perform(get("/api/workers")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTrabajadorAdmin() throws Exception {

        this.mockMvc.perform(get("/api/workers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))

                .andExpect(jsonPath("$.name").value("trabajador1"));
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerTrabajadorCapataz() throws Exception {

        this.mockMvc.perform(get("/api/workers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))

                .andExpect(jsonPath("$.name").value("trabajador1"));
    }

    @Test
    @WithAnonymousUser
    void obtenerTrabajadorAnonymous() throws Exception {

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
        this.mockMvc.perform(delete("/api/workers/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/api/workers/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerAsistenciasAdmin() throws Exception {
        this.mockMvc.perform(get("/api/callroll")).andExpect(status().isOk()).andExpect(content().contentType("application/json"))

                .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerAsistenciasCapataz() throws Exception {
        this.mockMvc.perform(get("/api/callroll")).andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void obtenerAsistenciasAnonymous() throws Exception {
        this.mockMvc.perform(get("/api/callroll")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void callRollAdmin() throws Exception {
        List<CallDTO> calls = new ArrayList<>();
        CallDTO call1 = new CallDTO();
        call1.setId(1L);
        call1.setCheckin(LocalTime.of(8, 0, 0));
        call1.setCheckout(LocalTime.of(16, 0, 0));
        calls.add(call1);

        CallDTO call2 = new CallDTO();
        call2.setId(3L);
        call2.setCheckin(LocalTime.of(8, 0, 0));
        call2.setCheckout(LocalTime.of(16, 0, 0));
        calls.add(call2);

        CallDTO call3 = new CallDTO();
        call3.setId(5L);
        call3.setCheckin(LocalTime.of(8, 0, 0));
        call3.setCheckout(LocalTime.of(16, 0, 0));
        calls.add(call3);

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(put("/api/callroll").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(calls))).andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void callRoll404() throws Exception {
        List<CallDTO> calls = new ArrayList<>();
        CallDTO call1 = new CallDTO();
        call1.setId(1L);
        call1.setCheckin(LocalTime.of(8, 0, 0));
        call1.setCheckout(LocalTime.of(16, 0, 0));
        calls.add(call1);

        CallDTO call2 = new CallDTO();
        call2.setId(3L);
        call2.setCheckin(LocalTime.of(8, 0, 0));
        call2.setCheckout(LocalTime.of(16, 0, 0));
        calls.add(call2);

        CallDTO call3 = new CallDTO();
        call3.setId(10L); // NO EXISTE NINGUNA DISPONIBLIDAD CON ID 10
        call3.setCheckin(LocalTime.of(8, 0, 0));
        call3.setCheckout(LocalTime.of(16, 0, 0));
        calls.add(call3);

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(put("/api/callroll").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(calls))).andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser(roles = "CAPATAZ")
    void callRollCapataz() throws Exception {
        List<CallDTO> calls = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(put("/api/callroll").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(calls))).andExpect(status().isForbidden());
    }
    @Test
    @WithAnonymousUser
    void callRollAnonymous() throws Exception {
        List<CallDTO> calls = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(put("/api/callroll").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(calls))).andExpect(status().isUnauthorized());
    }




}
