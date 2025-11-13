package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.GradoAcademicoMinimo;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.GradoAcademicoMinimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GradoAcademicoMinimoService {
    @Autowired
    private GradoAcademicoMinimoRepository gradoAcademicoMinimoRepository;

    @Autowired
    private CargoRepository cargoRepository;

    public List<GradoAcademicoMinimo> findByCargoId(Long cargoId) {
        return gradoAcademicoMinimoRepository.findByCargoIdOrderByOrderIndexAsc(cargoId);
    }

    public Optional<GradoAcademicoMinimo> findById(Long id) {
        return gradoAcademicoMinimoRepository.findById(id);
    }

    @Transactional
    public GradoAcademicoMinimo save(GradoAcademicoMinimo gradoAcademicoMinimo) {
        if (gradoAcademicoMinimo == null) {
            throw new IllegalArgumentException("GradoAcademicoMinimo cannot be null");
        }
        if (gradoAcademicoMinimo.getCargo() == null) {
            throw new IllegalArgumentException("Cargo cannot be null in GradoAcademicoMinimo");
        }
        if (gradoAcademicoMinimo.getCargo().getId() == null) {
            throw new IllegalStateException("Cargo ID cannot be null when saving GradoAcademicoMinimo");
        }

        // NUEVA LÓGICA: Distinguir entre crear y actualizar
        if (gradoAcademicoMinimo.getId() != null) {
            // EDITANDO: Preservar el orderIndex existente
            Optional<GradoAcademicoMinimo> existente = findById(gradoAcademicoMinimo.getId());
            if (existente.isPresent()) {
                gradoAcademicoMinimo.setOrderIndex(existente.get().getOrderIndex());
            }
        } else {
            // CREANDO: Asignar nuevo orderIndex al final
            if (gradoAcademicoMinimo.getOrderIndex() == null) {
                List<GradoAcademicoMinimo> existing = findByCargoId(gradoAcademicoMinimo.getCargo().getId());
                gradoAcademicoMinimo.setOrderIndex(existing.size());
            }
        }

        return gradoAcademicoMinimoRepository.save(gradoAcademicoMinimo);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<GradoAcademicoMinimo> gradoAcademicoMinimo = findById(id);
        if (gradoAcademicoMinimo.isPresent()) {
            Cargo cargo = gradoAcademicoMinimo.get().getCargo();
            if (cargo == null) {
                throw new IllegalStateException("Cargo is null for gradoAcademicoMinimo ID: " + id);
            }
            Long cargoId = cargo.getId();
            gradoAcademicoMinimoRepository.deleteById(id);
            reorderGradoAcademicoMinimo(cargoId);
        }
    }

    @Transactional
    public void updateOrder(Long cargoId, List<Long> orderedIds) {
        List<GradoAcademicoMinimo> gradosAcademicos = findByCargoId(cargoId);
        Map<Long, GradoAcademicoMinimo> gradosMap = gradosAcademicos.stream()
                .collect(Collectors.toMap(GradoAcademicoMinimo::getId, grado -> grado));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long gradoId = orderedIds.get(i);
            GradoAcademicoMinimo grado = gradosMap.get(gradoId);
            if (grado != null) {
                grado.setOrderIndex(i);
            }
        }

        gradoAcademicoMinimoRepository.saveAll(gradosMap.values());
        gradoAcademicoMinimoRepository.flush(); // Asegurar persistencia inmediata
    }

    private void reorderGradoAcademicoMinimo(Long cargoId) {
        List<GradoAcademicoMinimo> gradosAcademicos = findByCargoId(cargoId);
        for (int i = 0; i < gradosAcademicos.size(); i++) {
            gradosAcademicos.get(i).setOrderIndex(i);
        }
        gradoAcademicoMinimoRepository.saveAll(gradosAcademicos);
    }
}