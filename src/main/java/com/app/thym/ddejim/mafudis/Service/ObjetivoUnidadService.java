package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.ObjetivoUnidad;
import com.app.thym.ddejim.mafudis.repository.ObjetivoUnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ObjetivoUnidadService {

    @Autowired
    private ObjetivoUnidadRepository objetivoUnidadRepository;

    public List<ObjetivoUnidad> findByUnidadId(Long unidadId) {
        return objetivoUnidadRepository.findByUnidadId(unidadId);
    }

    public List<ObjetivoUnidad> findByUnidadIdOrdered(Long unidadId) {
        return objetivoUnidadRepository.findByUnidadIdOrderByOrderIndexAsc(unidadId);
    }

    public Optional<ObjetivoUnidad> findById(Long id) {
        return objetivoUnidadRepository.findById(id);
    }

    public ObjetivoUnidad save(ObjetivoUnidad objetivo) {
        try {
            if (objetivo.getId() == null) { // New objetivo
                List<ObjetivoUnidad> objetivos = findByUnidadId(objetivo.getUnidad().getId());
                int maxOrder = objetivos.stream()
                        .mapToInt(obj -> obj.getOrderIndex() != null ? obj.getOrderIndex() : 0)
                        .max()
                        .orElse(0);
                objetivo.setOrderIndex(maxOrder + 1);
            }
            ObjetivoUnidad saved = objetivoUnidadRepository.save(objetivo);
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Error saving objetivo: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        try {
            objetivoUnidadRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting objetivo with id " + id + ": " + e.getMessage(), e);
        }
    }

    @Transactional
    public void updateOrder(Long unidadId, List<Long> orderedIds) {
        List<ObjetivoUnidad> objetivos = objetivoUnidadRepository.findByUnidadId(unidadId);
        Map<Long, ObjetivoUnidad> objetivosMap = objetivos.stream()
                .collect(Collectors.toMap(ObjetivoUnidad::getId, objetivo -> objetivo));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long objetivoId = orderedIds.get(i);
            ObjetivoUnidad objetivo = objetivosMap.get(objetivoId);
            if (objetivo != null) {
                objetivo.setOrderIndex(i + 1);
                // LOG PARA DEBUG
                System.out.println("Updating objetivo ID: " + objetivoId + " to orderIndex: " + (i + 1));
            }
        }

        objetivoUnidadRepository.saveAll(objetivosMap.values());
        objetivoUnidadRepository.flush();

        // VERIFICACIÓN POST-SAVE
        List<ObjetivoUnidad> verificacion = objetivoUnidadRepository.findByUnidadIdOrderByOrderIndexAsc(unidadId);
        System.out.println("After save - Order verification:");
        verificacion.forEach(obj -> System.out.println("ID: " + obj.getId() + " -> Order: " + obj.getOrderIndex()));
    }
}