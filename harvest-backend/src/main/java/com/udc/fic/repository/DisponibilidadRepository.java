package com.udc.fic.repository;

import com.udc.fic.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    List<Disponibilidad> findByTrabajadorId(Long trabajadorId);

}
