package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.TipoUnidad;
import com.app.thym.ddejim.mafudis.repository.TipoUnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoUnidadService {

    @Autowired
    private TipoUnidadRepository tipoUnidadRepository;

    public List<TipoUnidad> findAll() {
        return tipoUnidadRepository.findAll();
    }

    public Optional<TipoUnidad> findById(Long id) {
        return tipoUnidadRepository.findById(id);
    }

    public TipoUnidad save(TipoUnidad tipoUnidad) {
        try {
            return tipoUnidadRepository.save(tipoUnidad);
        } catch (Exception e) {
            throw new RuntimeException("Error saving TipoUnidad: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        try {
            tipoUnidadRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting TipoUnidad with id " + id + ": " + e.getMessage(), e);
        }
    }
}
