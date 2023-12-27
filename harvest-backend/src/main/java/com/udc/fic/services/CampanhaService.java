package com.udc.fic.services;

import com.udc.fic.services.exceptions.DuplicateInstanceException;

import javax.management.InstanceNotFoundException;

public interface CampanhaService {

    void comenzarCampanha() throws DuplicateInstanceException;

    void comenzarPoda() throws InstanceNotFoundException;

    void comenzarRecoleccion() throws InstanceNotFoundException;

    void finalizarCampanha() throws InstanceNotFoundException;


}
