package com.udc.fic.repository;

import com.udc.fic.model.Rol;
import com.udc.fic.model.RolUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByName(RolUser rol);
}
