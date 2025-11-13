package com.app.thym.ddejim.mafudis.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CargoOrgChartDTO {
    private Long id;
    private Long pid;
    private String name;
    private String title;
    private String img;
    private String email;
    private String phone;
    private String description;
    private List<String> tags = new ArrayList<>();
}