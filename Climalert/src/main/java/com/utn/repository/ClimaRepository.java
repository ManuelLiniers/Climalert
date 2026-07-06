package com.utn.repository;

import com.utn.model.entities.ClimaRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClimaRepository extends JpaRepository<ClimaRecord, Long> {
  // Method para obtener el último registro para el análisis de alertas
  ClimaRecord findTopByOrderByFechaRegistroDesc();
}