package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dia;

    @Column(nullable = false, columnDefinition = "TIME")
    private LocalTime horaEntrada;

    @Column(nullable = false, columnDefinition = "TIME")
    private LocalTime horaSalida;

    @ManyToOne
    @JoinColumn(name = "trabajador_id", nullable = false)
    private Trabajador trabajador;

}
