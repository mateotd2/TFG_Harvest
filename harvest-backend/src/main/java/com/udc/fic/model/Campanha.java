package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "ano")})
public class Campanha {

    @Column(nullable = false)
    LocalDate inicio;
    LocalDate finalizacion;
    @Column(nullable = false)
    int ano;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Fase faseCamp;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //ONE TO MANY A ZONACAMPAÑA
    @OneToMany(mappedBy = "campanha", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ZonaCampanha> zonaCampanhas;
}

