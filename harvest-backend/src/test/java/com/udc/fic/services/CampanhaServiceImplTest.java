package com.udc.fic.services;

import com.udc.fic.model.Campanha;
import com.udc.fic.model.LineaCampanha;
import com.udc.fic.model.ZonaCampanha;
import com.udc.fic.repository.CampanhaRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CampanhaServiceImplTest {
    @Autowired
    CampanhaService campanhaService;

    @Autowired
    CampanhaRepository campanhaRepository;

    @Test
    void comenzarCampanhaTestException() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        assertThrows(DuplicateInstanceException.class,() -> campanhaService.comenzarCampanha());
    }

    @Test
    void comenzarCampanha() throws DuplicateInstanceException{
        campanhaService.comenzarCampanha();
        List<Campanha> campanhas = campanhaRepository.findAll();
        // Se crea una campanha
        assertEquals(1,campanhas.size());

        Campanha campanha = campanhas.get(0);
        List<ZonaCampanha> zonaCampanhas = campanha.getZonaCampanhas();
        // Se crea con la campaña 2 zonas
        assertEquals(2,zonaCampanhas.size());

        List<LineaCampanha> lineaCampanhas1 = zonaCampanhas.get(0).getLineaCampanhas();

        // Por cada zona se crean 3 lineas que estan habilitadas
        assertEquals(3,lineaCampanhas1.size());

    }
}