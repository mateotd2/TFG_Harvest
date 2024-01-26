package com.udc.fic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Tractor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int maxLoad;

    @Column(nullable = false)
    private int boxCapacity;

    @OneToMany(mappedBy = "tractor")
    private List<Tarea> tareas;

    @Column(nullable = false)
    private Boolean enTarea;


}
