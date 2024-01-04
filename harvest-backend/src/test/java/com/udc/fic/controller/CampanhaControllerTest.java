package com.udc.fic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.udc.fic.harvest.DTOs.StopTaskDTO;
import com.udc.fic.harvest.DTOs.WorkersTractorDTO;
import com.udc.fic.model.Empleado;
import com.udc.fic.security.UserDetailsImpl;
import com.udc.fic.security.jwt.JwtGeneratorInfo;
import com.udc.fic.services.EmpleadoService;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.NoRoleException;
import jakarta.transaction.Transactional;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CampanhaControllerTest {


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
    void pasarFaseSinEmpezarCampanha() throws Exception {
        this.mockMvc.perform(post("/api/pruning")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void pasarFaseSinEmpezarCampanha2() throws Exception {
        this.mockMvc.perform(post("/api/startharvest")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void pasarFaseAdmin() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());
        this.mockMvc.perform(post("/api/pruning")).andExpect(status().isOk());
        this.mockMvc.perform(post("/api/startharvest")).andExpect(status().isOk());
        this.mockMvc.perform(post("/api/endCampaign")).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTareas() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());

        this.mockMvc.perform(get("/api/pendingTasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].idTarea").exists())
                .andExpect(jsonPath("$[0].zoneName").exists())
                .andExpect(jsonPath("$[0].numeroLinea").exists())
                .andExpect(jsonPath("$[0].tipoTrabajo").exists());

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

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTareaEspecifica() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());
        ResultActions result = this.mockMvc.perform(get("/api/pendingTasks"));

        String jsonId = result.andReturn().getResponse().getContentAsString();

        Integer respuestaId = JsonPath.read(jsonId, "$[0].idTarea");
        this.mockMvc.perform(get("/api/task/" + respuestaId)).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTareaSinEmpezar() throws Exception {
        this.mockMvc.perform(post("/api/startCampaign")).andExpect(status().isOk());
        ResultActions result = this.mockMvc.perform(get("/api/pendingTasks"));

        String jsonId = result.andReturn().getResponse().getContentAsString();

        Integer respuestaId = JsonPath.read(jsonId, "$[0].idTarea");
        this.mockMvc.perform(get("/api/task/" + respuestaId)).andExpect(status().isOk());

    }

    @Test
    void dellateTarea() throws Exception {

        // Preparacion del administrador para la prueba
        crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Mapper
        ObjectMapper mapper = new ObjectMapper();

        // Inicio de campaña
        this.mockMvc.perform(post("/api/startCampaign"));

        // Obtengo un id de tarea para la prueba
        ResultActions result = this.mockMvc.perform(get("/api/pendingTasks"));

        String jsonId = result.andReturn().getResponse().getContentAsString();

        Integer respuestaId = JsonPath.read(jsonId, "$[0].idTarea");

        // Preparo datos para empezar la tarea
        WorkersTractorDTO workersTractorDTO = new WorkersTractorDTO();
        List<Long> idsTrabajadores = new ArrayList<>();
        idsTrabajadores.add(1L);
        idsTrabajadores.add(2L);
        workersTractorDTO.setIdsWorkers(idsTrabajadores);
        workersTractorDTO.setIdTractor(1L);

        // Inicio tarea
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/startTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(workersTractorDTO)));

        // Preparo datos para finalizar tarea
        StopTaskDTO stopTaskDTO = new StopTaskDTO();
        stopTaskDTO.setPercentaje(50);
        stopTaskDTO.setComment("Comentario nuevo");

        // Finalizo tarea
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/stopTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(stopTaskDTO)));

        // Obtengo tarea
        this.mockMvc.perform(get("/api/task/" + respuestaId)).andExpect(status().isOk()).andExpect(jsonPath("$.horaInicio").exists());

    }

    @Test
    void obtenerTareasFinalizadas() throws Exception {

        // Preparacion del administrador para la prueba
        crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Mapper
        ObjectMapper mapper = new ObjectMapper();

        // Inicio de campaña
        this.mockMvc.perform(post("/api/startCampaign"));

        // Obtengo un id de tarea para la prueba
        ResultActions result = this.mockMvc.perform(get("/api/pendingTasks"));
        String jsonId = result.andReturn().getResponse().getContentAsString();
        Integer respuestaId = JsonPath.read(jsonId, "$[0].idTarea");

        // Preparo datos para empezar la tarea
        WorkersTractorDTO workersTractorDTO = new WorkersTractorDTO();
        List<Long> idsTrabajadores = new ArrayList<>();
        idsTrabajadores.add(1L);
        idsTrabajadores.add(2L);
        workersTractorDTO.setIdsWorkers(idsTrabajadores);
        workersTractorDTO.setIdTractor(1L);

        // Inicio tarea
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/startTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(workersTractorDTO)));

        // Preparo datos para finalizar tarea
        StopTaskDTO stopTaskDTO = new StopTaskDTO();
        stopTaskDTO.setPercentaje(50);
        stopTaskDTO.setComment("Comentario nuevo");

        // Finalizo tarea
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/stopTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(stopTaskDTO)));

        // Obtengo tareas
        this.mockMvc.perform(get("/api/endedTasks")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());

    }

    @Test
    void iniciarTareaYaIniciada() throws Exception {

        // Preparacion del administrador para la prueba
        crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Mapper
        ObjectMapper mapper = new ObjectMapper();

        // Inicio de campaña
        this.mockMvc.perform(post("/api/startCampaign"));

        // Obtengo un id de tarea para la prueba
        ResultActions result = this.mockMvc.perform(get("/api/pendingTasks"));
        String jsonId = result.andReturn().getResponse().getContentAsString();
        Integer respuestaId = JsonPath.read(jsonId, "$[0].idTarea");

        // Preparo datos para empezar la tarea
        WorkersTractorDTO workersTractorDTO = new WorkersTractorDTO();
        List<Long> idsTrabajadores = new ArrayList<>();
        idsTrabajadores.add(1L);
        idsTrabajadores.add(2L);
        workersTractorDTO.setIdsWorkers(idsTrabajadores);
        workersTractorDTO.setIdTractor(1L);

        // Inicio tarea
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/startTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(workersTractorDTO)));
        // Excepcion
        WorkersTractorDTO workersTractorDTO2 = new WorkersTractorDTO();
        List<Long> idsTrabajadores2 = new ArrayList<>();
        idsTrabajadores2.add(3L);
        idsTrabajadores2.add(4L);
        workersTractorDTO2.setIdsWorkers(idsTrabajadores2);
        workersTractorDTO2.setIdTractor(1L);

        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/startTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(workersTractorDTO2))).andExpect(status().isBadRequest());
    }

    @Test
    void finalizarTareaYaFinalizada() throws Exception {

        // Preparacion del administrador para la prueba
        crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Mapper
        ObjectMapper mapper = new ObjectMapper();

        // Inicio de campaña
        this.mockMvc.perform(post("/api/startCampaign"));

        // Obtengo un id de tarea para la prueba
        ResultActions result = this.mockMvc.perform(get("/api/pendingTasks"));
        String jsonId = result.andReturn().getResponse().getContentAsString();
        Integer respuestaId = JsonPath.read(jsonId, "$[0].idTarea");

        // Preparo datos para empezar la tarea
        WorkersTractorDTO workersTractorDTO = new WorkersTractorDTO();
        List<Long> idsTrabajadores = new ArrayList<>();
        idsTrabajadores.add(1L);
        idsTrabajadores.add(2L);
        workersTractorDTO.setIdsWorkers(idsTrabajadores);
        workersTractorDTO.setIdTractor(1L);

        // Inicio tarea
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/startTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(workersTractorDTO)));
        // Preparo datos para finalizar tarea
        StopTaskDTO stopTaskDTO = new StopTaskDTO();
        stopTaskDTO.setPercentaje(50);
        stopTaskDTO.setComment("Comentario nuevo");

        // Finalizo tarea
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/stopTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(stopTaskDTO)));
        // Excepcion

        // Preparo datos para finalizar tarea
//        StopTaskDTO stopTaskDTO = new StopTaskDTO();
//        stopTaskDTO.setPercentaje(50);
//        stopTaskDTO.setComment("Comentario nuevo");

        // Finalizo tarea
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/stopTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(stopTaskDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void finalizarTareaNoEmpezada() throws Exception {

        // Preparacion del administrador para la prueba
        crearAdmin();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Mapper
        ObjectMapper mapper = new ObjectMapper();

        // Inicio de campaña
        this.mockMvc.perform(post("/api/startCampaign"));

        // Obtengo un id de tarea para la prueba
        ResultActions result = this.mockMvc.perform(get("/api/pendingTasks"));
        String jsonId = result.andReturn().getResponse().getContentAsString();
        Integer respuestaId = JsonPath.read(jsonId, "$[0].idTarea");

        StopTaskDTO stopTaskDTO = new StopTaskDTO();
        stopTaskDTO.setPercentaje(50);
        stopTaskDTO.setComment("Comentario nuevo");

        // Finalizo tarea que no empezo aun, por lo que saltara excepcion
        this.mockMvc.perform(post("/api/pendingTasks/" + respuestaId + "/stopTask").header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(stopTaskDTO))).andExpect(status().isBadRequest());
    }


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
