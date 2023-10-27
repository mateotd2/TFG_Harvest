package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.MessageResponseDTO;
import com.udc.fic.harvest.DTOs.WorkerDTO;
import com.udc.fic.harvest.controller.TrabajadoresApi;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.services.TrabajadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<WorkerDTO> _getWorker(Long id) throws Exception {


        return null;
    }

    @Override
    public ResponseEntity<List<WorkerDTO>> _getWorkers() {
        return ResponseEntity.ok(trabajadorService.obtenerTrabajadores().stream()
                .map(trabajador -> mapper.toWorker(trabajador)).toList());
    }


    @Override
    public ResponseEntity<MessageResponseDTO> _signUpWorker(WorkerDTO workerDTO) throws Exception {
        return null;
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _updateWorker(Long id, WorkerDTO workerDTO) throws Exception {
        return null;
    }
}
