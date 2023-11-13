package com.udc.fic.services;

import com.udc.fic.model.Asistencia;
import com.udc.fic.model.Disponibilidad;
import com.udc.fic.model.ElementoListaDisponibilidad;
import com.udc.fic.model.Trabajador;
import com.udc.fic.repository.DisponibilidadRepository;
import com.udc.fic.repository.TrabajadorRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class TrabajadorServiceImpl implements TrabajadorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrabajadorServiceImpl.class);


    @Autowired
    TrabajadorRepository trabajadorRepository;
    @Autowired
    DisponibilidadRepository disponibilidadRepository;


    @Override
    public void pasarLista(List<ElementoListaDisponibilidad> lista) throws InstanceNotFoundException {


        for (ElementoListaDisponibilidad elemento : lista) {
            if (!disponibilidadRepository.existsById(elemento.getId())) {
                throw new InstanceNotFoundException();
            }
        }

        for (ElementoListaDisponibilidad elemento : lista) {
            Disponibilidad disponibilidad = disponibilidadRepository.findById(elemento.getId()).get();
            disponibilidad.setAttendance(true);

            if (elemento.getCheckin() != null) disponibilidad.setCheckin(elemento.getCheckin());
            if (elemento.getCheckout() != null) disponibilidad.setCheckout(elemento.getCheckout());

            disponibilidadRepository.save(disponibilidad);
        }


    }

    @Override
    public List<Trabajador> obtenerTrabajadoresDisponibles(int page, int amount) {
        Pageable pagina = PageRequest.of(page, amount);
        return trabajadorRepository.findByAvailable(true, pagina).getContent();
    }

    @Override
    public Trabajador obtenerTrabajador(Long id) throws InstanceNotFoundException {
        Optional<Trabajador> res = trabajadorRepository.findById(id);
        if (res.isEmpty()) {
            throw new InstanceNotFoundException();
        }
        LOGGER.info("Obteniendo trabajador con id: {}", id);
        return res.get();
    }

    @Override
    public Trabajador registrarTrabajador(Trabajador trabajador) throws DuplicateInstanceException {

        if (trabajadorRepository.existsByDniOrNss(trabajador.getDni(), trabajador.getNss())) {
            throw new DuplicateInstanceException("DNI or NSS already exists", trabajador.getDni());
        }
        LOGGER.info("Añadiendo trabajador con dni: {}", trabajador.getDni());
        trabajadorRepository.save(trabajador);

        return trabajador;
    }

    @Override
    public Trabajador actualizarTrabajador(Trabajador trabajador) throws InstanceNotFoundException {

        Optional<Trabajador> trabajadorOptional = trabajadorRepository.findById(trabajador.getId());
        if (trabajadorOptional.isPresent()) {

            Trabajador trabajadorObtenido = trabajadorOptional.get();
            if (!trabajadorObtenido.isAvailable()) {
                throw new InstanceNotFoundException();
            }
            LOGGER.info("Actualizando trabajador con id: {}", trabajador.getId());

            trabajador.setAvailable(trabajadorObtenido.isAvailable());
            trabajador.setCalendario(trabajadorObtenido.getCalendario());

            trabajadorRepository.save(trabajador);

            return trabajador;
        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public void bajaTrabajador(Long id) throws InstanceNotFoundException {

        Optional<Trabajador> trabajadorOptional = trabajadorRepository.findById(id);
        if (trabajadorOptional.isPresent()) {
            Trabajador trabajadorObtenido = trabajadorOptional.get();
            if (!trabajadorObtenido.isAvailable()) {
                throw new InstanceNotFoundException();
            }

            LOGGER.info("Dando de baja trabajador con id: {}", id);
            disponibilidadRepository.deleteByTrabajadorId(id);


            trabajadorObtenido.setAvailable(false);

            trabajadorRepository.save(trabajadorObtenido);

        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public void registrarDisponibilidad(Disponibilidad disponibilidad, Trabajador trabajador) throws InstanceNotFoundException {

        if (!trabajadorRepository.existsById(trabajador.getId())) {
            throw new InstanceNotFoundException();
        }
        LOGGER.info("Añadiendo disponibildad al trabajador con id: {}", trabajador.getId());
        disponibilidad.setTrabajador(trabajador);

        disponibilidadRepository.save(disponibilidad);
    }

    @Override
    public List<Asistencia> trabajadoresDisponiblesPorFecha(LocalDate date) {
        return disponibilidadRepository.asistenciasByDia(date);
    }


}
