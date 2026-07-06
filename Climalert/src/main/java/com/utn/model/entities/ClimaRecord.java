package com.utn.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class ClimaRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double temperatura;
  private int humedad;
  private LocalDateTime fechaRegistro;
  private String detalleCompleto; // Para guardar el JSON crudo o detalles extra

  // Getters, Setters y Constructores
}