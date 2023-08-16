package com.harvest.repository;

import com.harvest.model.Rol;
import com.harvest.model.RolUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByName(RolUser rol);
}
