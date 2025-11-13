package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Unidad;
import com.app.thym.ddejim.mafudis.repository.UnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UnidadService {

    @Autowired
    private UnidadRepository unidadRepository;

    public Optional<Unidad> findById(Long id) {
        return unidadRepository.findById(id);
    }
}