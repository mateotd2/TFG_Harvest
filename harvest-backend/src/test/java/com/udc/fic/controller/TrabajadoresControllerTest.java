package com.udc.fic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udc.fic.harvest.DTOs.CalendarDTO;
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

                .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$.length()").value(4));
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
        call1.setAttendance(true);
        calls.add(call1);

        CallDTO call2 = new CallDTO();
        call2.setId(3L);
        call2.setCheckin(LocalTime.of(8, 0, 0));
        call2.setCheckout(LocalTime.of(16, 0, 0));
        call2.setAttendance(true);
        calls.add(call2);

        CallDTO call3 = new CallDTO();
        call3.setId(5L);
        call3.setCheckin(LocalTime.of(8, 0, 0));
        call3.setCheckout(LocalTime.of(16, 0, 0));
        call3.setAttendance(true);
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
        call1.setAttendance(true);
        calls.add(call1);

        CallDTO call2 = new CallDTO();
        call2.setId(3L);
        call2.setCheckin(LocalTime.of(8, 0, 0));
        call2.setCheckout(LocalTime.of(16, 0, 0));
        call2.setAttendance(true);
        calls.add(call2);

        CallDTO call3 = new CallDTO();
        call3.setId(100L); // NO EXISTE NINGUNA DISPONIBLIDAD CON ID 100
        call3.setCheckin(LocalTime.of(8, 0, 0));
        call3.setCheckout(LocalTime.of(16, 0, 0));
        call3.setAttendance(true);
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarCalendario() throws Exception {
        List<CalendarDTO> calendarioDtos = new ArrayList<>();
        CalendarDTO dia1 = new CalendarDTO();
        dia1.setCheckin(LocalTime.of(8, 0, 0));
        dia1.setCheckout(LocalTime.of(16, 0, 0));
        dia1.setDay(LocalDate.now());
        dia1.setAttendance(false);
        dia1.setId(1L);
        calendarioDtos.add(dia1);
        CalendarDTO dia2 = new CalendarDTO();
        dia2.setCheckin(LocalTime.of(8, 0, 0));
        dia2.setCheckout(LocalTime.of(16, 0, 0));
        dia2.setDay(LocalDate.now().plusDays(1L));
        dia2.setAttendance(false);
        dia2.setId(2L);
        calendarioDtos.add(dia2);
        CalendarDTO dia3 = new CalendarDTO();
        dia3.setCheckin(LocalTime.of(8, 0, 0));
        dia3.setCheckout(LocalTime.of(16, 0, 0));
        dia3.setDay(LocalDate.of(2023, 10, 25));
        dia3.setAttendance(true);
        dia3.setId(3L);
        calendarioDtos.add(dia3);


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(put("/api/workers/1/calendar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(calendarioDtos))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerCalendarioAdmin() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(get("/api/workers/1/calendar")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerCalendarioCapataz() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(get("/api/workers/1/calendar")).andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminarDiaDeTrabajo() throws Exception {


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(delete("/api/workers/1/calendar/2")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminarDiaDeTrabajoInvalid() throws Exception {


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(delete("/api/workers/1/calendar/1")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void anadirDiaDeTrabajo() throws Exception {
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setDay(LocalDate.now().plusDays(1));
        calendarDTO.setCheckin(LocalTime.of(8, 0, 0));
        calendarDTO.setCheckout(LocalTime.of(14, 0, 0));
        calendarDTO.setAttendance(false);


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/workers/1/calendar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(calendarDTO))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void anadirDiaDeTrabajoInvalidDate() throws Exception {
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setDay(LocalDate.now());
        calendarDTO.setCheckin(LocalTime.of(8, 0, 0));
        calendarDTO.setCheckout(LocalTime.of(14, 0, 0));
        calendarDTO.setAttendance(false);


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/workers/1/calendar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(calendarDTO))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void anadirDiaDeTrabajoInvalidChecks() throws Exception {
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setDay(LocalDate.now());
        calendarDTO.setCheckin(LocalTime.of(14, 0, 0));
        calendarDTO.setCheckout(LocalTime.of(8, 0, 0));
        calendarDTO.setAttendance(false);


        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/workers/1/calendar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(calendarDTO))).andExpect(status().isBadRequest());
    }


}
