package com.app.thym.ddejim.mafudis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "unidades")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_unidad", nullable = false)
    private String nombreUnidad;

    // Transient field for organigram display
    @Transient
    private String tipoUnidadName;

    // --- Relaciones Many-to-One con los Catálogos ---
    @ManyToOne(fetch = FetchType.EAGER) // EAGER podría ser útil aquí si siempre muestras esta info
    @JoinColumn(name = "tipo_unidad_id")
    private TipoUnidad tipoUnidad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clasificacion_unidad_id")
    private ClasificacionUnidad clasificacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nivel_jerarquico_id")
    private NivelJerarquico nivelJerarquico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dependencia_id")
    @JsonIgnore
    private Dependencia dependencia; // De quién depende esta unidad

    // --- Relaciones One-to-Many ---
    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference // Para evitar ciclos si serializas desde Objetivo
    @OrderBy("orderIndex ASC")
    private List<ObjetivoUnidad> objetivos = new ArrayList<>();

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference // Para evitar ciclos si serializas desde Funcion
    @OrderBy("orderIndex ASC")
    private List<FuncionGeneralUnidad> funcionesGenerales = new ArrayList<>();

    // Una unidad tiene varios cargos (incluyendo autoridades)
    @OneToMany(mappedBy = "unidad", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    // @JsonManagedReference // CUIDADO: Podría causar ciclos con Cargo -> Responsabilidad -> Cargo -> Unidad -> Cargo... Considerar DTOs o @JsonIgnore aquí.
    @JsonIgnore
    // Es más seguro ignorar esta lista al serializar Unidad para evitar ciclos complejos. Cargarla bajo demanda.
    private Set<Cargo> cargos = new HashSet<>();


    // --- Relaciones Many-to-Many ---

    // Relaciones de coordinación externa (Unidad con Unidad)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "unidad_coordinacion_externa",
            joinColumns = @JoinColumn(name = "unidad_origen_id"),
            inverseJoinColumns = @JoinColumn(name = "unidad_coordina_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // Ignorar para evitar ciclos complejos
    private Set<Unidad> coordinaCon = new HashSet<>();

    // Necesitas también el lado inverso si quieres navegar en ambos sentidos
    @ManyToMany(mappedBy = "coordinaCon", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Unidad> esCoordinadoPor = new HashSet<>();




}