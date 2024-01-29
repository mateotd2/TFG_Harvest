package com.udc.fic.repository;

import com.udc.fic.model.Tarea;
import com.udc.fic.model.TipoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TareasRepository extends JpaRepository<Tarea, Long> {


    //TAREAS SIN INICIAR
    List<Tarea> findByHoraEntradaNullAndTipoTrabajoNotLike(TipoTrabajo tipoTrabajo);

    List<Tarea> findByHoraEntradaNullAndTipoTrabajoLike(TipoTrabajo tipoTrabajo);

    default List<Tarea> tareasSinIniciar() {
        return findByHoraEntradaNullAndTipoTrabajoNotLike(TipoTrabajo.CARGA);
    }

    default List<Tarea> tareasSinIniciarDeCarga() {
        return findByHoraEntradaNullAndTipoTrabajoLike(TipoTrabajo.CARGA);
    }

    // TAREAS EN PROGRESO
    List<Tarea> findByHoraSalidaNullAndHoraEntradaNotNullAndTipoTrabajoNotLike(TipoTrabajo tipoTrabajo);

    List<Tarea> findByHoraSalidaNullAndHoraEntradaNotNullAndTipoTrabajoLike(TipoTrabajo tipoTrabajo);

    default List<Tarea> tareasEnProgreso() {
        return findByHoraSalidaNullAndHoraEntradaNotNullAndTipoTrabajoNotLike(TipoTrabajo.CARGA);
    }

    default List<Tarea> tareasEnProgresoDeCarga() {
        return findByHoraSalidaNullAndHoraEntradaNotNullAndTipoTrabajoLike(TipoTrabajo.CARGA);
    }

    // TAREAS FINALIZADAS
    List<Tarea> findByHoraSalidaNotNullAndHoraEntradaNotNullAndTipoTrabajoNotLike(TipoTrabajo tipoTrabajo);

    List<Tarea> findByHoraSalidaNotNullAndHoraEntradaNotNullAndTipoTrabajoLike(TipoTrabajo tipoTrabajo);

    default List<Tarea> tareasFinalizadas() {
        return findByHoraSalidaNotNullAndHoraEntradaNotNullAndTipoTrabajoNotLike(TipoTrabajo.CARGA);
    }

    default List<Tarea> tareasFinalizadasDeCarga() {
        return findByHoraSalidaNotNullAndHoraEntradaNotNullAndTipoTrabajoLike(TipoTrabajo.CARGA);
    }


    // TAREAS PENDIENTES DE LINEA
    List<Tarea> findByHoraEntradaNullAndLineaCampanhaId(Long campanhaId);


    @Query("SELECT t FROM Tarea t  WHERE  t.horaSalida is not NULL AND t.lineaCampanha.zonaCampanha.campanha.ano = :ano ")
    List<Tarea> findTareasFinalizadasDeCampanha(int ano);

    List<Tarea> findByIdInAndHoraEntradaNullAndTipoTrabajo(List<Long> idsTareas, TipoTrabajo tipoCarga);

    @Query("SELECT t FROM Tarea t WHERE t.tractor.id = :idTractor AND t.horaSalida is NULL AND t.horaEntrada is not NULL")
    List<Tarea> findByTractorAndEnProgreso(Long idTractor);

}
