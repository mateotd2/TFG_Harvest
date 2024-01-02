package com.udc.fic.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CampanhaControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(roles = "ADMIN")
    void empezarCampanha() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CAPATAZ")
    void empezarCampanhaForbidden() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void pasarFaseSinEmpezarCampaña() throws Exception {
        this.mockMvc.perform(post("/api/pruning")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void pasarFaseSinEmpezarCampaña2() throws Exception {
        this.mockMvc.perform(post("/api/startharvest")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTareas() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());

        this.mockMvc.perform(get("/api/pendingTasks")).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTareasCapataz() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());

        this.mockMvc.perform(get("/api/pendingTasks").with(request -> {
            request.addUserRole("CAPATAZ");
            return request;
        })).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTareasTractorista() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());

        this.mockMvc.perform(get("/api/pendingTasks").with(request -> {
            request.addUserRole("TRACTORISTA");
            return request;
        })).andExpect(status().isOk());

    }

//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void comenzarTareaCapataz() throws Exception {
//        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());
//
//
//        WorkersTractorDTO workersTractorDTO = new WorkersTractorDTO();
//        List<Long> idsTrabajadores = new ArrayList<>();
//        idsTrabajadores.add(1l);
//        idsTrabajadores.add(2l);
//        workersTractorDTO.setIdsWorkers(idsTrabajadores);
//        workersTractorDTO.setIdTractor(1l);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//
//        this.mockMvc.perform(post("/api/pendingTasks/1/startTask")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(request -> {
//                            request.addUserRole("CAPATAZ");
//                            return request;
//                        })
//                        .content(mapper.writeValueAsBytes(workersTractorDTO)))
//                .andExpect(status().isOk());
//
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void comenzarTareaTractorista() throws Exception {
//        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());
//
//
//        WorkersTractorDTO workersTractorDTO = new WorkersTractorDTO();
//        List<Long> idsTrabajadores = new ArrayList<>();
//        idsTrabajadores.add(1l);
//        idsTrabajadores.add(2l);
//        workersTractorDTO.setIdsWorkers(idsTrabajadores);
//        workersTractorDTO.setIdTractor(null);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//
//        this.mockMvc.perform(post("/api/pendingTasks/1/startTask")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(request -> {
//                            request.addUserRole("CAPATAZ");
//                            return request;
//                        })
//                        .content(mapper.writeValueAsBytes(workersTractorDTO)))
//                .andExpect(status().isOk());
//
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void fianalizarTareaTractorista() throws Exception {
//        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());
//
//
//        WorkersTractorDTO workersTractorDTO = new WorkersTractorDTO();
//        List<Long> idsTrabajadores = new ArrayList<>();
//        idsTrabajadores.add(1l);
//        idsTrabajadores.add(2l);
//        workersTractorDTO.setIdsWorkers(idsTrabajadores);
//        workersTractorDTO.setIdTractor(null);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//
//        this.mockMvc.perform(get("/api/pendingTasks")
//                .contentType(MediaType.APPLICATION_JSON)
//                .with(request -> {
//                    request.addUserRole("TRACTORISTA");
//                    return request;
//                })
//                .content(mapper.writeValueAsBytes(workersTractorDTO)))
//                .andExpect(status().isOk());
//
//        StopTaskDTO stopTaskDTO = new StopTaskDTO();
//        stopTaskDTO.setComment("Comentario");
//        stopTaskDTO.setPercentaje(50);
//
//        this.mockMvc.perform(post("/api/pendingTasks/1/startTask")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(request -> {
//                            request.addUserRole("TRACTORISTA");
//                            return request;
//                        })
//                        .content(mapper.writeValueAsBytes(stopTaskDTO)))
//                .andExpect(status().isOk());
//
//
//    }


}
