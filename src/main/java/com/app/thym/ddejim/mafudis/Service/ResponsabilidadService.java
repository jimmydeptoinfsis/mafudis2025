package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.Responsabilidad;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.ResponsabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResponsabilidadService {
    @Autowired
    private ResponsabilidadRepository responsabilidadRepository;

    @Autowired
    private CargoRepository cargoRepository;

    public List<Responsabilidad> findByCargoId(Long cargoId) {
        return responsabilidadRepository.findByCargoIdOrderByOrderIndexAsc(cargoId);
    }

    public Optional<Responsabilidad> findById(Long id) {
        return responsabilidadRepository.findById(id);
    }

    @Transactional
    public Responsabilidad save(Responsabilidad responsabilidad) {
        if (responsabilidad == null) {
            throw new IllegalArgumentException("Responsabilidad cannot be null");
        }
        if (responsabilidad.getCargo() == null) {
            throw new IllegalArgumentException("Cargo cannot be null in Responsabilidad");
        }
        if (responsabilidad.getCargo().getId() == null) {
            throw new IllegalStateException("Cargo ID cannot be null when saving Responsabilidad");
        }

        // NUEVA LÓGICA: Distinguir entre crear y actualizar
        if (responsabilidad.getId() != null) {
            // EDITANDO: Preservar el orderIndex existente
            Optional<Responsabilidad> existente = findById(responsabilidad.getId());
            if (existente.isPresent()) {
                responsabilidad.setOrderIndex(existente.get().getOrderIndex());
            }
        } else {
            // CREANDO: Asignar nuevo orderIndex al final
            if (responsabilidad.getOrderIndex() == null) {
                List<Responsabilidad> existing = findByCargoId(responsabilidad.getCargo().getId());
                responsabilidad.setOrderIndex(existing.size());
            }
        }

        return responsabilidadRepository.save(responsabilidad);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<Responsabilidad> responsabilidad = findById(id);
        if (responsabilidad.isPresent()) {
            Cargo cargo = responsabilidad.get().getCargo();
            if (cargo == null) {
                throw new IllegalStateException("Cargo is null for responsabilidad ID: " + id);
            }
            Long cargoId = cargo.getId();
            responsabilidadRepository.deleteById(id);
            reorderResponsabilidades(cargoId);
        }
    }

    @Transactional
    public void updateOrder(Long cargoId, List<Long> orderedIds) {
        List<Responsabilidad> responsabilidades = findByCargoId(cargoId);
        Map<Long, Responsabilidad> responsabilidadesMap = responsabilidades.stream()
                .collect(Collectors.toMap(Responsabilidad::getId, resp -> resp));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long responsabilidadId = orderedIds.get(i);
            Responsabilidad responsabilidad = responsabilidadesMap.get(responsabilidadId);
            if (responsabilidad != null) {
                responsabilidad.setOrderIndex(i);
            }
        }

        responsabilidadRepository.saveAll(responsabilidadesMap.values());
        responsabilidadRepository.flush(); // Asegurar persistencia inmediata
    }

    private void reorderResponsabilidades(Long cargoId) {
        List<Responsabilidad> responsabilidades = findByCargoId(cargoId);
        for (int i = 0; i < responsabilidades.size(); i++) {
            responsabilidades.get(i).setOrderIndex(i);
        }
        responsabilidadRepository.saveAll(responsabilidades);
    }
}