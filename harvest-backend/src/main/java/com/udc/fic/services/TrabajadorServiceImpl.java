package com.udc.fic.services;

import com.udc.fic.model.Asistencia;
import com.udc.fic.model.Disponibilidad;
import com.udc.fic.model.ElementoListaDisponibilidad;
import com.udc.fic.model.Trabajador;
import com.udc.fic.repository.DisponibilidadRepository;
import com.udc.fic.repository.TrabajadorRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.InvalidChecksException;
import com.udc.fic.services.exceptions.InvalidDateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.Comparator;
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

        LOGGER.info("Realizando actualizacion de asistencias.");
        for (ElementoListaDisponibilidad elemento : lista) {
            if (!disponibilidadRepository.existsById(elemento.getId())) {
                throw new InstanceNotFoundException();
            }
        }

        for (ElementoListaDisponibilidad elemento : lista) {
            Disponibilidad disponibilidad = disponibilidadRepository.findById(elemento.getId()).get();
            disponibilidad.setAttendance(elemento.isAttendance());

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
        trabajador.setAvailable(true);
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
            trabajadorObtenido.setAvailable(false);
            trabajadorRepository.save(trabajadorObtenido);

        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public List<Asistencia> trabajadoresDisponiblesPorFecha(LocalDate date) {
        return disponibilidadRepository.asistenciasByDia(date);
    }

    @Override
    public List<Disponibilidad> obtenerCalendario(Long trabajadorId) throws InstanceNotFoundException {
        Optional<Trabajador> trabajadorOptional = trabajadorRepository.findById(trabajadorId);
        LOGGER.info("Obtener calendario de trabajador: {}", trabajadorId);
        if (trabajadorOptional.isPresent()) {
            Trabajador trabajador = trabajadorOptional.get();
            return trabajador.getCalendario();
        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public void altaDiaCalendario(Long trabajadorId, Disponibilidad disponibilidad) throws InstanceNotFoundException, InvalidDateException, InvalidChecksException {
        Optional<Trabajador> trabajadorOptional = trabajadorRepository.findById(trabajadorId);
        LOGGER.info("Obtener calendario de trabajador: {}", trabajadorId);
        if (trabajadorOptional.isPresent()) {


            // Valida que no es una fecha anterior a la actual
            if (!disponibilidad.getDaywork().isAfter(LocalDate.now())) {
                throw new InvalidDateException();
            }

            // Valida que checkIn y checkout estan en orden
            if (!disponibilidad.getCheckin().isBefore(disponibilidad.getCheckout())) {
                throw new InvalidChecksException();
            }
            Trabajador trabajador = trabajadorOptional.get();
            disponibilidad.setTrabajador(trabajador);

            trabajador.getCalendario().add(disponibilidad);
            trabajadorRepository.save(trabajador);
        } else {
            throw new InstanceNotFoundException();
        }


    }

    @Override
    public void eliminarDiaCalendario(Long trabajadorId, Long disponibilidadId) throws InstanceNotFoundException {
        if (disponibilidadRepository.existsByIdAndTrabajadorIdAndDayworkAfter(disponibilidadId, trabajadorId, LocalDate.now())) {
            LOGGER.info("Eliminando dia de trabajo con id: {}", disponibilidadId);
            disponibilidadRepository.deleteById(disponibilidadId);
        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public void actualizarCalendario(Long trabajadorId, List<Disponibilidad> calendario) throws InstanceNotFoundException, InvalidChecksException{
        Optional<Trabajador> trabajadorOptional = trabajadorRepository.findById(trabajadorId);
        calendario.sort(Comparator.comparing(Disponibilidad::getId));
        if (trabajadorOptional.isPresent()) {
            Trabajador trabajador = trabajadorOptional.get();

            // Compruebo que el calendario del trabajador y el calendario para actualizar tienen la misma cantidad de items y los mismos Ids
            if (trabajador.getCalendario().size() != calendario.size()) {
                throw new InstanceNotFoundException();
            }
            boolean sameIds = trabajador.getCalendario().stream().allMatch(itemTrab -> calendario.stream().anyMatch(
                    itemCal -> itemCal.getId().equals(itemTrab.getId())             // Comprueba que son los mimsos Ids
                            && itemCal.getDaywork().isEqual(itemTrab.getDaywork()))); // Y ademas que son las mismas fechas
            if (!sameIds) {
                throw new InstanceNotFoundException();
            }

            //Validar si los checkins son anteriores que los checkouts
            for (int i = 0; i < trabajador.getCalendario().size(); i++) {
                //  Validacion de checks
                if (!calendario.get(i).getCheckin().isBefore(calendario.get(i).getCheckout())) {
                    throw new InvalidChecksException();
                }
                // Solo actualiza las horas
                LOGGER.info("Actualizando calendario de trabajador {}, con checkin:{} y checkout:{}", trabajadorId, trabajador.getCalendario().get(i).getCheckin(), trabajador.getCalendario().get(i).getCheckout());
                trabajador.getCalendario().get(i).setCheckin(calendario.get(i).getCheckin());
                trabajador.getCalendario().get(i).setCheckout(calendario.get(i).getCheckout());

            }

            trabajadorRepository.save(trabajador);


        } else {
            throw new InstanceNotFoundException();
        }

    }


}
