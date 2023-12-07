package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Linea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Numero de linea dentro de la zona
    private int lineNumber;

    private LocalDate plantingDate;

    private boolean harvestEnabled;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipoVid_id", nullable = false)
    private TipoVid tipoVid;


    @ManyToOne
    @JoinColumn(name = "zona_id", nullable = false)
    private Zona zona;
}
