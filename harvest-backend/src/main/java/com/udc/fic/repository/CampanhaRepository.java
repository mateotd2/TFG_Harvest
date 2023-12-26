package com.udc.fic.repository;

import com.udc.fic.model.Campanha;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampanhaRepository extends JpaRepository<Campanha, Long> {

    boolean existsByAno(int ano);
}
