package com.udc.fic.services;

import com.udc.fic.model.Linea;
import com.udc.fic.model.TipoVid;
import com.udc.fic.model.Zona;
import com.udc.fic.repository.LineasRepository;
import com.udc.fic.repository.TipoVidRepository;
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
public class LineasServiceImpl implements LineasService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LineasServiceImpl.class);

    @Autowired
    LineasRepository lineasRepository;

    @Autowired
    ZonasRepository zonasRepository;

    @Autowired
    TipoVidRepository tipoVidRepository;

    @Override
    public List<Linea> obtenerLineas(Long id) throws InstanceNotFoundException {
        Optional<Zona> zonaOptional = zonasRepository.findById(id);
        if (zonaOptional.isPresent()) {
            Zona zona = zonaOptional.get();
            return zona.getLineas();
        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public Linea registrarLinea(Linea linea, Long zonaId, Long tipoVidId) throws InstanceNotFoundException, DuplicateInstanceException {
        Optional<Zona> zonaOptional = zonasRepository.findById(zonaId);
        Optional<TipoVid> tipoVidOptional = tipoVidRepository.findById(tipoVidId);
        if (zonaOptional.isPresent() && tipoVidOptional.isPresent()) {

            Zona zona = zonaOptional.get();
            if (lineasRepository.existsBylineNumberAndZonaId(linea.getLineNumber(), zona.getId())) {
                throw new DuplicateInstanceException("Line already exists", linea.getLineNumber());
            }

            LOGGER.info("Registrando nueva linea en la zona con id {}", zonaId);
            linea.setZona(zona);
            linea.setTipoVid(tipoVidOptional.get());
            zona.getLineas().add(linea);

            zonasRepository.save(zona);

            return linea;


        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public List<TipoVid> obtenerTiposVid() {
        return tipoVidRepository.findAll();
    }

    @Override
    public void actualizarLinea(Long id, Long typeVidId, Linea linea) throws InstanceNotFoundException {
        Optional<Linea> lineaOptional = lineasRepository.findById(id);
        if (lineaOptional.isPresent()) {
            Linea lineaObtenida = lineaOptional.get();
            LOGGER.info("Actualizando detalles de la linea con id:{}", id);
            // Solo puedo modificar el numero de linea, fecha de plantacion y el tipo de vid
            lineaObtenida.setLineNumber(linea.getLineNumber());
            lineaObtenida.setPlantingDate(linea.getPlantingDate());

            Optional<TipoVid> optionalTipoVid = tipoVidRepository.findById(typeVidId);
            if (linea.getTipoVid() != lineaObtenida.getTipoVid() && optionalTipoVid.isPresent()) {

                lineaObtenida.setTipoVid(optionalTipoVid.get());

            }
            lineasRepository.save(lineaObtenida);

        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public Linea obtenerDetalleLinea(Long id) throws InstanceNotFoundException {
        Optional<Linea> optionalLinea = lineasRepository.findById(id);
        if (optionalLinea.isPresent()) {
            return optionalLinea.get();
        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public void habilitarLinea(Long id) throws InstanceNotFoundException {
        Optional<Linea> optionalLinea = lineasRepository.findById(id);
        if (optionalLinea.isPresent()) {
            LOGGER.info("Habilitando recoleccion de la linea con id:{}", id);
            Linea linea = optionalLinea.get();
            linea.setHarvestEnabled(true);
            lineasRepository.save(linea);
        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public void deshabilitarLinea(Long id) throws InstanceNotFoundException {
        Optional<Linea> optionalLinea = lineasRepository.findById(id);
        if (optionalLinea.isPresent()) {
            Linea linea = optionalLinea.get();
            LOGGER.info("Deshabilitando recoleccion de la linea con id:{}", id);
            linea.setHarvestEnabled(false);
            lineasRepository.save(linea);
        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public void eliminarLinea(Long id) throws InstanceNotFoundException {
        if (lineasRepository.existsById(id)) {
            LOGGER.info("Eliminando linea con id: {}", id);
            lineasRepository.deleteById(id);
        } else {
            throw new InstanceNotFoundException();
        }
    }
}
