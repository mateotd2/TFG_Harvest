package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.*;
import com.udc.fic.harvest.controller.TractoristaApi;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.model.Tarea;
import com.udc.fic.services.CampanhaService;
import com.udc.fic.services.exceptions.PermissionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class TractoristaController implements TractoristaApi {



    @Autowired
    SourceTargetMapper mapper;

    @Autowired
    CampanhaService campanhaService;

    // TAREAS DE FASE DE CARGA
    @Override
    public ResponseEntity<List<ListedTaskDTO>> _loadTasks() {
        List<Tarea> tareasFinalizadas = campanhaService.mostrarTareasPendientesDeCarga();
        List<ListedTaskDTO> endedTasks = new ArrayList<>();
        tareasFinalizadas.forEach(t -> {
                    ListedTaskDTO tarea = new ListedTaskDTO();
                    tarea.setIdTarea(t.getId());
                    tarea.setNumeroLinea(t.getLineaCampanha().getLinea().getLineNumber());
                    tarea.setZoneName(t.getLineaCampanha().getZonaCampanha().getZona().getName());
                    tarea.setTipoTrabajo(t.getTipoTrabajo().name());
                    endedTasks.add(tarea);
                }
        );
        return ResponseEntity.ok().body(endedTasks);
    }


    @Override
    public ResponseEntity<MessageResponseDTO> _startLoadTasks(StartLoadTasksDTO startLoadTasksDTO) throws Exception {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new PermissionException();
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        campanhaService.comenzarTareasCarga(startLoadTasksDTO.getIdLoadTasks(), userId, startLoadTasksDTO.getIdTractor(), startLoadTasksDTO.getIdsWorkers());

        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Tareas iniciadas");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<List<ListedTaskDTO>> _inProgressLoadTasks() {
        List<Tarea> tareasFinalizadas = campanhaService.mostrarTareasSinFinalizarDeCarga();
        List<ListedTaskDTO> endedTasks = new ArrayList<>();
        tareasFinalizadas.forEach(t -> {
                    ListedTaskDTO tarea = new ListedTaskDTO();
                    tarea.setIdTarea(t.getId());
                    tarea.setNumeroLinea(t.getLineaCampanha().getLinea().getLineNumber());
                    tarea.setZoneName(t.getLineaCampanha().getZonaCampanha().getZona().getName());
                    tarea.setTipoTrabajo(t.getTipoTrabajo().name());
                    tarea.setIdTractor(t.getTractor().getId());

                    endedTasks.add(tarea);
                }
        );
        return ResponseEntity.ok().body(endedTasks);
    }

    @Override
    public ResponseEntity<List<ListedTaskDTO>> _endedLoadTasks() {
        List<Tarea> tareasFinalizadas = campanhaService.mostrarTareasFinalizadasDeCarga();
        List<ListedTaskDTO> endedTasks = new ArrayList<>();
        tareasFinalizadas.forEach(t -> {
                    ListedTaskDTO tarea = new ListedTaskDTO();
                    tarea.setIdTarea(t.getId());
                    tarea.setNumeroLinea(t.getLineaCampanha().getLinea().getLineNumber());
                    tarea.setZoneName(t.getLineaCampanha().getZonaCampanha().getZona().getName());
                    tarea.setTipoTrabajo(t.getTipoTrabajo().name());
                    endedTasks.add(tarea);
                }
        );
        return ResponseEntity.ok().body(endedTasks);
    }

    @Override
    public ResponseEntity<List<TractorDTO>> _getAvailableTractors() throws Exception {

        return ResponseEntity.ok().body(campanhaService.tractoresDisponibles().stream().map(tractor -> mapper.toTractorDTO(tractor)).toList());
    }

    @Override
    public ResponseEntity<Boolean> _checkNewLoadTasks() throws Exception {
        ResponseEntity<Boolean> ok = ResponseEntity.ok(campanhaService.notificacionTareasCarga());
        return ok;
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _endLoadTasks(EndLoadTasksDTO endLoadTasksDTO) throws Exception {
        campanhaService.pararTareasCarga(endLoadTasksDTO.getIdLoadTasks(), endLoadTasksDTO.getComment());
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Tareas FINALIZADAS");
        return ResponseEntity.ok().body(message);
    }
}
