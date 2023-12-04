package com.udc.fic.services;

import com.udc.fic.model.Zona;
import com.udc.fic.services.exceptions.DuplicateInstanceException;

import java.util.List;

public interface ZonasService {
    List<Zona> obtenerZonas();

    Zona registrarZona(Zona zona) throws DuplicateInstanceException;
}
