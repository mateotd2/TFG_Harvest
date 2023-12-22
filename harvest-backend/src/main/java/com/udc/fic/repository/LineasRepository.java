package com.udc.fic.repository;

import com.udc.fic.model.Linea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineasRepository extends JpaRepository<Linea, Long> {
    boolean existsBylineNumberAndZonaId(int linenumber, Long zonaId);
}
