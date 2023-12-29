package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime horaEntrada;

    private LocalDateTime horaSalida;

    @Column(nullable = false, length = 2096)
    private String comentarios;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTrabajo tipoTrabajo;

    @ManyToOne
    @JoinColumn(name = "linea_campanha_id", nullable = false)
    private LineaCampanha lineaCampanha;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @ManyToMany
    @JoinTable(
            name = "work",
            joinColumns = @JoinColumn(name = "tarea_id"),
            inverseJoinColumns = @JoinColumn(name = "trabajador_id")
    )
    private List<Trabajador> trabajadores;


}
