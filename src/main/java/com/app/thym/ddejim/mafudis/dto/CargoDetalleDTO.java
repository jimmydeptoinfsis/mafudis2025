package com.app.thym.ddejim.mafudis.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) para transportar los detalles completos de un Cargo
 * desde el backend hacia el frontend.
 */
public class CargoDetalleDTO {

    // --- Datos Generales ---
    private String nombreCargo;
    private String superior;
    private String dependientes;
    private String clasificacion;

    // --- Listas de Datos ---
    private List<String> responsabilidades;
    private List<RelacionInternaSimpleDTO> relacionesInternas;
    private List<String> relacionesExternas;
    private List<String> funcionesActividades;
    private List<String> documentosGenera;
    private List<String> perfilContratacion;
    private List<String> gradoAcademico;
    private List<String> otrosConocimientos;
    private List<String> habilidadesDestrezas;

    // --- Clase interna para Relaciones Internas ---
    public static class RelacionInternaSimpleDTO {
        private String tipoRelacion;
        private String cargoRelacionado;

        public RelacionInternaSimpleDTO() {}

        public RelacionInternaSimpleDTO(String tipoRelacion, String cargoRelacionado) {
            this.tipoRelacion = tipoRelacion;
            this.cargoRelacionado = cargoRelacionado;
        }

        public String getTipoRelacion() { return tipoRelacion; }
        public void setTipoRelacion(String tipoRelacion) { this.tipoRelacion = tipoRelacion; }
        public String getCargoRelacionado() { return cargoRelacionado; }
        public void setCargoRelacionado(String cargoRelacionado) { this.cargoRelacionado = cargoRelacionado; }
    }

    // --- Constructor por defecto ---
    public CargoDetalleDTO() {}

    // --- Getters y Setters ---
    public String getNombreCargo() { return nombreCargo; }
    public void setNombreCargo(String nombreCargo) { this.nombreCargo = nombreCargo; }

    public String getSuperior() { return superior; }
    public void setSuperior(String superior) { this.superior = superior; }

    public String getDependientes() { return dependientes; }
    public void setDependientes(String dependientes) { this.dependientes = dependientes; }

    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }

    public List<String> getResponsabilidades() { return responsabilidades; }
    public void setResponsabilidades(List<String> responsabilidades) { this.responsabilidades = responsabilidades; }

    public List<RelacionInternaSimpleDTO> getRelacionesInternas() { return relacionesInternas; }
    public void setRelacionesInternas(List<RelacionInternaSimpleDTO> relacionesInternas) { this.relacionesInternas = relacionesInternas; }

    public List<String> getRelacionesExternas() { return relacionesExternas; }
    public void setRelacionesExternas(List<String> relacionesExternas) { this.relacionesExternas = relacionesExternas; }

    public List<String> getFuncionesActividades() { return funcionesActividades; }
    public void setFuncionesActividades(List<String> funcionesActividades) { this.funcionesActividades = funcionesActividades; }

    public List<String> getDocumentosGenera() { return documentosGenera; }
    public void setDocumentosGenera(List<String> documentosGenera) { this.documentosGenera = documentosGenera; }

    public List<String> getPerfilContratacion() { return perfilContratacion; }
    public void setPerfilContratacion(List<String> perfilContratacion) { this.perfilContratacion = perfilContratacion; }

    public List<String> getGradoAcademico() { return gradoAcademico; }
    public void setGradoAcademico(List<String> gradoAcademico) { this.gradoAcademico = gradoAcademico; }

    public List<String> getOtrosConocimientos() { return otrosConocimientos; }
    public void setOtrosConocimientos(List<String> otrosConocimientos) { this.otrosConocimientos = otrosConocimientos; }

    public List<String> getHabilidadesDestrezas() { return habilidadesDestrezas; }
    public void setHabilidadesDestrezas(List<String> habilidadesDestrezas) { this.habilidadesDestrezas = habilidadesDestrezas; }
}