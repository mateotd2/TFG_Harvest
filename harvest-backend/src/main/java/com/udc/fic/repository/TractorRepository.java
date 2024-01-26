package com.udc.fic.repository;

import com.udc.fic.model.Tractor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TractorRepository extends JpaRepository<Tractor, Long> {


    boolean existsByIdAndEnTareaFalse(Long tractorId);

    List<Tractor> findByEnTareaFalse();
}
