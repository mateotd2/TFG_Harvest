package com.udc.fic.repository;

import com.udc.fic.model.Campanha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampanhaRepository extends JpaRepository<Campanha, Long> {

    boolean existsByAno(int ano);

    Optional<Campanha> findByAno(int ano);
}
