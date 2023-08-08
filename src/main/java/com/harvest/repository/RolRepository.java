package com.harvest.repository;

import com.harvest.empleado.Rol;
import com.harvest.empleado.RolUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByName(RolUser rol);
}
