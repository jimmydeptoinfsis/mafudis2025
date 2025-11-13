package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.ClasificacionUnidad;
import com.app.thym.ddejim.mafudis.repository.ClasificacionUnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClasificacionUnidadService {

    @Autowired
    private ClasificacionUnidadRepository clasificacionUnidadRepository;

    public List<ClasificacionUnidad> findAll() {
        return clasificacionUnidadRepository.findAll();
    }

    public Optional<ClasificacionUnidad> findById(Long id) {
        return clasificacionUnidadRepository.findById(id);
    }

    public ClasificacionUnidad save(ClasificacionUnidad clasificacionUnidad) {
        try {
            return clasificacionUnidadRepository.save(clasificacionUnidad);
        } catch (Exception e) {
            throw new RuntimeException("Error saving ClasificacionUnidad: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        try {
            clasificacionUnidadRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting ClasificacionUnidad with id " + id + ": " + e.getMessage(), e);
        }
    }
}