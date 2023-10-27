package com.udc.fic.services;

import com.udc.fic.model.Disponibilidad;
import com.udc.fic.model.Trabajador;
import com.udc.fic.repository.DisponibilidadRepository;
import com.udc.fic.repository.TrabajadorRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
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
    public List<Trabajador> obtenerTrabajadores() {
        return trabajadorRepository.findAll();
    }

    @Override
    public Trabajador obtenerTrabajador(Long id) throws InstanceNotFoundException {
        Optional<Trabajador> res = trabajadorRepository.findById(id);
        if (!res.isPresent()) {
            throw new InstanceNotFoundException();
        }
        LOGGER.info("Obteniendo trabajador con id: {}", id);
        return res.get();
    }

    @Override
    public Trabajador registrarTrabajador(Trabajador trabajador) throws DuplicateInstanceException {

        if (trabajadorRepository.existsByDni(trabajador.getDni())) {
            throw new DuplicateInstanceException("DNI already exists", trabajador.getDni());
        }
        LOGGER.info("Añadiendo trabajador con dni: {}", trabajador.getDni());
        trabajadorRepository.save(trabajador);

        return trabajador;
    }

    @Override
    public Trabajador actualizarTrabajador(Trabajador trabajador) throws InstanceNotFoundException {

        if (!trabajadorRepository.existsById(trabajador.getId())) {
            throw new InstanceNotFoundException();
        }
        LOGGER.info("Actualizando trabajador con id: {}", trabajador.getId());

        trabajadorRepository.save(trabajador);

        return trabajador;
    }

    @Override
    public void eliminarTrabajador(Long id) {
        LOGGER.info("Eliminando trabajador con id: {}", id);
        trabajadorRepository.deleteById(id);
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
}
