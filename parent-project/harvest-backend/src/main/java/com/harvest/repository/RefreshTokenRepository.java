package com.harvest.repository;

import com.harvest.model.Empleado;
import com.harvest.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByEmpleadoId(Long id);

    @Modifying
    int deleteByEmpleado(Empleado empleado);
}