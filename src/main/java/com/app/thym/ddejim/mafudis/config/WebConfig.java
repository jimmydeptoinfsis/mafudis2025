package com.app.thym.ddejim.mafudis.config; // <-- ¡Asegúrate que el paquete sea correcto!

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Inyectamos nuestro convertidor
    @Autowired
    private RoleConverter roleConverter;

    // Lo registramos en Spring
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(roleConverter);
    }
}