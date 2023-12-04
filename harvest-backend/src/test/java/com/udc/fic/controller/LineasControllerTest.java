package com.udc.fic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udc.fic.harvest.DTOs.ZoneDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
        zona.setName("Nuevo nombre de zona Test");
        zona.setDescription("Nueva Descripcion de zona Test");
        zona.setFormation(ZoneDTO.FormationEnum.EMPARRADO);
        zona.setReference("12345678901234567891"); // Conflicto de referencia en la zona 2
        ObjectMapper mapper = new ObjectMapper();

        // Actualizamos la primera zona
        this.mockMvc.perform(put("/api/zones/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(zona))).andExpect(status().isConflict());
    }


}
