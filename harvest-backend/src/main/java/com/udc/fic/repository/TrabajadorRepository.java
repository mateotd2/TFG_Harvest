package com.udc.fic.repository;

import com.udc.fic.model.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {

    Boolean existsByDniOrNss(String dni, String nss);

    Page<Trabajador> findByAvailable(Boolean available, Pageable page);

    Optional<Trabajador> findByName(String name);

    @Query("SELECT DISTINCT t FROM Trabajador t JOIN t.calendario d WHERE t.inTask=false AND d.daywork = :diaTrabajo AND t.available=true AND :hora BETWEEN d.checkin AND d.checkout")
    List<Trabajador> findDistinctTrabajadoresByDateAndAvailable(@Param("diaTrabajo") LocalDate diaTrabajo, LocalTime hora);


    // Checka si los ids de los trabajadores existen y estan disponibles
    boolean existsByIdInAndInTaskFalse(List<Long> ids);
}
