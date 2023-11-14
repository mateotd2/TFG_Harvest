package com.udc.fic.services;

import com.udc.fic.model.Disponibilidad;
import com.udc.fic.model.ElementoListaDisponibilidad;
import com.udc.fic.model.Trabajador;
import com.udc.fic.repository.DisponibilidadRepository;
import com.udc.fic.repository.TrabajadorRepository;
import com.udc.fic.services.exceptions.DuplicateInstanceException;
import com.udc.fic.services.exceptions.WorkerNotAvailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrabajadorServiceImplTest {


    @InjectMocks
    TrabajadorServiceImpl trabajadorService;

    @Mock
    TrabajadorRepository trabajadorRepository;


    @Mock
    DisponibilidadRepository disponibilidadRepository;


    @Test
    void obtenerTrabajadorTest() throws InstanceNotFoundException {
        LocalDate birthdate = LocalDate.now();
        Trabajador trabajador = new Trabajador(1L, "test", "test", "12345678Q", "123456789012", "666666666", birthdate, "address", true, null);

        Optional<Trabajador> res = Optional.of(trabajador);
        when(trabajadorRepository.findById(1L)).thenReturn(res);

        assertEquals("12345678Q", trabajadorService.obtenerTrabajador(1L).getDni());
    }

    @Test
    void actualizarTrabajadorInstanceNotFoundTest() {
        LocalDate birthdate = LocalDate.now();
        Trabajador trabajador = new Trabajador(1L, "test", "test", "12345678Q", "123456789012", "666666666", birthdate, "address", true, null);

        Optional<Trabajador> res = Optional.empty();

        when(trabajadorRepository.findById(1L)).thenReturn(res);
        assertThrows(InstanceNotFoundException.class, () -> trabajadorService.actualizarTrabajador(trabajador));
    }

    @Test
    void actualizarTrabajadorNotAvailableInstanceNotFoundTest() {
        LocalDate birthdate = LocalDate.now();
        Trabajador trabajador = new Trabajador(1L, "test", "test", "12345678Q", "123456789012", "666666666", birthdate, "address", false, null);

        Optional<Trabajador> res = Optional.of(trabajador);

        when(trabajadorRepository.findById(1L)).thenReturn(res);
        // Aunque buque el mismo trabajador con available true, va a fallar, ya que el que devuelve el repositorio no esta disponible
        assertThrows(InstanceNotFoundException.class, () -> trabajadorService.actualizarTrabajador(new Trabajador(1L, "test", "test", "12345678Q", "123456789012", "666666666", birthdate, "address", true, null)));
    }

    @Test
    void bajaTrabajadorNotAvailableTest() {
        LocalDate birthdate = LocalDate.now();
        Trabajador trabajador = new Trabajador(1L, "test", "test", "12345678Q", "123456789012", "666666666", birthdate, "address", false, null);

        Optional<Trabajador> res = Optional.of(trabajador);
        when(trabajadorRepository.findById(1L)).thenReturn(res);
        assertThrows(InstanceNotFoundException.class, () -> trabajadorService.bajaTrabajador(1L));

    }

    @Test
    void bajaTrabajadorNotFoundTest() {
        LocalDate birthdate = LocalDate.now();

        Optional<Trabajador> res = Optional.empty();
        when(trabajadorRepository.findById(1L)).thenReturn(res);
        assertThrows(InstanceNotFoundException.class, () -> trabajadorService.bajaTrabajador(1L));

    }

    @Test
    void obtenerTrabajadorNotFoundTest() throws InstanceNotFoundException {

        Optional<Trabajador> res = Optional.empty();
        when(trabajadorRepository.findById(1L)).thenReturn(res);

        assertThrows(InstanceNotFoundException.class, () -> trabajadorService.obtenerTrabajador(1L));
    }

    @Test
    void registrarTrabajadorTest() throws DuplicateInstanceException {
        LocalDate birthdate = LocalDate.now();
        Trabajador trabajador = new Trabajador(1L, "test", "test", "12345678Q", "123456789012", "666666666", birthdate, "address", true, null);
        when(trabajadorRepository.existsByDniOrNss(trabajador.getDni(), trabajador.getNss())).thenReturn(false);
        when(trabajadorRepository.save(trabajador)).thenReturn(trabajador);
        assertEquals("12345678Q", trabajadorService.registrarTrabajador(trabajador).getDni());
    }

    @Test
    void registrarTrabajadorDuplicateExceptionTest() {
        LocalDate birthdate = LocalDate.now();
        Trabajador trabajador = new Trabajador(1L, "test", "test", "12345678Q", "123456789012", "666666666", birthdate, "address", true, null);
        when(trabajadorRepository.existsByDniOrNss(trabajador.getDni(), trabajador.getNss())).thenReturn(true);

        assertThrows(DuplicateInstanceException.class, () -> trabajadorService.registrarTrabajador(trabajador));

    }

    @Test
    void pasarListaInstaneNotFoundTest() {
        List<ElementoListaDisponibilidad> elementos = new ArrayList<>();
        elementos.add(new ElementoListaDisponibilidad(1L, LocalTime.now(), LocalTime.now()));
        when(disponibilidadRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(InstanceNotFoundException.class, () -> trabajadorService.pasarLista(elementos));
    }


    @Test
    void obtenerCalendario() throws InstanceNotFoundException {
        List<Disponibilidad> calendario = new ArrayList<>();
        Trabajador trabajador = new Trabajador(1L, "name", "lastname", "12345678A", "123456789012", "666666666", LocalDate.of(1990, 1, 1), "13 Rua del Percebe", true, calendario);
        calendario.add(new Disponibilidad(1L, LocalDate.of(2023, 1, 1), LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), trabajador, false));
        calendario.add(new Disponibilidad(1L, LocalDate.of(2023, 1, 2), LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), trabajador, false));
        calendario.add(new Disponibilidad(1L, LocalDate.of(2023, 1, 3), LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), trabajador, false));
        calendario.add(new Disponibilidad(1L, LocalDate.of(2023, 1, 4), LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), trabajador, false));

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));

        assertEquals(calendario, trabajadorService.obtenerCalendario(1L));

    }

    @Test
    void obtenerCalendarioInstanceNotFound() throws InstanceNotFoundException {

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> trabajadorService.obtenerCalendario(1L));
    }

    @Test
    void actualizarCalendarioInstanceNotFoundException() {
        when(trabajadorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InstanceNotFoundException.class, () -> trabajadorService.actualizarCalendario(1L, new ArrayList<>()));
    }

    @Test
    void actualizarCalendarioWorkerNotAvailableException() {
        List<Disponibilidad> calendario = new ArrayList<>();
        Trabajador trabajador = new Trabajador(1L, "name", "lastname", "12345678A", "123456789012", "666666666", LocalDate.of(1990, 1, 1), "13 Rua del Percebe", false, calendario);
        calendario.add(new Disponibilidad(1L, LocalDate.of(2023, 1, 1), LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), trabajador, false));
        calendario.add(new Disponibilidad(1L, LocalDate.of(2023, 1, 2), LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), trabajador, false));
        calendario.add(new Disponibilidad(1L, LocalDate.of(2023, 1, 3), LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), trabajador, false));
        calendario.add(new Disponibilidad(1L, LocalDate.of(2023, 1, 4), LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), trabajador, false));

        when(trabajadorRepository.findById(1L)).thenReturn(Optional.of(trabajador));
        assertThrows(WorkerNotAvailableException.class, () -> trabajadorService.actualizarCalendario(1L, new ArrayList<>()));
    }


}
