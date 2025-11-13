package com.app.thym.ddejim.mafudis.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) para transportar los detalles completos de una Unidad
 * desde el backend hacia el frontend. Está diseñado para ser un objeto plano y seguro
 * para la serialización a JSON, evitando relaciones complejas y referencias circulares.
 */
public class UnidadDetalleDTO {

    // --- Datos Generales ---
    private String nombreUnidad;
    private String tipoUnidad;
    private String clasificacion;
    private String nivelJerarquico;
    private String dependencia; // Nombre de la unidad padre

    // --- Listas de Datos ---
    private List<CargoSimpleDTO> cargos;
    private List<String> jerarquiaUnidad; // Nombres de la jerarquía, ej: Rectorado > Vicerrectorado > Facultad
    private List<String> objetivos;
    private List<String> funciones;

    private List<RelacionExternaSimpleDTO> relacionesExternas; // Relaciones institucionales
    private List<RelacionInternaSimpleDTO> relacionesInternas;
    // --- Clase interna estática para Cargos (se mantiene igual) ---


    // --- NUEVA CLASE INTERNA PARA RELACIONES EXTERNAS ---
    public static class RelacionExternaSimpleDTO {
        private String nombre;
        public RelacionExternaSimpleDTO() {}
        public RelacionExternaSimpleDTO(String nombre) { this.nombre = nombre; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }



        // Constructores


    public static class RelacionInternaSimpleDTO {
        private String tipoRelacion;
        private String nombreUnidadDestino;

        public RelacionInternaSimpleDTO() {}
        public RelacionInternaSimpleDTO(String tipoRelacion, String nombreUnidadDestino) {
            this.tipoRelacion = tipoRelacion;
            this.nombreUnidadDestino = nombreUnidadDestino;
        }

        public String getTipoRelacion() { return tipoRelacion; }
        public void setTipoRelacion(String tipoRelacion) { this.tipoRelacion = tipoRelacion; }
        public String getNombreUnidadDestino() { return nombreUnidadDestino; }
        public void setNombreUnidadDestino(String nombreUnidadDestino) { this.nombreUnidadDestino = nombreUnidadDestino; }
    }

        // Getters y Setters


    /**
     * Clase interna estática para representar la información mínima de un Cargo.
     * Esto evita exponer datos innecesarios del cargo y previene problemas de serialización.
     */

    public static class CargoSimpleDTO {
        private String name;

        // Constructor por defecto necesario para algunos frameworks
        public CargoSimpleDTO() {}

        public CargoSimpleDTO(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // --- Constructor por defecto ---
    public UnidadDetalleDTO() {
    }

    // --- Getters y Setters (Esencial para la serialización a JSON) ---

    public String getNombreUnidad() {
        return nombreUnidad;
    }

    public void setNombreUnidad(String nombreUnidad) {
        this.nombreUnidad = nombreUnidad;
    }

    public String getTipoUnidad() {
        return tipoUnidad;
    }

    public void setTipoUnidad(String tipoUnidad) {
        this.tipoUnidad = tipoUnidad;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getNivelJerarquico() {
        return nivelJerarquico;
    }

    public void setNivelJerarquico(String nivelJerarquico) {
        this.nivelJerarquico = nivelJerarquico;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public List<CargoSimpleDTO> getCargos() {
        return cargos;
    }

    public void setCargos(List<CargoSimpleDTO> cargos) {
        this.cargos = cargos;
    }

    public List<String> getJerarquiaUnidad() {
        return jerarquiaUnidad;
    }

    public void setJerarquiaUnidad(List<String> jerarquiaUnidad) {
        this.jerarquiaUnidad = jerarquiaUnidad;
    }

    public List<String> getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(List<String> objetivos) {
        this.objetivos = objetivos;
    }

    public List<String> getFunciones() {
        return funciones;
    }

    public void setFunciones(List<String> funciones) {
        this.funciones = funciones;
    }

    public List<RelacionExternaSimpleDTO> getRelacionesExternas() {
        return relacionesExternas;
    }

    public void setRelacionesExternas(List<RelacionExternaSimpleDTO> relacionesExternas) {
        this.relacionesExternas = relacionesExternas;
    }


    // Getter y Setter para la lista corregida
    public List<RelacionInternaSimpleDTO> getRelacionesInternas() { return relacionesInternas; }
    public void setRelacionesInternas(List<RelacionInternaSimpleDTO> relacionesInternas) { this.relacionesInternas = relacionesInternas; }
}