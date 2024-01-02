package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.MessageResponseDTO;
import com.udc.fic.harvest.DTOs.PendingTask;
import com.udc.fic.harvest.DTOs.StopTaskDTO;
import com.udc.fic.harvest.DTOs.WorkersTractorDTO;
import com.udc.fic.harvest.controller.CampanhaApi;
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
public class CampanhaController implements CampanhaApi {

    @Autowired
    SourceTargetMapper mapper;

    @Autowired
    CampanhaService campanhaService;

    @Override
    public ResponseEntity<MessageResponseDTO> _endCampaign() throws Exception {

        campanhaService.finalizarCampanha();
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Campaña finalizada");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<List<PendingTask>> _pendingTasks() {
        List<Tarea> tareasPendientes = campanhaService.mostrarTareasPendientes();
        List<PendingTask> pendingTasks = new ArrayList<>();
        tareasPendientes.forEach(t -> {
                    PendingTask tarea = new PendingTask();
                    tarea.setIdTarea(t.getId());
                    tarea.setNumeroLinea(t.getLineaCampanha().getLinea().getLineNumber());
                    tarea.setZoneName(t.getLineaCampanha().getZonaCampanha().getZona().getName());
                    tarea.setTipoTrabajo(t.getTipoTrabajo().name());
                    pendingTasks.add(tarea);
                }
        );
        return ResponseEntity.ok().body(pendingTasks);

    }

    @Override
    public ResponseEntity<MessageResponseDTO> _startCampaign() throws Exception {

        campanhaService.comenzarCampanha();
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Campaña iniciada");
        return ResponseEntity.ok().body(message);

    }

    @Override
    public ResponseEntity<MessageResponseDTO> _startHarvesting() throws Exception {
        campanhaService.comenzarRecoleccion();
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Recoleccion iniciada");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _startPruning() throws Exception {
        campanhaService.comenzarPoda();
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Poda iniciada");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _startTask(Long id, WorkersTractorDTO workersTractorDTO) throws Exception {
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

        campanhaService.pararTarea(id, stopTaskDTO.getComment(), stopTaskDTO.getPercentaje());
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Tarea iniciada");
        return ResponseEntity.ok().body(message);
    }

}
