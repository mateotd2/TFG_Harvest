package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.*;
import com.udc.fic.harvest.controller.LineasApi;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.model.Linea;
import com.udc.fic.services.LineasService;
import com.udc.fic.services.ZonasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class LineasController implements LineasApi {

    @Autowired
    ZonasService zonasService;
    @Autowired
    LineasService lineasService;


    @Autowired
    SourceTargetMapper mapper;

    @Override
    public ResponseEntity<MessageResponseDTO> _addZone(ZoneDTO zoneDTO) throws Exception {

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(mapper.toZoneDTO(zonasService.registrarZona(mapper.toZona(zoneDTO)))).toUri();
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Zona añadida");
        return ResponseEntity.created(location).body(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _deleteLine(Long id) throws Exception {
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Linea  borrada");

        lineasService.eliminarLinea(id);
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<Void> _disableLine(Long id) throws Exception {
        lineasService.deshabilitarLinea(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> _enableLine(Long id) throws Exception {
        lineasService.habilitarLinea(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LineDetailsDTO> _getLineDetails(Long id) throws Exception {
        Linea linea = lineasService.obtenerDetalleLinea(id);
        LineDetailsDTO lineDetailsDTO = mapper.toLineDetailsDTO(linea);
        lineDetailsDTO.setVid(mapper.toTypeVidDTO(linea.getTipoVid()));
        return ResponseEntity.ok().body(lineDetailsDTO);
    }


    @Override
    public ResponseEntity<ZoneDTO> _getZone(Long id) throws Exception {

        return ResponseEntity.ok(mapper.toZoneDTO(zonasService.obtenerZona(id)));
    }

    @Override
    public ResponseEntity<List<ZoneDTO>> _getZones() {
        List<ZoneDTO> zonas = zonasService.obtenerZonas().stream().map(e -> mapper.toZoneDTO(e)).toList();
        return ResponseEntity.ok(zonas);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _updateLineDetails(Long id, LineDTO lineDTO) throws Exception {
        lineasService.actualizarLinea(id, lineDTO.getIdTypeVid(), mapper.toLine(lineDTO));
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Linea actualizada");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<MessageResponseDTO> _updateZone(Long id, ZoneDTO zoneDTO) throws Exception {
        zonasService.actualizarZona(id, mapper.toZona(zoneDTO));
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Zona Actualizada");
        return ResponseEntity.ok().body(message);
    }

    @Override
    public ResponseEntity<List<LineDTO>> _getLines(Long id) throws Exception {

        return ResponseEntity.ok(lineasService.obtenerLineas(id).stream()
                .map(e -> mapper.toLineDTO(e))
                .toList());
    }

    @Override
    public ResponseEntity<List<TypeVidDTO>> _getVids() {
        return ResponseEntity.ok(lineasService.obtenerTiposVid().stream().map(e -> mapper.toTypeVidDTO(e)).toList());
    }


    @Override
    public ResponseEntity<MessageResponseDTO> _addLine(Long id, LineDTO lineDTO) throws Exception {

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(mapper.toLineDTO(lineasService.registrarLinea(mapper.toLine(lineDTO), id, lineDTO.getIdTypeVid()))).toUri();
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Linea añadida");
        return ResponseEntity.created(location).body(message);
    }
}
