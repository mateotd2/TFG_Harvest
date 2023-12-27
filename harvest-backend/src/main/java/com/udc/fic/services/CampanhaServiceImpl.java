package com.udc.fic.services;

import com.udc.fic.model.*;
import com.udc.fic.repository.CampanhaRepository;
import com.udc.fic.repository.ZonasRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CampanhaServiceImpl implements CampanhaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CampanhaServiceImpl.class);

    @Autowired
    CampanhaRepository campanhaRepository;

    @Autowired
    ZonasRepository zonasRepository;


    private ZonaCampanha crearZonaCampanha(Zona zona, Campanha campanha) {
        ZonaCampanha zonaCampanha = new ZonaCampanha();
        zonaCampanha.setCampanha(campanha);
        zonaCampanha.setZona(zona);


        List<Linea> lineas = zona.getLineas();

        List<LineaCampanha> lineaCampanhas = new ArrayList<>();

        // Cada linea se comprueba si esta habilitada para su recolección y se añade a la campaña
        lineas.forEach(e ->
                {
                    if (e.isHarvestEnabled()) {
                        LineaCampanha lineaNueva = new LineaCampanha();
                        lineaNueva.setEstado(Estado.PAUSADO);
                        lineaNueva.setPorcentajeTrabajado(0);
                        lineaNueva.setCargaLista(false);
                        lineaNueva.setZonaCampanha(zonaCampanha);

                        lineaCampanhas.add(lineaNueva);
                    }
                }
        );

        zonaCampanha.setLineaCampanhas(lineaCampanhas);


        return zonaCampanha;
    }

    private void despausarLineasCampanha(Campanha campanha) {
        List<ZonaCampanha> zonaCampanhas = campanha.getZonaCampanhas();

        zonaCampanhas.forEach(
                zonaCampanha -> zonaCampanha.getLineaCampanhas().forEach(
                        lineaCampanha -> {
                            lineaCampanha.setEstado(Estado.PAUSADO);
                            lineaCampanha.setPorcentajeTrabajado(0);
                        }
                )
        );
    }

    @Override
    public void comenzarCampanha() throws DuplicateInstanceException {
        int ano = LocalDateTime.now().getYear();
        if (!campanhaRepository.existsByAno(ano)) {
            LOGGER.info("Comenzando campaña del año {}", ano);
            Campanha campanha = new Campanha();


            List<ZonaCampanha> zonasCampanha = new ArrayList<>();
            List<Zona> zonas = zonasRepository.findAll();

            // Crea una zonaCampanha por cada zona registrada
            zonas.forEach(z -> {
                ZonaCampanha zonaCampanha = crearZonaCampanha(z, campanha);
                zonasCampanha.add(zonaCampanha);
            });

            campanha.setZonaCampanhas(zonasCampanha);
            campanha.setAno(ano);
            campanha.setInicio(LocalDate.now());
            campanha.setFaseCamp(Fase.LIMPIEZA);

            // TODO: inicializar las tareas pendientes


            campanhaRepository.save(campanha);


        } else {
            throw new DuplicateInstanceException("La campaña anual ya esta iniciada", ano);
        }

    }

    @Override
    public void comenzarPoda() throws InstanceNotFoundException {
        int ano = LocalDateTime.now().getYear();
        Optional<Campanha> campanhaOptional = campanhaRepository.findByAno(ano);

        if (campanhaOptional.isPresent()) {
            Campanha campanha = campanhaOptional.get();
            // Comprobar que se paso por la fase de limpieza
            if (!campanha.getFaseCamp().equals(Fase.LIMPIEZA)) {
                throw new InstanceNotFoundException();
            }

            despausarLineasCampanha(campanha);

            campanha.setFaseCamp(Fase.PODA);
            // TODO: Finalizar tareas pendientes y crear las tareas de poda


            campanhaRepository.save(campanha);

        } else {
            throw new InstanceNotFoundException();
        }

    }

    @Override
    public void comenzarRecoleccion() throws InstanceNotFoundException {
        int ano = LocalDateTime.now().getYear();
        Optional<Campanha> campanhaOptional = campanhaRepository.findByAno(ano);

        if (campanhaOptional.isPresent()) {
            Campanha campanha = campanhaOptional.get();
            // Comprobar que se paso por la fase de Poda
            if (!campanha.getFaseCamp().equals(Fase.PODA)) {
                throw new InstanceNotFoundException();
            }

            despausarLineasCampanha(campanha);

            campanha.setFaseCamp(Fase.RECOLECCION_CARGA);
            // TODO: Finalizar tareas pendientes y crear las tareas de recoleccion


            campanhaRepository.save(campanha);

        } else {
            throw new InstanceNotFoundException();
        }

    }

    @Override
    public void finalizarCampanha() throws InstanceNotFoundException {
        int ano = LocalDateTime.now().getYear();
        Optional<Campanha> campanhaOptional = campanhaRepository.findByAno(ano);

        if (campanhaOptional.isPresent()) {
            Campanha campanha = campanhaOptional.get();
            // Comprobar que se paso por la fase de recoleccion
            if (!campanha.getFaseCamp().equals(Fase.RECOLECCION_CARGA)) {
                throw new InstanceNotFoundException();
            }
            campanha.setFinalizacion(LocalDate.now());
            //TODO: finalizar todas las tareas pendientes


            campanhaRepository.save(campanha);

        } else {
            throw new InstanceNotFoundException();
        }
    }
}
