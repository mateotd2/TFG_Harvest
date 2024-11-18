package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "dni"), @UniqueConstraint(columnNames = "nss")})
public class Trabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean inTask;
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
    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Disponibilidad> calendario;

    @ManyToMany(mappedBy = "trabajadores")
    private List<Tarea> tareas;

    public Trabajador(Long id, String name, String lastname, String dni, String nss, String phone, LocalDate birthdate, String address, boolean available, List<Disponibilidad> calendario) {
        this.id = id;
        this.inTask = false;
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.nss = nss;
        this.phone = phone;
        this.birthdate = birthdate;
        this.address = address;
        this.available = available;
        if (calendario != null) {
            this.calendario = new ArrayList<>(calendario);
        } else {
            this.calendario = null;
        }
    }

    public void setCalendario(List<Disponibilidad> calendario) {
        this.calendario = new ArrayList<>(calendario);
    }
}
