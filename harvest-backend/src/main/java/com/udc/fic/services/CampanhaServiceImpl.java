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
        List<Tarea> tareasALimpiar = tareasRepository.findByHoraEntradaNull();

        tareasRepository.deleteAllInBatch(tareasALimpiar);
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

            limpiarTareasPendientes();
            campanhaRepository.save(campanha);

        } else {
            throw new InstanceNotFoundException();
        }
    }

    @Override
    public List<Tarea> mostrarTareasPendientes() {
        return tareasRepository.findByHoraEntradaNull();
    }

    @Override
    public List<Tarea> mostrarTareasSinFinalizar() {
        return tareasRepository.findByHoraSalidaNullAndHoraEntradaNotNull();
    }

    @Override
    public List<Tarea> mostrarTareasFinalizadas() {
        int ano = LocalDateTime.now().getYear();

        return tareasRepository.findTareasFinalizadasDeCampanha(ano);
    }

    @Override
    public void comenzarTarea(List<Long> idsTrabajadores, Long idTarea, Long idEmpleado, Long idTractor) throws InstanceNotFoundException, TaskAlreadyStartedException {
        permissionChecker.checkEmpleado(idEmpleado);
        Optional<Empleado> empleadoOptional = empleadoRepository.findById(idEmpleado);

        // TODO: si tengo id de tractor necesito conocer si el que empieza la tarea es tractorista y si el tractor existe

        if (empleadoOptional.isEmpty()) {
            throw new InstanceNotFoundException();
        }

        Optional<Tarea> tareaOptional = tareasRepository.findById(idTarea);
        if (tareaOptional.isEmpty()) {
            throw new InstanceNotFoundException();
        }


        if (!idsTrabajadores.isEmpty()) {
            List<Trabajador> trabajadoresDisponiblesAhora = trabajadorRepository.findDistinctTrabajadoresByDateAndAvailable(LocalDate.now(), LocalTime.now());
            if (!new HashSet<>(trabajadoresDisponiblesAhora.stream().map(Trabajador::getId).toList()).containsAll(idsTrabajadores)) {
                throw new InstanceNotFoundException();
            }
        }


        Tarea tarea = tareaOptional.get();
        // Validacion por si ya empezo la tarea
        if (tarea.getHoraEntrada() != null) {
            throw new TaskAlreadyStartedException();
        }
        tarea.setHoraEntrada(LocalDateTime.now());
        tarea.setEmpleado(empleadoOptional.get());
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
    public void pararTarea(Long idTarea, String comentarios, int porcentaje) throws InstanceNotFoundException, InvalidChecksException, TaskAlreadyEndedException, TaskNotStartedException {
        Optional<Tarea> tareaOptional = tareasRepository.findById(idTarea);

        if (tareaOptional.isPresent()) {
            {


                Tarea tarea = tareaOptional.get();


                // Validacion de que son tareas validas
                if (tarea.getHoraEntrada() == null) throw new TaskNotStartedException();
                if (tarea.getHoraSalida() != null) throw new TaskAlreadyEndedException();

                tarea.setHoraSalida(LocalDateTime.now());
                tarea.setComentarios(comentarios);


                //Validar que el porcentaje es mayor que en porcentaje actual
                if (porcentaje <= tarea.getLineaCampanha().getPorcentajeTrabajado()) {
                    throw new InvalidChecksException();
                }
                tarea.getLineaCampanha().setPorcentajeTrabajado(porcentaje);

                // Para tipo de tarea que no sea CARGA
                if (tarea.getTipoTrabajo() != TipoTrabajo.CARGA) {
                    if (porcentaje < 100) {
                        Tarea tareaNueva = new Tarea();
                        tareaNueva.setComentarios(NO_COMENTS);
                        tareaNueva.setLineaCampanha(tarea.getLineaCampanha());
                        tareaNueva.setTipoTrabajo(tarea.getTipoTrabajo());
                        tareasRepository.save(tareaNueva);
                    }
                    // Caso en el que la recoleccion finaliza en una linea
                    else {
                        if (tarea.getTipoTrabajo() == TipoTrabajo.RECOLECCION) {
                            Tarea tareaNueva = new Tarea();
                            tareaNueva.setComentarios(NO_COMENTS);
                            tareaNueva.setLineaCampanha(tarea.getLineaCampanha());
                            tareaNueva.setTipoTrabajo(TipoTrabajo.CARGA);
                            tareasRepository.save(tareaNueva);

                        }
                    }

                    // El true cambiarlo luego por el boolean que le pasare a la funcion
                    if (tarea.getTipoTrabajo() == TipoTrabajo.RECOLECCION) {
                        Tarea tareaNueva = new Tarea();
                        tareaNueva.setComentarios(NO_COMENTS);
                        tareaNueva.setLineaCampanha(tarea.getLineaCampanha());
                        tareaNueva.setTipoTrabajo(TipoTrabajo.CARGA);
                        tareasRepository.save(tareaNueva);
                    }
                }
                // Para las tareas de tipo CARGA las finalizo directamente si llegan al 100, si no, vuelve a crearse una tarea de RECOLECCION
                else {
                    if (tarea.getLineaCampanha().getPorcentajeTrabajado() < 100) {
                        Tarea tareaNueva = new Tarea();
                        tareaNueva.setComentarios(NO_COMENTS);
                        tareaNueva.setLineaCampanha(tarea.getLineaCampanha());
                        tareaNueva.setTipoTrabajo(TipoTrabajo.RECOLECCION);
                        tareasRepository.save(tareaNueva);
                    }
                }


                tarea.getTrabajadores().forEach(trabajador ->
                        trabajador.setInTask(false)
                );

                tareasRepository.save(tarea);
            }

        } else {
            throw new InstanceNotFoundException();
        }

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
}
