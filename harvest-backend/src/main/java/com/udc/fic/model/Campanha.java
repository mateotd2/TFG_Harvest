package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    LocalDate inicio;

    LocalDate finalizacion;

    @Column(nullable = false)
    int ano;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Fase faseCamp;

    //ONE TO MANY A ZONACAMPAÑA
}

