package com.udc.fic.services;

import com.udc.fic.model.*;
import com.udc.fic.repository.CampanhaRepository;
import com.udc.fic.repository.EmpleadoRepository;
import com.udc.fic.repository.TareasRepository;
import com.udc.fic.repository.TrabajadorRepository;
import com.udc.fic.services.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CampanhaServiceImplTest {
    @Autowired
    CampanhaService campanhaService;

    @Autowired
    CampanhaRepository campanhaRepository;

    @Autowired
    TareasRepository tareasRepository;

    @Autowired
    TrabajadorRepository trabajadorRepository;

    @Autowired
    EmpleadoRepository empleadoRepository;


    @Test
    void comenzarCampanhaTestException() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        assertThrows(DuplicateInstanceException.class, () -> campanhaService.comenzarCampanha());
    }

    @Test
    void comenzarCampanha() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        List<Campanha> campanhas = campanhaRepository.findAll();
        // Se crea una campanha
        assertEquals(1, campanhas.size());

        Campanha campanha = campanhas.get(0);
        List<ZonaCampanha> zonaCampanhas = campanha.getZonaCampanhas();
        // Se crea con la campaña 2 zonas
        assertEquals(2, zonaCampanhas.size());

        List<LineaCampanha> lineaCampanhas1 = zonaCampanhas.get(0).getLineaCampanhas();

        // Por cada zona se crean 3 lineas que estan habilitadas
        assertEquals(3, lineaCampanhas1.size());

        assertEquals(6, tareasRepository.findAll().size());
//        assertEquals(6, tareasRepository.findByHoraSalidaNullAndHoraEntradaNotNull().size());

    }

    @Test
    void comenzarPoda() throws InstanceNotFoundException, DuplicateInstanceException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        List<Campanha> campanhas = campanhaRepository.findAll();
        assertEquals(1, campanhas.size());
        Campanha campanha = campanhas.get(0);

        assertEquals(Fase.PODA, campanha.getFaseCamp());

    }

    @Test
    void comenzarRecoleccion() throws InstanceNotFoundException, DuplicateInstanceException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        List<Campanha> campanhas = campanhaRepository.findAll();
        assertEquals(1, campanhas.size());
        Campanha campanha = campanhas.get(0);

        assertEquals(Fase.RECOLECCION_CARGA, campanha.getFaseCamp());

    }
    @Test
    void tareaCargaTractorNull() throws InstanceNotFoundException, DuplicateInstanceException, PermissionException, TaskAlreadyStartedException, TaskAlreadyEndedException, TaskNotStartedException, InvalidChecksException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        //Obtengo las tareas que hay que son todas de tipo de tarea RECOLECCION
        List<Tarea> tareas  = tareasRepository.findAll();

        // Una tarea para la prueba
        Tarea tareaRecoleccion = tareas.get(0);

        // Trabajadores para realizar la prueba
        List<Trabajador> trabajadoresDisponibles = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        List<Long> idTrabajadores = new ArrayList<>();
        idTrabajadores.add(trabajadoresDisponibles.get(0).getId());
        idTrabajadores.add(trabajadoresDisponibles.get(1).getId());

        //Inicio y finalizo la tarea
        campanhaService.comenzarTarea(idTrabajadores,  tareaRecoleccion.getId(),1L,1L);
        List<Tarea> tareasEnCurso = campanhaService.mostrarTareasSinFinalizar();
        campanhaService.pararTarea(tareasEnCurso.get(0).getId(), "Tarea Finalizada",100,false  );

        // Comprueba que despues de finalizar una tarea al 100 de RECOLECCION se genera una tarea de CARGA
        List<Tarea> tareasSinIniciarDeCarga = campanhaService.mostrarTareasPendientesDeCarga();

//        assertEquals("Tarea Finalizada", finalizadas.get(0).getComentarios());
        Long idTareaCarga = tareasSinIniciarDeCarga.get(tareasSinIniciarDeCarga.size()-1).getId();

        assertThrows(InstanceNotFoundException.class , ()->campanhaService.comenzarTarea(idTrabajadores,idTareaCarga,1L,null));
    }
    @Test
    void tareaCargaTractorNotFound() throws InstanceNotFoundException, DuplicateInstanceException, PermissionException, TaskAlreadyStartedException, TaskAlreadyEndedException, TaskNotStartedException, InvalidChecksException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        //Obtengo las tareas que hay que son todas de tipo de tarea RECOLECCION
        List<Tarea> tareas  = tareasRepository.findAll();

        // Una tarea para la prueba
        Tarea tareaRecoleccion = tareas.get(0);

        // Trabajadores para realizar la prueba
        List<Trabajador> trabajadoresDisponibles = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        List<Long> idTrabajadores = new ArrayList<>();
        idTrabajadores.add(trabajadoresDisponibles.get(0).getId());
        idTrabajadores.add(trabajadoresDisponibles.get(1).getId());

        //Inicio y finalizo la tarea
        campanhaService.comenzarTarea(idTrabajadores,  tareaRecoleccion.getId(),1L,1L);
        List<Tarea> tareasEnCurso = campanhaService.mostrarTareasSinFinalizar();
        campanhaService.pararTarea(tareasEnCurso.get(0).getId(), "Tarea Finalizada",100,false  );

        // Comprueba que despues de finalizar una tarea al 100 de RECOLECCION se genera una tarea de CARGA
        List<Tarea> tareasSinIniciar = campanhaService.mostrarTareasPendientes();

//        assertEquals("Tarea Finalizada", finalizadas.get(0).getComentarios());
        Long idTareaCarga = tareasSinIniciar.get(tareasSinIniciar.size()-1).getId();

        assertThrows(InstanceNotFoundException.class , ()->campanhaService.comenzarTarea(idTrabajadores,idTareaCarga,1L,10L));
    }
    @Test
    void tareaCargaTractorSinCompletar() throws InstanceNotFoundException, DuplicateInstanceException, PermissionException, TaskAlreadyStartedException, TaskAlreadyEndedException, TaskNotStartedException, InvalidChecksException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        //Obtengo las tareas que hay que son todas de tipo de tarea RECOLECCION
        List<Tarea> tareas  = tareasRepository.findAll();

        // Una tarea para la prueba
        Tarea tareaRecoleccion = tareas.get(0);

        // Trabajadores para realizar la prueba
        List<Trabajador> trabajadoresDisponibles = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        List<Long> idTrabajadores = new ArrayList<>();
        idTrabajadores.add(trabajadoresDisponibles.get(0).getId());
        idTrabajadores.add(trabajadoresDisponibles.get(1).getId());

        //Inicio y finalizo la tarea con la peticion de carga
        campanhaService.comenzarTarea(idTrabajadores,  tareaRecoleccion.getId(),1L,1L);
        List<Tarea> tareasEnCurso = campanhaService.mostrarTareasSinFinalizar();
        campanhaService.pararTarea(tareasEnCurso.get(0).getId(), "Tarea Finalizada",50,true  );

        // Se genera una tarea de CARGA
        List<Tarea> finalizadas = campanhaService.mostrarTareasFinalizadas();
        List<Tarea> tareasSinIniciar = campanhaService.mostrarTareasPendientes();

        assertEquals("Tarea Finalizada", finalizadas.get(0).getComentarios());

        // Comprobamos que se crea una tarea de carga
        List<Tarea> tareasDeCarga = tareasRepository.tareasSinIniciarDeCarga();
        assertEquals(1, tareasDeCarga.size());

        // De esa tarea la iniciamos
        campanhaService.comenzarTarea(idTrabajadores,tareasDeCarga.get(0).getId(),1L, 1L);

        // La finalizamos a medio trabajo por completar
        campanhaService.pararTarea(tareasDeCarga.get(0).getId(),"Comentario",80,false);

        List<Tarea> tareasDeCargaDespues = tareasRepository.tareasSinIniciarDeCarga();
        // Tarea de carga finalizada
        assertEquals(0,tareasDeCargaDespues.size());

        // Tarea de recoleccion generada
        List<Tarea> tareasDeRecoleccionDespues = campanhaService.mostrarTareasPendientes();

        // Se genera una tarea de recoleccion
        assertEquals(tareasSinIniciar.size(),tareasDeRecoleccionDespues.size() );

    }
    @Test
    void tareaRecoleccionLineaRecolectada() throws InstanceNotFoundException, DuplicateInstanceException, PermissionException, TaskAlreadyStartedException, TaskAlreadyEndedException, TaskNotStartedException, InvalidChecksException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        //Obtengo las tareas que hay que son todas de tipo de tarea RECOLECCION
        List<Tarea> tareas  = tareasRepository.findAll();

        // Una tarea para la prueba
        Tarea tareaRecoleccion = tareas.get(0);

        // Trabajadores para realizar la prueba
        List<Trabajador> trabajadoresDisponibles = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        List<Long> idTrabajadores = new ArrayList<>();
        idTrabajadores.add(trabajadoresDisponibles.get(0).getId());
        idTrabajadores.add(trabajadoresDisponibles.get(1).getId());

        //Inicio y finalizo la tarea
        campanhaService.comenzarTarea(idTrabajadores,  tareaRecoleccion.getId(),1L,1L);
        List<Tarea> tareasEnCurso = campanhaService.mostrarTareasSinFinalizar();
        campanhaService.pararTarea(tareasEnCurso.get(0).getId(), "Tarea Finalizada",100,false  );

        // Comprueba que despues de finalizar una tarea al 100 de RECOLECCION se genera una tarea de CARGA
        List<Tarea> finalizadas = campanhaService.mostrarTareasFinalizadas();

        assertEquals("Tarea Finalizada", finalizadas.get(0).getComentarios());

        // Comprobamos que se crea una tarea de carga
        assertEquals(1,tareasRepository.tareasSinIniciarDeCarga().size());


    }
    @Test
    void tareaRecoleccionLineaOrdenCarga() throws InstanceNotFoundException, DuplicateInstanceException, PermissionException, TaskAlreadyStartedException, TaskAlreadyEndedException, TaskNotStartedException, InvalidChecksException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        //Obtengo las tareas que hay que son todas de tipo de tarea RECOLECCION
        List<Tarea> tareas  = tareasRepository.findAll();

        // Una tarea para la prueba
        Tarea tareaRecoleccion = tareas.get(0);

        // Trabajadores para realizar la prueba
        List<Trabajador> trabajadoresDisponibles = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        List<Long> idTrabajadores = new ArrayList<>();
        idTrabajadores.add(trabajadoresDisponibles.get(0).getId());
        idTrabajadores.add(trabajadoresDisponibles.get(1).getId());

        //Inicio y finalizo la tarea
        campanhaService.comenzarTarea(idTrabajadores,  tareaRecoleccion.getId(),1L,1L);
        List<Tarea> tareasEnCurso = campanhaService.mostrarTareasSinFinalizar();
        campanhaService.pararTarea(tareasEnCurso.get(0).getId(), "Tarea Finalizada",50,true  );

        // Comprueba que despues de finalizar una tarea al 100 de RECOLECCION se genera una tarea de CARGA
        List<Tarea> finalizadas = campanhaService.mostrarTareasFinalizadas();

        // Se crea una tarea de carga  y otra de recoleccion, es decir hay una tarea mas en las tareas pendientes
        assertEquals(1,tareasRepository.tareasSinIniciarDeCarga().size());

        assertEquals("Tarea Finalizada", finalizadas.get(0).getComentarios());

    }

    @Test
    void finalizarCampanha() throws InstanceNotFoundException, DuplicateInstanceException {
        campanhaService.comenzarCampanha();

        campanhaService.comenzarPoda();

        campanhaService.comenzarRecoleccion();

        campanhaService.finalizarCampanha();

        List<Campanha> campanhas = campanhaRepository.findAll();
        assertEquals(1, campanhas.size());
        Campanha campanha = campanhas.get(0);

        assertNotNull(campanha.getFinalizacion());


    }

    @Test
    void transicionDeFasesTest() throws DuplicateInstanceException, InstanceNotFoundException {
        //Probamos que las transiciones sin tocarse se crean las tareas de cada una de las fases de campaña

        campanhaService.comenzarCampanha();
        campanhaService.mostrarTareasPendientes().forEach(elemento -> assertEquals(TipoTrabajo.LIMPIEZA, elemento.getTipoTrabajo()));

        assertEquals(6, campanhaService.mostrarTareasPendientes().size());

        campanhaService.comenzarPoda();

        assertEquals(6, campanhaService.mostrarTareasPendientes().size());
        assertEquals(TipoTrabajo.PODA, campanhaService.mostrarTareasPendientes().get(1).getTipoTrabajo());

        campanhaService.mostrarTareasPendientes().forEach(elemento -> assertEquals(TipoTrabajo.PODA, elemento.getTipoTrabajo()));

        campanhaService.comenzarRecoleccion();

        assertEquals(6, campanhaService.mostrarTareasPendientes().size());
        campanhaService.mostrarTareasPendientes().forEach(elemento -> assertEquals(TipoTrabajo.RECOLECCION, elemento.getTipoTrabajo()));

        campanhaService.finalizarCampanha();

        assertEquals(0, campanhaService.mostrarTareasPendientes().size());


    }

    @Test
    void comenzarTareaTest() throws DuplicateInstanceException, InstanceNotFoundException, TaskAlreadyStartedException, PermissionException {
        campanhaService.comenzarCampanha();

//        List<Trabajador> trabajadores =trabajadorService.obtenerTrabajadoresDisponiblesAhora();
        List<Trabajador> trabajadores = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        List<Long> idsTrabajadores = new ArrayList<>();

        trabajadores.forEach(t -> idsTrabajadores.add(t.getId()));

        List<Tarea> tareasEnBd = tareasRepository.findAll();

        campanhaService.comenzarTarea(idsTrabajadores, tareasEnBd.get(0).getId(), 1L, null);

        assertEquals(tareasEnBd.get(0).getId(), campanhaService.mostrarTareasSinFinalizar().get(0).getId());
    }

    @Test
    void comenzarTareaTaskAlreadyStarted() throws DuplicateInstanceException, InstanceNotFoundException, TaskAlreadyStartedException, PermissionException {
        campanhaService.comenzarCampanha();

        List<Long> idsTrabajadores = new ArrayList<>();
        List<Long> idsTrabajadores2 = new ArrayList<>();


        List<Tarea> tareasEnBd = tareasRepository.findAll();

        List<Trabajador> trabajadorsEnBd = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());

        idsTrabajadores.add(trabajadorsEnBd.get(0).getId());


        campanhaService.comenzarTarea(idsTrabajadores, tareasEnBd.get(0).getId(), 1L, null);

        idsTrabajadores2.add(trabajadorsEnBd.get(1).getId());
        assertThrows(TaskAlreadyStartedException.class, () -> campanhaService.comenzarTarea(idsTrabajadores2, tareasEnBd.get(0).getId(), 1L, null));
    }

    @Test
    void comenzarTareaInstanceNotFoundTest() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();

        List<Trabajador> trabajadores = trabajadorRepository.findAll();
        List<Long> idsTrabajadores = new ArrayList<>();

        trabajadores.forEach(t -> idsTrabajadores.add(t.getId()));


        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarTarea(idsTrabajadores, 355L, 1L, null));

    }

    @Test
    void finalizarTareaTest() throws DuplicateInstanceException, InstanceNotFoundException, TaskAlreadyStartedException, TaskAlreadyEndedException, InvalidChecksException, TaskNotStartedException, PermissionException {
        campanhaService.comenzarCampanha();

        List<Trabajador> trabajadores = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        List<Long> idsTrabajadores = new ArrayList<>();

        trabajadores.forEach(t -> idsTrabajadores.add(t.getId()));

        List<Tarea> tareasEnBd = tareasRepository.findAll();
        List<Trabajador> trabajadorsEnBd = trabajadorRepository.findAll();

        campanhaService.comenzarTarea(idsTrabajadores, tareasEnBd.get(0).getId(), trabajadorsEnBd.get(0).getId(), null);

        campanhaService.pararTarea(tareasEnBd.get(0).getId(), "Nuevo comentario", 40, false);


        assertEquals(7, tareasRepository.findAll().size());
        assertEquals(6, tareasRepository.tareasSinIniciar().size());
        assertEquals(0, tareasRepository.tareasEnProgreso().size());

    }

    @Test
    void finalizarTareaTest2() throws DuplicateInstanceException, InstanceNotFoundException, TaskAlreadyStartedException, TaskAlreadyEndedException, InvalidChecksException, TaskNotStartedException, PermissionException {
        campanhaService.comenzarCampanha();

        List<Trabajador> trabajadores = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        List<Long> idsTrabajadores = new ArrayList<>();

        trabajadores.forEach(t -> idsTrabajadores.add(t.getId()));

        List<Tarea> tareasEnBd = tareasRepository.findAll();
        List<Empleado> empleadosEnBd = empleadoRepository.findAll();

        campanhaService.comenzarTarea(idsTrabajadores, tareasEnBd.get(0).getId(), empleadosEnBd.get(0).getId(), null);

        campanhaService.pararTarea(tareasEnBd.get(0).getId(), "Nuevo comentario", 40, false);

        List<Tarea> tareasFinalizadas = campanhaService.mostrarTareasFinalizadas();

        assertEquals(1, tareasFinalizadas.size());


        assertEquals(7, tareasRepository.findAll().size());
        assertEquals(6, tareasRepository.tareasSinIniciar().size());
        assertEquals(0, tareasRepository.tareasEnProgreso().size());

    }

    @Test
    void finalizarTareaInstanceNotFoundException() throws DuplicateInstanceException, InstanceNotFoundException, TaskAlreadyStartedException, PermissionException {
        campanhaService.comenzarCampanha();

        List<Trabajador> trabajadores = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());

        List<Long> idsTrabajadores = new ArrayList<>();

        trabajadores.forEach(t -> idsTrabajadores.add(t.getId()));

        List<Tarea> tareasEnBd = tareasRepository.findAll();
        List<Empleado> empleadosEnBd = empleadoRepository.findAll();


        campanhaService.comenzarTarea(idsTrabajadores, tareasEnBd.get(0).getId(), empleadosEnBd.get(0).getId(), null);

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.pararTarea(400L, "Nuevo comentario", 40, false));


    }

    @Test
    void finalizarPararTareaAcabadaExceptionTest() throws DuplicateInstanceException, InstanceNotFoundException, TaskAlreadyStartedException, TaskAlreadyEndedException, InvalidChecksException, TaskNotStartedException, PermissionException {
        campanhaService.comenzarCampanha();

        List<Trabajador> trabajadores = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());

        List<Long> idsTrabajadores = new ArrayList<>();

        trabajadores.forEach(t -> idsTrabajadores.add(t.getId()));

        List<Tarea> tareasEnBd = tareasRepository.findAll();
        List<Empleado> empleadosEnBd = empleadoRepository.findAll();
        campanhaService.comenzarTarea(idsTrabajadores, tareasEnBd.get(0).getId(), empleadosEnBd.get(0).getId(), null);

        campanhaService.pararTarea(tareasEnBd.get(0).getId(), "Nuevo comentario", 40, false);

        assertThrows(TaskAlreadyEndedException.class, () ->
                campanhaService.pararTarea(tareasEnBd.get(0).getId(), "Nuevo comentario", 50, false)
        );


    }

    @Test
    void finalizarTareaWrongPercentajeExceptionTest() throws DuplicateInstanceException, InstanceNotFoundException, TaskAlreadyStartedException, TaskAlreadyEndedException, InvalidChecksException, TaskNotStartedException, PermissionException {
        campanhaService.comenzarCampanha();

        List<Trabajador> trabajadores = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());

        List<Long> idsTrabajadores = new ArrayList<>();

        trabajadores.forEach(t -> idsTrabajadores.add(t.getId()));

        List<Tarea> tareasEnBd = tareasRepository.findAll();
        List<Trabajador> trabajadorsEnBd = trabajadorRepository.findAll();
        List<Empleado> empleadosEnBd = empleadoRepository.findAll();

        campanhaService.comenzarTarea(idsTrabajadores, tareasEnBd.get(0).getId(), empleadosEnBd.get(0).getId(), null);

        campanhaService.pararTarea(tareasEnBd.get(0).getId(), "Nuevo comentario", 40, false);

        List<Tarea> tareasEnBd2 = tareasRepository.findAll();

        campanhaService.comenzarTarea(idsTrabajadores, tareasEnBd2.get(tareasEnBd2.size() - 1).getId(), trabajadorsEnBd.get(0).getId(), null);

        assertThrows(InvalidChecksException.class, () ->
                campanhaService.pararTarea(tareasEnBd2.get(6).getId(), "Nuevo comentario", 20, false)
        );


    }

    @Test
    void finalizarCampanhaException() {

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.finalizarCampanha());
    }

    @Test
    void finalizarCampanhaFaseException() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        assertThrows(InstanceNotFoundException.class, () -> campanhaService.finalizarCampanha());
    }

    @Test
    void comenzarRecoleccionException() {

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarRecoleccion());
    }

    @Test
    void comenzarRecoleccionFaseException() throws DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarRecoleccion());
    }

    @Test
    void comenzarPodaException() {

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarPoda());
    }

    @Test
    void comenzarPodaFaseException() throws InstanceNotFoundException, DuplicateInstanceException {
        campanhaService.comenzarCampanha();
        campanhaService.comenzarPoda();

        assertThrows(InstanceNotFoundException.class, () -> campanhaService.comenzarPoda());
    }


}