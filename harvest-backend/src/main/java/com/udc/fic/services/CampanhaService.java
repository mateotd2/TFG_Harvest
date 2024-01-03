package com.udc.fic.services;

import com.udc.fic.model.Tarea;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.InvalidChecksException;
import com.udc.fic.services.exceptions.TaskAlreadyEndedException;
import com.udc.fic.services.exceptions.TaskAlreadyStartedException;

import javax.management.InstanceNotFoundException;
import java.util.List;

public interface CampanhaService {

    void comenzarCampanha() throws DuplicateInstanceException;

    void comenzarPoda() throws InstanceNotFoundException;

    void comenzarRecoleccion() throws InstanceNotFoundException;

    void finalizarCampanha() throws InstanceNotFoundException;


    List<Tarea> mostrarTareasPendientes();

    List<Tarea> mostrarTareasSinFinalizar();

    // TODO: En la siguiente iteracion pasarle el id de Tractor
    void comenzarTarea(List<Long> idsTrabajadores, Long idTarea, Long idEmpleado) throws InstanceNotFoundException, TaskAlreadyStartedException;

    void pararTarea(Long idTarea, String comentarios, int porcentaje) throws InstanceNotFoundException, TaskAlreadyEndedException, InvalidChecksException;
}
