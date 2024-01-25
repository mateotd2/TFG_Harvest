package com.udc.fic.services;

import com.udc.fic.model.Fase;
import com.udc.fic.model.Tarea;
import com.udc.fic.services.exceptions.*;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface CampanhaService {

    void comenzarCampanha() throws DuplicateInstanceException;

    void comenzarPoda() throws InstanceNotFoundException;

    void comenzarRecoleccion() throws InstanceNotFoundException;

    void finalizarCampanha() throws InstanceNotFoundException;


    List<Tarea> mostrarTareasPendientes();

    List<Tarea> mostrarTareasPendientesDeCarga();

    List<Tarea> mostrarTareasSinFinalizar();

    List<Tarea> mostrarTareasSinFinalizarDeCarga();

    List<Tarea> mostrarTareasFinalizadas();

    List<Tarea> mostrarTareasFinalizadasDeCarga();

    void comenzarTarea(List<Long> idsTrabajadores, Long idTarea, Long idEmpleado, Long idTractor) throws InstanceNotFoundException, TaskAlreadyStartedException, PermissionException, TractorNotAvailableException, InvalidChecksException;

    void pararTarea(Long idTarea, String comentarios, int porcentaje, boolean carga) throws InstanceNotFoundException, TaskAlreadyEndedException, InvalidChecksException, TaskNotStartedException;

    Tarea mostrarDetallesTarea(Long id) throws InstanceNotFoundException;

    Fase mostrarFaseCampanha();
}
