package com.udc.fic.repository;

import com.udc.fic.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    void deleteByTrabajadorId(Long trabajadorId);
}
