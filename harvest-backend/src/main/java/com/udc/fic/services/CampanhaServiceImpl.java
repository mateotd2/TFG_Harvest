package com.udc.fic.services;

import com.udc.fic.model.*;
import com.udc.fic.repository.*;
import com.udc.fic.services.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CampanhaServiceImpl implements CampanhaService {

    // Valor boolean para notificar de la existencia de nuevas tareas de Carga
    private boolean notificacionMasTareas = false;

    private static final String NO_COMENTS = "Sin comentarios";

    private static final Logger LOGGER = LoggerFactory.getLogger(CampanhaServiceImpl.class);
    @Autowired
    CampanhaRepository campanhaRepository;
    @Autowired
    ZonasRepository zonasRepository;
    @Autowired
    TareasRepository tareasRepository;
    @Autowired
    TrabajadorRepository trabajadorRepository;
    @Autowired
    EmpleadoRepository empleadoRepository;

    @Autowired
    TractorRepository tractorRepository;
    @Autowired
    private PermissionChecker permissionChecker;

    private void inicializarTaresPorFase(TipoTrabajo tipoTrabajo, List<ZonaCampanha> zonaCampanhas) {
        zonaCampanhas.forEach(z -> {
            List<LineaCampanha> lineaCampanhas = z.getLineaCampanhas();
            lineaCampanhas.forEach(l -> {
                Tarea tarea = new Tarea();
                tarea.setLineaCampanha(l);
                tarea.setTipoTrabajo(tipoTrabajo);
                tarea.setComentarios(NO_COMENTS);

                l.getTareas().add(tarea);
            });
        });
    }


    private ZonaCampanha crearZonaCampanha(Zona zona, Campanha campanha) {
        ZonaCampanha zonaCampanha = new ZonaCampanha();
        zonaCampanha.setCampanha(campanha);
        zonaCampanha.setZona(zona);


        List<Linea> lineas = zona.getLineas();

        List<LineaCampanha> lineaCampanhas = new ArrayList<>();

        // Cada linea se comprueba si esta habilitada para su recolección y se añade a la campaña
        lineas.forEach(e ->
                {
                    if (e.isHarvestEnabled()) {
                        LineaCampanha lineaNueva = new LineaCampanha();
                        lineaNueva.setEstado(Estado.PAUSADO);
                        lineaNueva.setPorcentajeTrabajado(0);
                        lineaNueva.setCargaLista(false);
                        lineaNueva.setZonaCampanha(zonaCampanha);
                        lineaNueva.setLinea(e);

                        // Se inicializa una tarea por cada linea

                        Tarea tarea = new Tarea();
                        tarea.setLineaCampanha(lineaNueva);
                        tarea.setTipoTrabajo(TipoTrabajo.LIMPIEZA);
                        tarea.setComentarios(NO_COMENTS);
                        List<Tarea> tareas = new ArrayList<>();
                        tareas.add(tarea);
                        lineaNueva.setTareas(tareas);
                        lineaCampanhas.add(lineaNueva);
                    }
                }
        );

        zonaCampanha.setLineaCampanhas(lineaCampanhas);


        return zonaCampanha;
    }

    private void restaurarLineasCampanha(Campanha campanha) {
        List<ZonaCampanha> zonaCampanhas = campanha.getZonaCampanhas();

        zonaCampanhas.forEach(
                zonaCampanha -> zonaCampanha.getLineaCampanhas().forEach(
                        lineaCampanha -> {
                            lineaCampanha.setEstado(Estado.PAUSADO);
                            lineaCampanha.setPorcentajeTrabajado(0);
                        }
                )
        );
    }


    private void limpiarTareasPendientes() {
        //Tareas sin comenzar que al pasar de fase se eliminan
        List<Tarea> tareasALimpiar = tareasRepository.tareasSinIniciar();

        tareasRepository.deleteAllInBatch(tareasALimpiar);
    }

    @Override
    public boolean notificacionTareasCarga() {
        boolean nuevaNotificaciones = this.notificacionMasTareas;
        this.notificacionMasTareas = false;

        return nuevaNotificaciones;
    }

    @Override
    public void comenzarCampanha() throws DuplicateInstanceException {
        int ano = LocalDateTime.now().getYear();
        if (!campanhaRepository.existsByAno(ano)) {
            LOGGER.info("Comenzando campaña del año {}", ano);
            Campanha campanha = new Campanha();

            List<ZonaCampanha> zonasCampanha = new ArrayList<>();
            List<Zona> zonas = zonasRepository.findAll();

            // Crea una zonaCampanha por cada zona registrada
            zonas.forEach(z -> {
                ZonaCampanha zonaCampanha = crearZonaCampanha(z, campanha);
                zonasCampanha.add(zonaCampanha);
            });

            campanha.setZonaCampanhas(zonasCampanha);
            campanha.setAno(ano);
            campanha.setInicio(LocalDate.now());
            campanha.setFaseCamp(Fase.LIMPIEZA);

            campanhaRepository.save(campanha);


        } else {
            throw new DuplicateInstanceException("La campaña anual ya esta iniciada", ano);
        }

    }

    @Override
    public void comenzarPoda() throws InstanceNotFoundException {
        int ano = LocalDateTime.now().getYear();
        Optional<Campanha> campanhaOptional = campanhaRepository.findByAno(ano);

        if (campanhaOptional.isPresent()) {

            Campanha campanha = campanhaOptional.get();
            // Comprobar que se paso por la fase de limpieza
            if (!campanha.getFaseCamp().equals(Fase.LIMPIEZA)) {
                throw new InstanceNotFoundException();
            }

            restaurarLineasCampanha(campanha);

            campanha.setFaseCamp(Fase.PODA);
            limpiarTareasPendientes();
            inicializarTaresPorFase(TipoTrabajo.PODA, campanha.getZonaCampanhas());

            campanhaRepository.save(campanha);

        } else {
            throw new InstanceNotFoundException();
        }

    }

    @Override
    public void comenzarRecoleccion() throws InstanceNotFoundException {
        int ano = LocalDateTime.now().getYear();
        Optional<Campanha> campanhaOptional = campanhaRepository.findByAno(ano);

        if (campanhaOptional.isPresent()) {

            Campanha campanha = campanhaOptional.get();
            // Comprobar que se paso por la fase de Poda
            if (!campanha.getFaseCamp().equals(Fase.PODA)) {
                throw new InstanceNotFoundException();
            }
            campanha.setFaseCamp(Fase.RECOLECCION_CARGA);

            restaurarLineasCampanha(campanha);
            limpiarTareasPendientes();
            inicializarTaresPorFase(TipoTrabajo.RECOLECCION, campanha.getZonaCampanhas());


            campanhaRepository.save(campanha);

        } else {
            throw new InstanceNotFoundException();
        }

    }

    @Override
    public void finalizarCampanha() throws InstanceNotFoundException {
        int ano = LocalDateTime.now().getYear();
        Optional<Campanha> campanhaOptional = campanhaRepository.findByAno(ano);

        if (campanhaOptional.isPresent()) {
            Campanha campanha = campanhaOptional.get();
            // Comprobar que se paso por la fase de recoleccion
            if (!campanha.getFaseCamp().equals(Fase.RECOLECCION_CARGA)) {
                throw new InstanceNotFoundException();
            }
            campanha.setFinalizacion(LocalDate.now());
            campanha.setFaseCamp(Fase.FINALIZADA);
            limpiarTareasPendientes();
            campanhaRepository.save(campanha);

        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public List<Tarea> mostrarTareasPendientes() {
        return tareasRepository.tareasSinIniciar();
    }

    @Override
    public List<Tarea> mostrarTareasPendientesDeCarga() {
        return tareasRepository.tareasSinIniciarDeCarga();
    }


    @Override
    public List<Tarea> mostrarTareasSinFinalizar() {
        return tareasRepository.tareasEnProgreso();
    }

    @Override
    public List<Tarea> mostrarTareasSinFinalizarDeCarga() {
        return tareasRepository.tareasEnProgresoDeCarga();
    }

    @Override
    public List<Tarea> mostrarTareasFinalizadas() {
        int ano = LocalDateTime.now().getYear();

        return tareasRepository.findTareasFinalizadasDeCampanha(ano);
    }

    @Override
    public List<Tarea> mostrarTareasFinalizadasDeCarga() {
        return tareasRepository.tareasFinalizadasDeCarga();
    }


    @Override
    public void comenzarTarea(List<Long> idsTrabajadores, Long idTarea, Long idEmpleado) throws InstanceNotFoundException, TaskAlreadyStartedException {

        Empleado empleado = permissionChecker.checkEmpleado(idEmpleado);
        Optional<Tarea> tareaOptional = tareasRepository.findById(idTarea);
        if (tareaOptional.isEmpty()) throw new InstanceNotFoundException();


        Tarea tarea = tareaOptional.get();
        if (tarea.getTipoTrabajo() == TipoTrabajo.CARGA) throw new InstanceNotFoundException();

        // Validacion por si ya empezo la tarea
        if (tarea.getHoraEntrada() != null) throw new TaskAlreadyStartedException();

        // Comprueba que los distintos trabajadores de idTrabajadores esten disponibles para la realizacion de tareas
        List<Trabajador> trabajadoresDisponiblesAhora = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
        if (!new HashSet<>(trabajadoresDisponiblesAhora.stream().map(Trabajador::getId).toList()).containsAll(idsTrabajadores)) {
            throw new InstanceNotFoundException();
        }

        tarea.setHoraEntrada(LocalDateTime.now());
        tarea.setEmpleado(empleado);
        List<Trabajador> trabajadores = new ArrayList<>();
        idsTrabajadores.forEach(id -> {
                    Trabajador trabajador = trabajadorRepository.findById(id).get();
                    trabajador.setInTask(true);
                    trabajadores.add(trabajador);
                    trabajadorRepository.save(trabajador);
                }
        );

        tarea.setTrabajadores(trabajadores);
        tareasRepository.save(tarea);

    }


    @Override
    public void pararTarea(Long idTarea, String comentarios, int porcentaje, boolean solicitudCarga) throws InstanceNotFoundException, InvalidChecksException, TaskAlreadyEndedException, TaskNotStartedException {
        Optional<Tarea> tareaOptional = tareasRepository.findById(idTarea);
        if (tareaOptional.isEmpty()) throw new InstanceNotFoundException();
        Tarea tarea = tareaOptional.get();
        if (tarea.getTipoTrabajo() == TipoTrabajo.CARGA) throw new InstanceNotFoundException();

        // Validacion de que son tareas de carga validas
        if (tarea.getHoraEntrada() == null) throw new TaskNotStartedException();
        if (tarea.getHoraSalida() != null) throw new TaskAlreadyEndedException();

        // Validacion deun porcentaje valido
        if (porcentaje < tarea.getLineaCampanha().getPorcentajeTrabajado()) throw new InvalidChecksException();

        // Tareas de recoleccion
        if (tarea.getTipoTrabajo() == TipoTrabajo.RECOLECCION && (solicitudCarga || porcentaje == 100)) {
            // Carga solicitada o porcentaje 100 para crear la tarea de carga de la linea
            Tarea tareaNueva = new Tarea();
            tareaNueva.setComentarios(NO_COMENTS);
            tareaNueva.setLineaCampanha(tarea.getLineaCampanha());
            tareaNueva.setTipoTrabajo(TipoTrabajo.CARGA);
            tareasRepository.save(tareaNueva);
            this.notificacionMasTareas = true;
        }
        // Para cualquier tipo de tarea si no lleva al 100% se crea otra tarea
        if (porcentaje < 100) {
            Tarea tareaNueva = new Tarea();
            tareaNueva.setComentarios(NO_COMENTS);
            tareaNueva.setLineaCampanha(tarea.getLineaCampanha());
            tareaNueva.setTipoTrabajo(tarea.getTipoTrabajo());
            tareasRepository.save(tareaNueva);
        }

        tarea.getLineaCampanha().setPorcentajeTrabajado(porcentaje);
        tarea.setHoraSalida(LocalDateTime.now());
        tarea.setComentarios(comentarios);

        tarea.getTrabajadores().forEach(trabajador ->
                trabajador.setInTask(false)
        );

        tareasRepository.save(tarea);


    }




    @Override
    public Tarea mostrarDetallesTarea(Long id) throws InstanceNotFoundException {
        Optional<Tarea> tareaOptional = tareasRepository.findById(id);
        if (tareaOptional.isPresent()) {
            return tareaOptional.get();
        } else {
            throw new InstanceNotFoundException();
        }

    }

    @Override
    public Fase mostrarFaseCampanha() {
        Optional<Campanha> optionalCampanha = campanhaRepository.findByAno(LocalDate.now().getYear());

        if (optionalCampanha.isPresent()) {
            Campanha campanha = optionalCampanha.get();
            return campanha.getFaseCamp();
        } else {
            return null;
        }

    }

    @Override
    public void comenzarTareasCarga(List<Long> idTareas, Long idEmpleado, Long idTractor, List<Long> idsTrabajadores) throws InstanceNotFoundException, PermissionException {
        // Empleado existe
        Empleado tractorista = permissionChecker.checkTractorista(idEmpleado);
        //
        if (idTractor == null) throw new InstanceNotFoundException();

        // Tareas son de tipo CARGA
        List<Tarea> tareas = tareasRepository.findByIdInAndHoraEntradaNullAndTipoTrabajo(idTareas, TipoTrabajo.CARGA);
        if (!new HashSet<>(tareas.stream().map(Tarea::getId).toList()).containsAll(idTareas))
            throw new InstanceNotFoundException();

        // Trabajadores disponibles
        if (!idsTrabajadores.isEmpty() && (!trabajadorRepository.existsByIdInAndInTaskFalse(idsTrabajadores))) {
            throw new InstanceNotFoundException();
        }
        List<Trabajador> trabajadores = trabajadorRepository.findAllById(idsTrabajadores);

        //Tractor existe
        Optional<Tractor> tractorOptional = tractorRepository.findById(idTractor);
        if (tractorOptional.isEmpty()) throw new InstanceNotFoundException();
        Tractor tractor = tractorOptional.get();
        // Comprueba que los ids esten en el conjunto de tareas de carga obtenidas del repositorio
        tareas.forEach(tarea -> {
            tarea.setTractor(tractor);
            tarea.setTrabajadores(trabajadores);
            tarea.setHoraEntrada(LocalDateTime.now());
            tarea.setEmpleado(tractorista);
        });

        tractor.setEnTarea(true);
        trabajadores.forEach(trabajador -> trabajador.setInTask(true));

        tareasRepository.saveAll(tareas);

    }

    @Override
    public void pararTareasCarga(List<Long> idTareas, String comentario) throws InstanceNotFoundException, InvalidChecksException {


        // Las tares de ids forman parte de las tareas en progreso
        List<Tarea> tareas = tareasRepository.findAllById(idTareas);
        Tractor tractor = tareas.get(0).getTractor();

        // Todas las tareas son del mismo tractor
        for (Tarea tarea : tareas) {
            if (tarea.getTractor() != tractor) throw new InvalidChecksException();
        }

//        List<Tarea> tareasEnProgresoDeCarga = tareasRepository.tareasEnProgresoDeCarga();
//        List<Tarea> tareasDeTractor = tractor.getTareas();
        List<Tarea> tareasDeTractor = tareasRepository.findByTractorAndEnProgreso(tractor.getId());

        if (!new HashSet<>(tareasDeTractor).containsAll(tareas)) throw new InstanceNotFoundException();

        // Tractor y tareas en tarea
        if (tareas.size() == tareasDeTractor.size()) {
            tractor.setEnTarea(false);
            List<Trabajador> trabajadores = tareas.get(0).getTrabajadores();
            trabajadores.forEach(trabajador -> trabajador.setInTask(false));
        }

        tareas.forEach(tarea -> {
//            tarea.setTractor(tractor);
            tarea.setHoraSalida(LocalDateTime.now());
            tarea.setComentarios(comentario);
        });


        tareasRepository.saveAll(tareas);
        tractorRepository.save(tractor);


    }

    @Override
    public List<Tractor> tractoresDisponibles() {
        return tractorRepository.findByEnTareaFalse();
    }
}
