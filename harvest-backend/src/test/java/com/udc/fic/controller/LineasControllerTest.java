package com.udc.fic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udc.fic.harvest.DTOs.LineDTO;
import com.udc.fic.harvest.DTOs.ZoneDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LineasControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private ZoneDTO crearZona() {
        ZoneDTO zone = new ZoneDTO();
        zone.setDescription("Descripcion test");
        zone.setName("Zone Test");
        zone.setFormation(ZoneDTO.FormationEnum.EMPARRADO);
        zone.setSurface(300);
        zone.setReference("09876543210987654321");
        return zone;
    }

    private LineDTO crearLinea() {
        LineDTO linea = new LineDTO();
        linea.setHarvestEnabled(true);
        linea.setLineNumber(6);
        linea.setIdTypeVid(1L);
        linea.setPlantingDate(LocalDate.now().minusYears(10));
        return linea;

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerZonasAdmin() throws Exception {

        this.mockMvc.perform(get("/api/zones")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerZonasCapataz() throws Exception {

        this.mockMvc.perform(get("/api/zones")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerZonaAdmin() throws Exception {

        this.mockMvc.perform(get("/api/zones/2")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerZonaNotFound() throws Exception {

        this.mockMvc.perform(get("/api/zones/10")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearZonaAdmin() throws Exception {
        ZoneDTO zona = crearZona();
        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(zona))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void crearZonaCapataz() throws Exception {
        ZoneDTO zona = crearZona();
        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(zona))).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearZonaDuplicate() throws Exception {
        ZoneDTO zona = crearZona();
        zona.setName("Zona 1");
        zona.setReference("12345678901234567890"); // Esta ya existe
        ObjectMapper mapper = new ObjectMapper();

        this.mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(zona))).andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarZonaAdmin() throws Exception {
        ZoneDTO zona = new ZoneDTO();
        zona.setReference("12345678901234567890");
        zona.setSurface(300);
        zona.setName("Nuevo nombre de zona Test");
        zona.setDescription("Nueva Descripcion de zona Test");
        zona.setFormation(ZoneDTO.FormationEnum.EMPARRADO);
        zona.setReference("12345678901234567890"); // Misma referencia
        ObjectMapper mapper = new ObjectMapper();

        // Actualizamos la primera zona
        this.mockMvc.perform(put("/api/zones/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(zona))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarZonaDistintaReferencia() throws Exception {
        ZoneDTO zona = new ZoneDTO();
        zona.setReference("12345678901234567890");
        zona.setSurface(300);
        zona.setName("Nuevo nombre de zona Test");
        zona.setDescription("Nueva Descripcion de zona Test");
        zona.setFormation(ZoneDTO.FormationEnum.EMPARRADO);
        zona.setReference("1234567890123456789A"); // Misma referencia
        ObjectMapper mapper = new ObjectMapper();

        // Actualizamos la primera zona
        this.mockMvc.perform(put("/api/zones/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(zona))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarZonaConflicto() throws Exception {
        ZoneDTO zona = new ZoneDTO();
        zona.setReference("12345678901234567890");
        zona.setSurface(300);
        zona.setName("Zona 2");
        zona.setDescription("Nueva Descripcion de zona Test");
        zona.setFormation(ZoneDTO.FormationEnum.EMPARRADO);
        zona.setReference("12345678901234567891"); // Conflicto de referencia en la zona 2
        ObjectMapper mapper = new ObjectMapper();

        // Actualizamos la primera zona
        this.mockMvc.perform(put("/api/zones/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(zona))).andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerLineasDeZonaAdmin() throws Exception {

        this.mockMvc.perform(get("/api/zones/1/lines")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerLineasDeZonaCapataz() throws Exception {

        this.mockMvc.perform(get("/api/zones/1/lines")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerLineasDeZonaNotFound() throws Exception {

        this.mockMvc.perform(get("/api/zones/6/lines")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerVidsDeZonaAdmin() throws Exception {

        this.mockMvc.perform(get("/api/tipeVids")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerVidsDeZonaCapataz() throws Exception {

        this.mockMvc.perform(get("/api/tipeVids")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerDetalleLineaAdmin() throws Exception {

        this.mockMvc.perform(get("/api/lines/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void obtenerDetalleLineaDeZonaCapataz() throws Exception {

        this.mockMvc.perform(get("/api/lines/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerDetalleLineaNotFound() throws Exception {

        this.mockMvc.perform(get("/api/lines/10")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deshabilitarLineaAdmin() throws Exception {

        this.mockMvc.perform(put("/api/lines/1/disable")).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void deshabilitarLineaCapataz() throws Exception {

        this.mockMvc.perform(put("/api/lines/1/disable")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void habilitarLineaAdmin() throws Exception {

        this.mockMvc.perform(put("/api/lines/3/enable")).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void habilitarLineaCapataz() throws Exception {

        this.mockMvc.perform(put("/api/lines/3/enable")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deshabilitarLineaNotFound() throws Exception {

        this.mockMvc.perform(get("/api/lines/10")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void agregarLineaAdmin() throws Exception {
        LineDTO linea = crearLinea();

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/zones/1/lines").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(linea))).andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void agregarLineaNotFound() throws Exception {
        LineDTO linea = crearLinea();

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/zones/33/lines").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(linea))).andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void agregarLineaCapataz() throws Exception {
        LineDTO linea = crearLinea();

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(post("/api/zones/1/lines").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(linea))).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarLineaAdmin() throws Exception {
        LineDTO linea = crearLinea();
        linea.setId(1L);
        linea.setIdTypeVid(2L);

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(put("/api/lines/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(linea))).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void actualizarLineaCapataz() throws Exception {
        LineDTO linea = crearLinea();
        linea.setId(1L);
        linea.setIdTypeVid(2L);

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(put("/api/lines/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(linea))).andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarLineaNotFound() throws Exception {
        LineDTO linea = crearLinea();
        linea.setId(1L);
        linea.setIdTypeVid(2L);

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.registerModule(new JavaTimeModule()).setDateFormat(df);

        this.mockMvc.perform(put("/api/lines/33").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(linea))).andExpect(status().isNotFound());

    }


}
