package com.udc.fic.repository;

import com.udc.fic.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TareasRepository extends JpaRepository<Tarea, Long> {


    // Tareas sin acabar
    List<Tarea> findByHoraSalidaNullAndHoraEntradaNotNull();

    //Tareas sin iniciar
    List<Tarea> findByHoraEntradaNull();

    //Tareas pendientes de linea
    List<Tarea> findByHoraEntradaNullAndLineaCampanhaId(Long campanhaId);

}
