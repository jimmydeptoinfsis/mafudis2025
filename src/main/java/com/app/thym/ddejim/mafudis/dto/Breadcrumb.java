package com.app.thym.ddejim.mafudis.dto;

public class Breadcrumb {
    private String label;
    private String url;

    public Breadcrumb(String label, String url) {
        this.label = label;
        this.url = url;
    }

    // Getters y Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}