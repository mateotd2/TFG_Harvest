package com.udc.fic.services;

import com.udc.fic.model.*;
import com.udc.fic.repository.CampanhaRepository;
import com.udc.fic.repository.TareasRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
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

    @Autowired
    TareasRepository tareasRepository;

    @Test
    void comenzarCampanhaTestException() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        assertThrows(DuplicateInstanceException.class, () -> campanhaService.comenzarCampanha());
    }

    @Test
    void comenzarCampanha() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        List<Campanha> campanhas = campanhaRepository.findAll();
        // Se crea una campanha
        assertEquals(1, campanhas.size());

        Campanha campanha = campanhas.get(0);
        List<ZonaCampanha> zonaCampanhas = campanha.getZonaCampanhas();
        // Se crea con la campaña 2 zonas
        assertEquals(2, zonaCampanhas.size());

        List<LineaCampanha> lineaCampanhas1 = zonaCampanhas.get(0).getLineaCampanhas();

        // Por cada zona se crean 3 lineas que estan habilitadas
        assertEquals(3, lineaCampanhas1.size());

        assertEquals(6, tareasRepository.findAll().size());
        assertEquals(6, tareasRepository.findByHoraSalidaNull().size());

    }

    @Test
    void comenzarPoda() throws InstanceNotFoundException, DuplicateInstanceException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        List<Campanha> campanhas = campanhaRepository.findAll();
        assertEquals(1, campanhas.size());
        Campanha campanha = campanhas.get(0);

        assertEquals(Fase.PODA, campanha.getFaseCamp());

    }

    @Test
    void comenzarRecoleccion() throws InstanceNotFoundException, DuplicateInstanceException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        List<Campanha> campanhas = campanhaRepository.findAll();
        assertEquals(1, campanhas.size());
        Campanha campanha = campanhas.get(0);

        assertEquals(Fase.RECOLECCION_CARGA, campanha.getFaseCamp());

    }

    @Test
    void finalizarCampanha() throws InstanceNotFoundException, DuplicateInstanceException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        campanhaService.finalizarCampanha();

        List<Campanha> campanhas = campanhaRepository.findAll();
        assertEquals(1, campanhas.size());
        Campanha campanha = campanhas.get(0);

        assertNotNull(campanha.getFinalizacion());


    }

    @Test
    void transicionDeFasesTest() throws DuplicateInstanceException, InstanceNotFoundException {
        //Probamos que las transiciones sin tocarse se crean las tareas de cada una de las fases de campaña

        campanhaService.comenzarCampanha();
        assertEquals(TipoTrabajo.LIMPIEZA, campanhaService.mostrarTareasPendientes().get(1).getTipoTrabajo());

        assertEquals(6,campanhaService.mostrarTareasPendientes().size());

        campanhaService.comenzarPoda();

        assertEquals(6,campanhaService.mostrarTareasPendientes().size());
        assertEquals(TipoTrabajo.PODA, campanhaService.mostrarTareasPendientes().get(1).getTipoTrabajo());

        campanhaService.comenzarRecoleccion();

        assertEquals(6,campanhaService.mostrarTareasPendientes().size());
        assertEquals(TipoTrabajo.RECOLECCION, campanhaService.mostrarTareasPendientes().get(1).getTipoTrabajo());

        campanhaService.finalizarCampanha();

        assertEquals(0,campanhaService.mostrarTareasPendientes().size());


    }

    @Test
    void finalizarCampanhaException() {

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.finalizarCampanha());
    }

    @Test
    void finalizarCampanhaFaseException() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        assertThrows(InstanceNotFoundException.class, () -> campanhaService.finalizarCampanha());
    }

    @Test
    void comenzarRecoleccionException() {

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarRecoleccion());
    }

    @Test
    void comenzarRecoleccionFaseException() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarRecoleccion());
    }

    @Test
    void comenzarPodaException() {

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarPoda());
    }

    @Test
    void comenzarPodaFaseException() throws InstanceNotFoundException, DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        campanhaService.comenzarPoda();

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarPoda());
    }



}