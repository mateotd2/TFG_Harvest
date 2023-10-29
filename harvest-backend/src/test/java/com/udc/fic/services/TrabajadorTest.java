package com.udc.fic.services;

import com.udc.fic.model.Trabajador;
import com.udc.fic.repository.DisponibilidadRepository;
import com.udc.fic.repository.TrabajadorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrabajadorTest {


    @InjectMocks
    TrabajadorServiceImpl trabajadorService;

    @Mock
    TrabajadorRepository trabajadorRepository;

    @Mock
    Logger logger;

    @Mock
    DisponibilidadRepository disponibilidadRepository;


    @Test
    void obtenerTrabajadorTest() throws InstanceNotFoundException {
        LocalDate birthdate = LocalDate.now();
        Trabajador trabajador = new Trabajador(1L,"test","test","12345678Q","123456789012","666666666",birthdate,"address",null);

        Optional<Trabajador> res = Optional.of(trabajador);
        when(trabajadorRepository.findById(1L)).thenReturn(res);

        assertEquals("12345678Q",trabajadorService.obtenerTrabajador(1L).getDni());
    }


//    @Test
//    void registrarTrabajadore(){
//
//        LocalDate birthdate = LocalDate.now();
//        Trabajador trabajador1 = new Trabajador(1L,"test1","test","12345678Q","123456789012","666666666",birthdate,"address",null);
//        Trabajador trabajador2 = new Trabajador(2L,"test2","test","12345678Q","123456789012","666666666",birthdate,"address",null);
//        Trabajador trabajador3 = new Trabajador(3L,"test3","test","12345678Q","123456789012","666666666",birthdate,"address",null);
//        Trabajador trabajador4 = new Trabajador(4L,"test4","test","12345678Q","123456789012","666666666",birthdate,"address",null);
//
//        List<Trabajador> trabajadores = new ArrayList<>();
//        trabajadores.add(trabajador1);
//        trabajadores.add(trabajador2);
//        trabajadores.add(trabajador3);
//        trabajadores.add(trabajador4);
//
//        when(trabajadorRepository.findAll()).thenReturn()
//    }


}
