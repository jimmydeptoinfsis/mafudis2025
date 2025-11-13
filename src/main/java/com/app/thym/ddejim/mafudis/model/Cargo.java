package com.app.thym.ddejim.mafudis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
@Table(name = "cargos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pid; // ID del padre (para la jerarquía del organigrama)

    private String name; // Nombre del empleado (ej. "Denny Curtis")

    private String title; // Cargo (ej. "CEO")

    private String img; // URL de la imagen

    private String email; // Correo electrónico

    private String phone; // Teléfono

    @Column(length = 1000)
    private String description; // Descripción

    @ManyToOne(fetch = FetchType.LAZY) // Un cargo pertenece a una unidad
    @JoinColumn(name = "unidad_id") // Nombre de la columna FK en la tabla 'cargos'
    @JsonBackReference // Para evitar ciclos si Unidad serializa su lista de Cargos
    private Unidad unidad;

    // Autoridad Lineal (quienes reportan directamente a este cargo, diferente de 'pid')
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cargo_autoridad_lineal",
            joinColumns = @JoinColumn(name = "cargo_superior_id"), // El cargo actual
            inverseJoinColumns = @JoinColumn(name = "cargo_subordinado_id") // Los cargos que dependen linealmente
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // Esencial para evitar ciclos con relaciones Cargo<->Cargo
    private Set<Cargo> subordinadosLineales = new HashSet<>();

    // Autoridad Funcional (sobre quién tiene autoridad funcional este cargo)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cargo_autoridad_funcional",
            joinColumns = @JoinColumn(name = "cargo_autoridad_id"), // El cargo actual
            inverseJoinColumns = @JoinColumn(name = "cargo_supervisado_id") // Los cargos sobre los que tiene autoridad funcional
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Cargo> supervisadosFuncionales = new HashSet<>();

    // Relaciones de Coordinación Interna (con qué otros cargos coordina)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cargo_coordinacion_interna",
            joinColumns = @JoinColumn(name = "cargo_a_id"), // El cargo actual
            inverseJoinColumns = @JoinColumn(name = "cargo_b_id") // Los otros cargos con los que coordina
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Cargo> coordinaInternamenteCon = new HashSet<>();

    // Lado inverso de las relaciones M-N (opcional pero útil si necesitas navegar en ambos sentidos)
    // Nota: mappedBy debe apuntar al nombre del campo en la otra entidad Cargo
    @ManyToMany(mappedBy = "subordinadosLineales", fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude @JsonIgnore
    private Set<Cargo> superioresLineales = new HashSet<>();

    @ManyToMany(mappedBy = "supervisadosFuncionales", fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude @JsonIgnore
    private Set<Cargo> autoridadesFuncionales = new HashSet<>();

    @ManyToMany(mappedBy = "coordinaInternamenteCon", fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude @JsonIgnore
    private Set<Cargo> coordinaInternamenteConInverso = new HashSet<>();


    // Relaciones existentes (Tag, Responsabilidad, RelacionInterna)

    //relaciones internas
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cargo_relaciones_internas",
            joinColumns = @JoinColumn(name = "cargo_id"),
            inverseJoinColumns = @JoinColumn(name = "relacion_id")
    )
    private Set<RelacionInterna> relacionesInternas = new HashSet<>();

    /*@ElementCollection
    @CollectionTable(
            name = "cargo_relaciones_internas_order",
            joinColumns = @JoinColumn(name = "cargo_id")
    )
    @Column(name = "order_index")
    @MapKeyJoinColumn(name = "relacion_id")
    private Map<RelacionInterna, Integer> relacionesInternasOrder = new HashMap<>();*/

    //relaciones externas
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cargo_relaciones_externas", // Tabla intermedia
            joinColumns = @JoinColumn(name = "cargo_id"),
            inverseJoinColumns = @JoinColumn(name = "relacion_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<RelacionExterna> relacionesExternas = new HashSet<>(); // Nombre exacto






    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(
            name = "cargo_tags",
            joinColumns = @JoinColumn(name = "cargo_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )

    @JsonManagedReference
    private List<Tag> tags = new ArrayList<>(); // Inicializar la lista

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference //Anotacion añadida
    private List<Responsabilidad> responsabilidades = new ArrayList<>();

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FuncionActividad> funcionesActividades = new ArrayList<>();

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DocumentosGenera> documentosGenera = new ArrayList<>();

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PerfilContratacion> perfilesContratacion = new ArrayList<>();

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OtrosConocimientos> otrosConocimientos = new ArrayList<>();

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GradoAcademicoMinimo> gradosAcademicosMinimos = new ArrayList<>();

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<HabilidadesDestrezas> habilidadesDestrezas = new ArrayList<>();
}