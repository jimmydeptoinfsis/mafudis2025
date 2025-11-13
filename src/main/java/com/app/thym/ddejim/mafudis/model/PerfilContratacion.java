package com.app.thym.ddejim.mafudis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "perfil_contratacion")
@Getter
@Setter
@NoArgsConstructor
public class PerfilContratacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Column(name = "orden", nullable = false)
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "cargo_id", nullable = false)
    @JsonBackReference
    private Cargo cargo;
}