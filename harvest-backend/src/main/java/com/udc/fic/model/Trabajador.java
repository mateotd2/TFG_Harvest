package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "dni"), @UniqueConstraint(columnNames = "nss")})
public class Trabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String dni;
    @Column(nullable = false)
    private String nss;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private LocalDate birthdate;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private boolean available;
    //    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "trabajador", orphanRemoval = true)
    @OneToMany(mappedBy = "trabajador")
    private Set<Disponibilidad> calendario = new HashSet<>();
}
