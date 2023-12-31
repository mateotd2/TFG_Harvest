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
public class LineaCampanha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(nullable = false)
    private int porcentajeTrabajado;

    @Column(nullable = false)
    private boolean cargaLista;

    private LocalDateTime finLimpieza;
    private LocalDateTime finPoda;
    private LocalDateTime finRecoleccion;
    private LocalDateTime finCarga;

    @ManyToOne
    @JoinColumn(name = "zona_campanha_id", nullable = false)
    private ZonaCampanha zonaCampanha;

    @ManyToOne
    @JoinColumn(name = "linea_id", nullable = false)
    private Linea linea;

    @OneToMany(mappedBy = "lineaCampanha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tarea> tareas;

}
