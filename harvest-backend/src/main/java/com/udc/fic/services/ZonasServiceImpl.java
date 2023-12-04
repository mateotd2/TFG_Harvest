package com.udc.fic.services;

import com.udc.fic.model.Zona;
import com.udc.fic.repository.ZonasRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ZonasServiceImpl implements ZonasService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ZonasServiceImpl.class);


    @Autowired
    ZonasRepository zonasRepository;

    @Override
    public List<Zona> obtenerZonas() {
        return zonasRepository.findAll();
    }

    @Override
    public Zona registrarZona(Zona zona) throws DuplicateInstanceException {
        if (zonasRepository.existsByReference(zona.getReference())) {
            throw new DuplicateInstanceException("Reference already exists", zona.getReference());
        }
        LOGGER.info("Añadiendo zona con referencia: {}", zona.getReference());

        return zonasRepository.save(zona);
    }
}
