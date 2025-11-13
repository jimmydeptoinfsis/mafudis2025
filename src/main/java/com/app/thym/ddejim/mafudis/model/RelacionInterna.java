package com.app.thym.ddejim.mafudis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "relaciones_internas")
public class RelacionInterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cargo origen
    @ManyToOne
    @JoinColumn(name = "origen_id", nullable = false)
    @NotNull(message = "El cargo origen es obligatorio")
    private Cargo origen;

    // Cargo destino
    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    @NotNull(message = "El cargo destino es obligatorio")
    private Cargo destino;

    @Column(nullable = false)
    private String tipoRelacion; // por ejemplo, "supervisa", "coordina", etc.

    @PrePersist
    @PreUpdate
    private void validarRelacion() {
        if (origen != null && destino != null && origen.getId().equals(destino.getId())) {
            throw new IllegalArgumentException("Un cargo no puede tener una relación consigo mismo.");
        }
    }
}
