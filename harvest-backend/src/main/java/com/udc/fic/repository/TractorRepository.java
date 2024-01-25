package com.udc.fic.repository;

import com.udc.fic.model.Tractor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TractorRepository extends JpaRepository<Tractor, Long> {


    boolean existsByIdAndEnTareaFalse(Long tractorId);
}
