package com.udc.fic.services;

import com.udc.fic.model.Disponibilidad;
import com.udc.fic.model.Trabajador;
import com.udc.fic.services.exceptions.DuplicateInstanceException;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface TrabajadorService {


    List<Trabajador> obtenerTrabajadores(int page, int amount);

    Trabajador obtenerTrabajador(Long id) throws InstanceNotFoundException;

    Trabajador registrarTrabajador(Trabajador trabajador) throws DuplicateInstanceException;

    Trabajador actualizarTrabajador(Trabajador trabajador) throws InstanceNotFoundException;

    void eliminarTrabajador(Long id);

    void registrarDisponibilidad(Disponibilidad disponibilidad, Trabajador trabajador) throws InstanceNotFoundException;

}
