package com.udc.fic.services;

import com.udc.fic.harvest.DTOs.ZoneDTO;
import com.udc.fic.mapper.SourceTargetMapper;
import com.udc.fic.mapper.SourceTargetMapperImpl;
import com.udc.fic.model.Formacion;
import com.udc.fic.model.Zona;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZonasServiceTest {

    @Autowired
    SourceTargetMapper mapper = new SourceTargetMapperImpl();


    @Test
    void obtenerZonas() {
        Zona zona = new Zona(1L, "Zona nombre", 300, "Descripcion", Formacion.EMPARRADO, "12345678901234567890");
        Zona zona2 = new Zona(1L, "Zona nombre", 300, "Descripcion", Formacion.ESPALDERA, "12345678901234567890");

        ZoneDTO zoneDTO = mapper.toZoneDTO(zona);
        ZoneDTO zoneDTO2 = mapper.toZoneDTO(zona2);
        assertEquals(Formacion.EMPARRADO.toString(), zoneDTO.getFormation().getValue());
        assertEquals(Formacion.ESPALDERA.toString(), zoneDTO2.getFormation().getValue());

    }


}
