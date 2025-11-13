package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Dependencia;
import com.app.thym.ddejim.mafudis.repository.DependenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DependenciaService {

    @Autowired
    private DependenciaRepository dependenciaRepository;

    public List<Dependencia> findAll() {
        return dependenciaRepository.findAll();
    }

    public Optional<Dependencia> findById(Long id) {
        return dependenciaRepository.findById(id);
    }

    public Dependencia save(Dependencia dependencia) {
        try {
            return dependenciaRepository.save(dependencia);
        } catch (Exception e) {
            throw new RuntimeException("Error saving Dependencia: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        try {
            dependenciaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting Dependencia with id " + id + ": " + e.getMessage(), e);
        }
    }
}