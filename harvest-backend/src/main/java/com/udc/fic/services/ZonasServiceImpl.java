package com.udc.fic.services;

import com.udc.fic.model.Zona;
import com.udc.fic.repository.ZonasRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Zona obtenerZona(Long id) throws InstanceNotFoundException {
        Optional<Zona> zonaOptional = zonasRepository.findById(id);
        if (zonaOptional.isPresent()){
            return zonaOptional.get();
        }else{
            throw new InstanceNotFoundException();
        }

    }

    @Override
    public void actualizarZona(Long id, Zona zona) throws InstanceNotFoundException, DuplicateInstanceException {

        Optional<Zona> zonaOptional = zonasRepository.findById(id);
        if (zonaOptional.isPresent()){
            Zona zonaObtenida = zonaOptional.get();
            if (!zona.getReference().equals(zonaObtenida.getReference())){
                // Check si nueva referencia coincide con alguna en DB
                if (zonasRepository.existsByReference(zona.getReference())){
                    throw new DuplicateInstanceException("Reference already exists", zona.getReference());
                }
                zonaObtenida.setDescription(zona.getDescription());
                zonaObtenida.setFormation(zona.getFormation());
                zonaObtenida.setName(zona.getName());
                zonaObtenida.setSurface(zona.getSurface());
                zonaObtenida.setReference(zona.getReference());
            }

        }else {
            throw new InstanceNotFoundException();
        }
    }
}
