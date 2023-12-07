package com.udc.fic.services;

import com.udc.fic.model.Linea;
import com.udc.fic.model.TipoVid;
import com.udc.fic.model.Zona;
import com.udc.fic.repository.LineasRepository;
import com.udc.fic.repository.TipoVidRepository;
import com.udc.fic.repository.ZonasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineasServiceImpl implements LineasService {

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
    public Linea registrarLinea(Linea linea, Long zonaId, Long tipoVidId) throws InstanceNotFoundException {
        Optional<Zona> zonaOptional = zonasRepository.findById(zonaId);
        Optional<TipoVid> tipoVidOptional = tipoVidRepository.findById(tipoVidId);
        if (zonaOptional.isPresent() && tipoVidOptional.isPresent()) {
            Zona zona = zonaOptional.get();

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
}
