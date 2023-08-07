package com.harvest.repository;

import com.harvest.empleado.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado,Long> {

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<Empleado> findByUsername(String username);

}
