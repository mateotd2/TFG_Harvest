package com.udc.fic.repository;

import com.udc.fic.model.Asistencia;
import com.udc.fic.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    void deleteByTrabajadorId(Long trabajadorId);

    @Query("SELECT new com.udc.fic.model.Asistencia( t.name, t.lastname, d.daywork, d.checkin, d.checkout, d.attendance, d.id) FROM Disponibilidad d JOIN  d.trabajador t WHERE  (t.available=true AND d.daywork=:dia)")
    List<Asistencia> asistenciasByDia(@Param("dia") LocalDate dia);
}
