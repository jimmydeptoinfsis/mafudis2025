package com.app.thym.ddejim.mafudis.dto;

public class CargoDTO {

    private String name;

    // Constructor vacío
    public CargoDTO() {
    }

    // Constructor con parámetros
    public CargoDTO(String name) {
        this.name = name;
    }

    // Getter y Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
