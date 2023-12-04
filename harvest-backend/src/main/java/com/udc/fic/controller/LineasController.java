package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.MessageResponseDTO;
import com.udc.fic.harvest.DTOs.ZoneDTO;
import com.udc.fic.harvest.controller.LineasApi;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.services.ZonasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class LineasController implements LineasApi {

    @Autowired
    ZonasService zonasService;

    @Autowired
    SourceTargetMapper mapper;

    @Override
    public ResponseEntity<MessageResponseDTO> _addZone(ZoneDTO zoneDTO) throws Exception {

        mapper.toZoneDTO(zonasService.registrarZona(mapper.toZona(zoneDTO)));
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Zona añadida");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<List<ZoneDTO>> _getZones() throws Exception {
        List<ZoneDTO> zonas = zonasService.obtenerZonas().stream().map(e -> mapper.toZoneDTO(e)).toList();
        return ResponseEntity.ok(zonas);
    }
}
