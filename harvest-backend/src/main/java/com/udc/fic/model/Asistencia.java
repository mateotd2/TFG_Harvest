package com.udc.fic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@EntityScan
public class Asistencia {
    private String name;
    private String lastname;
    private LocalDate daywork;
    private LocalTime checkin;
    private LocalTime checkout;
    private Boolean attendance;
    private Long id; // id de Disponibilidad
}
