package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.*;
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

import java.time.LocalTime;
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

    @Override
    public ResponseEntity<List<ListedTaskDTO>> _inProgressTasks() throws Exception {
        List<Tarea> tareasEnProgreso = campanhaService.mostrarTareasSinFinalizar();
        List<ListedTaskDTO> tareasInProgress = new ArrayList<>();
        tareasEnProgreso.forEach(t -> {
                ListedTaskDTO tarea = new ListedTaskDTO();
                tarea.setIdTarea(t.getId());
                tarea.setNumeroLinea(t.getLineaCampanha().getLinea().getLineNumber());
                tarea.setZoneName(t.getLineaCampanha().getZonaCampanha().getZona().getName());
                tarea.setTipoTrabajo(t.getTipoTrabajo().name());
                tareasInProgress.add(tarea);
            }
        );
        return ResponseEntity.ok().body(tareasInProgress);
    }

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

    @Override
    public ResponseEntity<TaskDTO> _taskDetails(Long id) throws Exception {
        Tarea tarea = campanhaService.mostrarDetallesTarea(id);
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setCommentarios(tarea.getComentarios());
        taskDTO.setNumeroLinea(tarea.getLineaCampanha().getLinea().getLineNumber());
        taskDTO.setIdTarea(tarea.getId());
        taskDTO.setEstado(TaskDTO.EstadoEnum.NO_INICIADA);
        if (tarea.getHoraEntrada() != null) {
            taskDTO.setHoraInicio(LocalTime.from(tarea.getHoraEntrada()));
            taskDTO.setEstado(TaskDTO.EstadoEnum.INICIADA);
        }
        if (tarea.getHoraSalida() != null) {
            taskDTO.setHoraInicio(LocalTime.from(tarea.getHoraEntrada()));
            taskDTO.setHoraFinalizacion(LocalTime.from(tarea.getHoraSalida()));
            taskDTO.setEstado(TaskDTO.EstadoEnum.FINALIZADA);
        }

        taskDTO.setZoneName(tarea.getLineaCampanha().getZonaCampanha().getZona().getName());
        taskDTO.setTipoTarea(tarea.getTipoTrabajo().toString());


        if (tarea.getTrabajadores() != null) {
            List<WorkerDTO> workerDTOS = new ArrayList<>();
            tarea.getTrabajadores().forEach(trabajador ->
                    workerDTOS.add(mapper.toWorker(trabajador))
            );
            taskDTO.setWorkers(workerDTOS);
        }
        return ResponseEntity.ok().body(taskDTO);
    }

}
