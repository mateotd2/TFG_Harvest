package com.udc.fic.services;

import com.udc.fic.model.Asistencia;
import com.udc.fic.model.Disponibilidad;
import com.udc.fic.model.ElementoListaDisponibilidad;
import com.udc.fic.model.Trabajador;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.InvalidChecksException;
import com.udc.fic.services.exceptions.InvalidDateException;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TrabajadorService {

    void pasarLista(List<ElementoListaDisponibilidad> lista) throws InstanceNotFoundException;

    List<Trabajador> obtenerTrabajadoresDisponibles(int page, int amount);

    List<Trabajador> obtenerTrabajadoresDisponiblesAhora();

    Trabajador obtenerTrabajador(Long id) throws InstanceNotFoundException;

    Trabajador registrarTrabajador(Trabajador trabajador) throws DuplicateInstanceException;

    Trabajador actualizarTrabajador(Trabajador trabajador) throws InstanceNotFoundException;


    void bajaTrabajador(Long id) throws InstanceNotFoundException;

    List<Asistencia> trabajadoresDisponiblesPorFecha(LocalDate date);

    List<Disponibilidad> obtenerCalendario(Long trabajadorId) throws InstanceNotFoundException;

    void altaDiaCalendario(Long trabajadorId, Disponibilidad disponibilidad) throws InstanceNotFoundException, InvalidDateException, InvalidChecksException;

    void eliminarDiaCalendario(Long trabajadorId, Long disponibilidadId) throws InstanceNotFoundException;

    void actualizarCalendario(Long trabajadorId, List<Disponibilidad> calendario) throws InstanceNotFoundException, InvalidChecksException, InvalidDateException;

    LocalTime obtenerSalidaJornada(Long id);
}
