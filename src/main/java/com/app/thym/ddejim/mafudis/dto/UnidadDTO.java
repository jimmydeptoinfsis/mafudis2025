package com.app.thym.ddejim.mafudis.dto;

public class UnidadDTO {

    private Long id;
    private String nombreUnidad;



    // Constructor vacío (importante para la serialización)
    public UnidadDTO() {
    }

    // Constructor para facilitar la creación desde la entidad
    public UnidadDTO(Long id, String nombreUnidad) {
        this.id = id;
        this.nombreUnidad = nombreUnidad;


    }

    // --- Getters y Setters para todos los campos ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreUnidad() { return nombreUnidad; }
    public void setNombreUnidad(String nombreUnidad) { this.nombreUnidad = nombreUnidad; }}





