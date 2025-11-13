package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.NivelJerarquico;
import com.app.thym.ddejim.mafudis.repository.NivelJerarquicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NivelJerarquicoService {

    @Autowired
    private NivelJerarquicoRepository nivelJerarquicoRepository;

    public List<NivelJerarquico> findAll() {
        return nivelJerarquicoRepository.findAll();
    }

    public Optional<NivelJerarquico> findById(Long id) {
        return nivelJerarquicoRepository.findById(id);
    }

    public NivelJerarquico save(NivelJerarquico nivelJerarquico) {
        try {
            return nivelJerarquicoRepository.save(nivelJerarquico);
        } catch (Exception e) {
            throw new RuntimeException("Error saving NivelJerarquico: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        try {
            nivelJerarquicoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting NivelJerarquico with id " + id + ": " + e.getMessage(), e);
        }
    }
}