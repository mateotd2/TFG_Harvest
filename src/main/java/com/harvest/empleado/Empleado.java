package com.harvest.empleado;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "username"),})
public class Empleado {


    public Empleado(String name, String lastname, String dni, String nss, String phone, String email, String username, String password, LocalDate birthdate) {
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.nss = nss;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.birthdate = birthdate;
    }

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
    @Email
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthdate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Rol> roles = new HashSet<>();
}
