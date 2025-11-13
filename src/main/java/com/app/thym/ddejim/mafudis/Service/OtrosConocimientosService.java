package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.OtrosConocimientos;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.OtrosConocimientosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OtrosConocimientosService {
    @Autowired
    private OtrosConocimientosRepository otrosConocimientosRepository;

    @Autowired
    private CargoRepository cargoRepository;

    public List<OtrosConocimientos> findByCargoId(Long cargoId) {
        return otrosConocimientosRepository.findByCargoIdOrderByOrderIndexAsc(cargoId);
    }

    public Optional<OtrosConocimientos> findById(Long id) {
        return otrosConocimientosRepository.findById(id);
    }

    @Transactional
    public OtrosConocimientos save(OtrosConocimientos otrosConocimientos) {
        if (otrosConocimientos == null) {
            throw new IllegalArgumentException("OtrosConocimientos cannot be null");
        }
        if (otrosConocimientos.getCargo() == null) {
            throw new IllegalArgumentException("Cargo cannot be null in OtrosConocimientos");
        }
        if (otrosConocimientos.getCargo().getId() == null) {
            throw new IllegalStateException("Cargo ID cannot be null when saving OtrosConocimientos");
        }

        // NUEVA LÓGICA: Distinguir entre crear y actualizar
        if (otrosConocimientos.getId() != null) {
            // EDITANDO: Preservar el orderIndex existente
            Optional<OtrosConocimientos> existente = findById(otrosConocimientos.getId());
            if (existente.isPresent()) {
                otrosConocimientos.setOrderIndex(existente.get().getOrderIndex());
            }
        } else {
            // CREANDO: Asignar nuevo orderIndex al final
            if (otrosConocimientos.getOrderIndex() == null) {
                List<OtrosConocimientos> existing = findByCargoId(otrosConocimientos.getCargo().getId());
                otrosConocimientos.setOrderIndex(existing.size());
            }
        }

        return otrosConocimientosRepository.save(otrosConocimientos);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<OtrosConocimientos> otrosConocimientos = findById(id);
        if (otrosConocimientos.isPresent()) {
            Cargo cargo = otrosConocimientos.get().getCargo();
            if (cargo == null) {
                throw new IllegalStateException("Cargo is null for otrosConocimientos ID: " + id);
            }
            Long cargoId = cargo.getId();
            otrosConocimientosRepository.deleteById(id);
            reorderOtrosConocimientos(cargoId);
        }
    }

    @Transactional
    public void updateOrder(Long cargoId, List<Long> orderedIds) {
        List<OtrosConocimientos> otrosConocimientos = findByCargoId(cargoId);
        Map<Long, OtrosConocimientos> conocimientosMap = otrosConocimientos.stream()
                .collect(Collectors.toMap(OtrosConocimientos::getId, conocimiento -> conocimiento));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long conocimientoId = orderedIds.get(i);
            OtrosConocimientos conocimiento = conocimientosMap.get(conocimientoId);
            if (conocimiento != null) {
                conocimiento.setOrderIndex(i);
            }
        }

        otrosConocimientosRepository.saveAll(conocimientosMap.values());
        otrosConocimientosRepository.flush(); // Asegurar persistencia inmediata
    }

    private void reorderOtrosConocimientos(Long cargoId) {
        List<OtrosConocimientos> otrosConocimientos = findByCargoId(cargoId);
        for (int i = 0; i < otrosConocimientos.size(); i++) {
            otrosConocimientos.get(i).setOrderIndex(i);
        }
        otrosConocimientosRepository.saveAll(otrosConocimientos);
    }
}