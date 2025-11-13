package com.app.thym.ddejim.mafudis.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;



import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Nombre de la etiqueta (ej. "assistant")

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    private List<Cargo> cargos = new ArrayList<>();
}