package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.ListedTaskDTO;
import com.udc.fic.harvest.DTOs.MessageResponseDTO;
import com.udc.fic.harvest.DTOs.StopTaskDTO;
import com.udc.fic.harvest.DTOs.WorkersDTO;
import com.udc.fic.harvest.controller.CapatazApi;
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

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class CapatazController implements CapatazApi {
    @Autowired
    SourceTargetMapper mapper;

    @Autowired
    CampanhaService campanhaService;


    @Override
    public ResponseEntity<MessageResponseDTO> _startTask(Long id, WorkersDTO workersTractorDTO) throws Exception {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new PermissionException();
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Long userId = (Long) request.getAttribute("userId");

        campanhaService.comenzarTarea(workersTractorDTO.getIdsWorkers(), id, userId);

        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Tarea iniciada");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _stopTask(Long id, StopTaskDTO stopTaskDTO) throws Exception {

        campanhaService.pararTarea(id, stopTaskDTO.getComment(), stopTaskDTO.getPercentaje(), stopTaskDTO.getLoad());
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Tarea iniciada");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<List<ListedTaskDTO>> _endedTasks() {
        List<Tarea> tareasFinalizadas = campanhaService.mostrarTareasFinalizadas();
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

    // LISTADOS DE TAREAS DE FASES
    @Override
    public ResponseEntity<List<ListedTaskDTO>> _pendingTasks() {
        List<Tarea> tareasPendientes = campanhaService.mostrarTareasPendientes();
        List<ListedTaskDTO> pendingTasks = new ArrayList<>();
        tareasPendientes.forEach(t -> {
                    ListedTaskDTO tarea = new ListedTaskDTO();
                    tarea.setIdTarea(t.getId());
                    tarea.setNumeroLinea(t.getLineaCampanha().getLinea().getLineNumber());
                    tarea.setZoneName(t.getLineaCampanha().getZonaCampanha().getZona().getName());
                    tarea.setTipoTrabajo(t.getTipoTrabajo().name());
                    pendingTasks.add(tarea);
                }
        );
        return ResponseEntity.ok().body(pendingTasks);

    }


}
