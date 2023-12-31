package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.*;
import com.udc.fic.harvest.controller.TrabajadoresApi;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.model.Disponibilidad;
import com.udc.fic.model.Trabajador;
import com.udc.fic.services.TrabajadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.management.InstanceNotFoundException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class TrabajadorController implements TrabajadoresApi {

    @Autowired
    TrabajadorService trabajadorService;


    @Autowired
    SourceTargetMapper mapper;


    @Override
    public ResponseEntity<MessageResponseDTO> _addDayOfWork(Long id, CalendarDTO calendarDTO) throws Exception {
        trabajadorService.altaDiaCalendario(id, mapper.toDisponibilidad(calendarDTO));
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Calendario actualizado");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _callRoll(List<CallDTO> callDTO) throws Exception {
        trabajadorService.pasarLista(callDTO.stream().map(e -> mapper.toElementoListDisponibilidad(e)).toList());
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Asistencias actualizadas");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _deleteDayOfWork(Long id, Long calendarId) throws Exception {
        trabajadorService.eliminarDiaCalendario(id, calendarId);
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Asistencia eliminada");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _deleteWorker(Long id) throws Exception {

        trabajadorService.bajaTrabajador(id);
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Trabajador  dado de baja");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<List<AttendanceDTO>> _getAttendances() {
        List<AttendanceDTO> asistencias = trabajadorService.trabajadoresDisponiblesPorFecha(LocalDate.now()).stream().map(e -> mapper.toAttendance(e)).toList();
        return ResponseEntity.ok(asistencias);
    }

    @Override
    public ResponseEntity<List<WorkerDTO>> _getAvailableWorkers() {
        List<WorkerDTO> trabajadores = trabajadorService.obtenerTrabajadoresDisponiblesAhora().stream()
                .map(trabajador -> mapper.toWorker(trabajador)).toList();
        return ResponseEntity.ok().body(trabajadores);
    }

    @Override
    public ResponseEntity<List<CalendarDTO>> _getCalendar(Long id) throws Exception {

        List<CalendarDTO> calendario = trabajadorService.obtenerCalendario(id).stream().map(e -> mapper.toCalendarDTO(e)).toList();
        return ResponseEntity.ok(calendario);
    }


    @Override
    public ResponseEntity<WorkerDTO> _getWorker(Long id) throws Exception {
        return ResponseEntity.ok(mapper.toWorker(trabajadorService.obtenerTrabajador(id)));
    }

    @Override
    public ResponseEntity<List<WorkerDTO>> _getWorkers() {
        return ResponseEntity.ok(trabajadorService.obtenerTrabajadoresDisponibles(0, 100).stream()
                .map(trabajador -> mapper.toWorker(trabajador)).toList());
    }


    @Override
    public ResponseEntity<MessageResponseDTO> _signUpWorker(WorkerDTO workerDTO) throws Exception {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(trabajadorService
                .registrarTrabajador(mapper.toTrabajador(workerDTO)).getId()).toUri();
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Trabajador  registrado");
        return ResponseEntity.created(location).body(message);

    }

    @Override
    public ResponseEntity<MessageResponseDTO> _updateCalendar(Long id, List<CalendarDTO> calendarDTO) throws Exception {
        List<Disponibilidad> calendario = new ArrayList<>();

        for (CalendarDTO calendar : calendarDTO) {
            Disponibilidad disponibilidad = new Disponibilidad();
            disponibilidad.setId(calendar.getId());
            disponibilidad.setCheckin(calendar.getCheckin());
            disponibilidad.setCheckout(calendar.getCheckout());
            disponibilidad.setDaywork(calendar.getDay());
            calendario.add(disponibilidad);
        }
        trabajadorService.actualizarCalendario(id, calendario);

        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Calendario actualizado!");

        return ResponseEntity.ok(message);
    }


    @Override
    public ResponseEntity<Void> _updateWorker(Long id, WorkerDTO workerDTO) throws InstanceNotFoundException {
        Trabajador trabajador = mapper.toTrabajador(workerDTO);
        trabajador.setId(id);

        trabajadorService.actualizarTrabajador(trabajador);
        return ResponseEntity.ok(null);
    }
}
