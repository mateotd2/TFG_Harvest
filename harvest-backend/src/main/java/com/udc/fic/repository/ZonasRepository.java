package com.udc.fic.repository;

import com.udc.fic.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZonasRepository extends JpaRepository<Zona, Long> {

    boolean existsByName(String name);

}
