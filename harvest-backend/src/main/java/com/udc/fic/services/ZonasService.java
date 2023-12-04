package com.udc.fic.services;

import com.udc.fic.model.Zona;
import com.udc.fic.services.exceptions.DuplicateInstanceException;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface ZonasService {
    List<Zona> obtenerZonas();

    Zona registrarZona(Zona zona) throws DuplicateInstanceException;

    Zona obtenerZona(Long id) throws InstanceNotFoundException;

    void actualizarZona(Long id, Zona zona) throws InstanceNotFoundException,DuplicateInstanceException;
}
