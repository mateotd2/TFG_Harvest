package com.udc.fic.repository;

import com.udc.fic.model.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {

    Boolean existsByDniOrNss(String dni, String nss);

    Page<Trabajador> findByAvailable(Boolean available, Pageable page);

    Optional<Trabajador> findByName(String name);

    @Query("SELECT DISTINCT t FROM Trabajador t JOIN t.calendario d WHERE d.daywork = :diaTrabajo and t.available=true")
    List<Trabajador> findDistinctTrabajadoresByDateAndAvailable(@Param("diaTrabajo") LocalDate diaTrabajo);


}
