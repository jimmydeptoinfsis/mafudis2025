package com.app.thym.ddejim.mafudis.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "configuracion_home")
@Data                   // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor      // Constructor sin argumentos
@AllArgsConstructor     // Constructor con todos los campos
@Builder                // Permite usar patrón builder (opcional pero útil)
public class ConfiguracionHome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tituloPrincipal;
    private String subtitulo;

    @Column(columnDefinition = "TEXT")
    private String descripcionIntro;

    @Column(columnDefinition = "TEXT")
    private String mision;

    @Column(columnDefinition = "TEXT")
    private String vision;
}
