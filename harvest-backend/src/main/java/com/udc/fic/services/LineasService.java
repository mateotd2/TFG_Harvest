package com.udc.fic.services;

import com.udc.fic.model.Linea;
import com.udc.fic.model.TipoVid;
import com.udc.fic.services.exceptions.DuplicateInstanceException;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface LineasService {
    List<Linea> obtenerLineas(Long id) throws InstanceNotFoundException;

    Linea registrarLinea(Linea linea, Long zonaId, Long tipoVidId) throws InstanceNotFoundException, DuplicateInstanceException;

    List<TipoVid> obtenerTiposVid();

    void actualizarLinea(Long id, Long typeVidId, Linea linea) throws InstanceNotFoundException;

    Linea obtenerDetalleLinea(Long id) throws InstanceNotFoundException;

    void habilitarLinea(Long id) throws InstanceNotFoundException;

    void deshabilitarLinea(Long id) throws InstanceNotFoundException;

    void eliminarLinea(Long id) throws InstanceNotFoundException;
}
