package com.app.thym.ddejim.mafudis.config; // <-- ¡Asegúrate que el paquete sea correcto!

import com.app.thym.ddejim.mafudis.model.Role;
import com.app.thym.ddejim.mafudis.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter implements Converter<String, Role> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            Long id = Long.parseLong(source);
            // Busca el rol por ID. Si no existe, orElse(null) evita un error.
            return roleRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}