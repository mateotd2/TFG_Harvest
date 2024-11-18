package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@EntityScan("com.udc.fic.model")
@NoArgsConstructor
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate daywork;


    @Column(nullable = false, columnDefinition = "TIME")
    private LocalTime checkin;

    @Column(nullable = false, columnDefinition = "TIME")
    private LocalTime checkout;

    //    @ManyToOne(fetch = FetchType.EAGER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    private Trabajador trabajador;

    private boolean attendance = false;

    public Disponibilidad(Long id, LocalDate daywork, LocalTime checkin, LocalTime checkout, Trabajador trabajador, boolean attendance) {
        this.id = id;
        this.daywork = daywork;
        this.checkin = checkin;
        this.checkout = checkout;
        this.trabajador = new Trabajador(trabajador.getId(), trabajador.getName(), trabajador.getLastname(), trabajador.getDni(), trabajador.getNss(), trabajador.getPhone(), trabajador.getBirthdate(), trabajador.getAddress(), trabajador.isAvailable(), trabajador.getCalendario());
        this.attendance = attendance;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = new Trabajador(trabajador.getId(), trabajador.getName(), trabajador.getLastname(), trabajador.getDni(), trabajador.getNss(), trabajador.getPhone(), trabajador.getBirthdate(), trabajador.getAddress(), trabajador.isAvailable(), trabajador.getCalendario());
    }
}
