package com.app.thym.ddejim.mafudis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "niveles_jerarquicos")
public class NivelJerarquico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre; // Ej: Ejecutivo, Directivo, Operativo
}
