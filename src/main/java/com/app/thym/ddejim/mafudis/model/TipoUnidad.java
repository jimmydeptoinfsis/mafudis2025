package com.app.thym.ddejim.mafudis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "tipos_unidad")
public class TipoUnidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // La relación @OneToMany estará en la entidad Unidad
    // @OneToMany(mappedBy = "tipoUnidad")
    // private Set<Unidad> unidades;
}