package com.harvest.empleado;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

//    @Column(nullable = false)
//    private String lastName;

    @Column(nullable = false)
    private String dni;

    @Column(nullable = false)
    private String nss;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private LocalDate birthdate;
}
