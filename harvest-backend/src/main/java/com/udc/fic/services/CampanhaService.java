package com.udc.fic.services;

import com.udc.fic.model.Fase;
import com.udc.fic.model.Tarea;
import com.udc.fic.model.Tractor;
import com.udc.fic.services.exceptions.*;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface CampanhaService {

    boolean notificacionTareasCarga();

    void comenzarCampanha() throws DuplicateInstanceException;

    void comenzarPoda() throws InstanceNotFoundException;

    void comenzarRecoleccion() throws InstanceNotFoundException;

    void finalizarCampanha() throws InstanceNotFoundException;


    List<Tarea> mostrarTareasPendientes();

    List<Tarea> mostrarTareasPendientesDeCarga();

    List<Tarea> mostrarTareasSinFinalizar();

    List<Tarea> mostrarTareasSinFinalizarDeCarga();

    List<Tarea> mostrarTareasFinalizadas();

    Tarea mostrarDetallesTarea(Long id) throws InstanceNotFoundException;

    Fase mostrarFaseCampanha();

    void comenzarTarea(List<Long> idsTrabajadores, Long idTarea, Long idEmpleado) throws InstanceNotFoundException, TaskAlreadyStartedException;

    void pararTarea(Long idTarea, String comentarios, int porcentaje, boolean carga) throws InstanceNotFoundException, InvalidChecksException, TaskAlreadyEndedException, TaskNotStartedException;

    // FUNCIONALIDADES DE TRACTORISTA

    List<Tarea> mostrarTareasFinalizadasDeCarga();

    void comenzarTareasCarga(List<Long> idTareas, Long idEmpleado, Long idTractor, List<Long> idsTrabajadores) throws InstanceNotFoundException, PermissionException;

    void pararTareasCarga(List<Long> idTareas, String comentario) throws InstanceNotFoundException, InvalidChecksException;

    List<Tractor> tractoresDisponibles();
}
