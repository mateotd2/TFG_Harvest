package com.udc.fic.controller;

import com.udc.fic.harvest.DTOs.MessageResponseDTO;
import com.udc.fic.harvest.controller.CampanhaApi;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.services.CampanhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class CampanhaController implements CampanhaApi {

    @Autowired
    SourceTargetMapper mapper;

    @Autowired
    CampanhaService campanhaService;

    @Override
    public ResponseEntity<MessageResponseDTO> _startCampaign() throws Exception {

        campanhaService.comenzarCampanha();
        MessageResponseDTO message = new MessageResponseDTO();
        message.message("Campaña iniciada");
        return ResponseEntity.ok().body(message);

    }
}
