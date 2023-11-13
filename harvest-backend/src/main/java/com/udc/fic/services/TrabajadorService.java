package com.udc.fic.services;

import com.udc.fic.model.Asistencia;
import com.udc.fic.model.Disponibilidad;
import com.udc.fic.model.ElementoListaDisponibilidad;
import com.udc.fic.model.Trabajador;
import com.udc.fic.services.exceptions.DuplicateInstanceException;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.List;

public interface TrabajadorService {

    void pasarLista(List<ElementoListaDisponibilidad> lista) throws InstanceNotFoundException;

    List<Trabajador> obtenerTrabajadoresDisponibles(int page, int amount);

    Trabajador obtenerTrabajador(Long id) throws InstanceNotFoundException;

    Trabajador registrarTrabajador(Trabajador trabajador) throws DuplicateInstanceException;

    Trabajador actualizarTrabajador(Trabajador trabajador) throws InstanceNotFoundException;


    void bajaTrabajador(Long id) throws InstanceNotFoundException;

    void registrarDisponibilidad(Disponibilidad disponibilidad, Trabajador trabajador) throws InstanceNotFoundException;

    List<Asistencia> trabajadoresDisponiblesPorFecha(LocalDate date);

}
