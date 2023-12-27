package com.udc.fic.services;

import com.udc.fic.services.exceptions.DuplicateInstanceException;

public interface CampanhaService {

    void comenzarCampanha() throws DuplicateInstanceException;
}
