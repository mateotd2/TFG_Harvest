package com.udc.fic.repository;

import com.udc.fic.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {


    Boolean existsByDni(String dni);

    Optional<Trabajador> findByName(String name);


}
