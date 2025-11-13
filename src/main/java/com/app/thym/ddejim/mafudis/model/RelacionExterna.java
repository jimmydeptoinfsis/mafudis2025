package com.app.thym.ddejim.mafudis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "relaciones_externas")
public class RelacionExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // Ej: "Proveedores", "Clientes Externos"

    @Column(nullable = false)
    private Integer orderIndex = 0; // Field for ordering

    @ManyToMany(mappedBy = "relacionesExternas", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Cargo> cargos = new HashSet<>();
}