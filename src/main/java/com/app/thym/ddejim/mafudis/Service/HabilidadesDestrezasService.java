package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.HabilidadesDestrezas;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.HabilidadesDestrezasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabilidadesDestrezasService {
    @Autowired
    private HabilidadesDestrezasRepository habilidadesDestrezasRepository;

    @Autowired
    private CargoRepository cargoRepository;

    public List<HabilidadesDestrezas> findByCargoId(Long cargoId) {
        return habilidadesDestrezasRepository.findByCargoIdOrderByOrderIndexAsc(cargoId);
    }

    public Optional<HabilidadesDestrezas> findById(Long id) {
        return habilidadesDestrezasRepository.findById(id);
    }

    @Transactional
    public HabilidadesDestrezas save(HabilidadesDestrezas habilidadesDestrezas) {
        if (habilidadesDestrezas == null) {
            throw new IllegalArgumentException("HabilidadesDestrezas cannot be null");
        }
        if (habilidadesDestrezas.getCargo() == null) {
            throw new IllegalArgumentException("Cargo cannot be null in HabilidadesDestrezas");
        }
        if (habilidadesDestrezas.getCargo().getId() == null) {
            throw new IllegalStateException("Cargo ID cannot be null when saving HabilidadesDestrezas");
        }

        // NUEVA LÓGICA: Distinguir entre crear y actualizar
        if (habilidadesDestrezas.getId() != null) {
            // EDITANDO: Preservar el orderIndex existente
            Optional<HabilidadesDestrezas> existente = findById(habilidadesDestrezas.getId());
            if (existente.isPresent()) {
                habilidadesDestrezas.setOrderIndex(existente.get().getOrderIndex());
            }
        } else {
            // CREANDO: Asignar nuevo orderIndex al final
            if (habilidadesDestrezas.getOrderIndex() == null) {
                List<HabilidadesDestrezas> existing = findByCargoId(habilidadesDestrezas.getCargo().getId());
                habilidadesDestrezas.setOrderIndex(existing.size());
            }
        }

        return habilidadesDestrezasRepository.save(habilidadesDestrezas);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<HabilidadesDestrezas> habilidadesDestrezas = findById(id);
        if (habilidadesDestrezas.isPresent()) {
            Cargo cargo = habilidadesDestrezas.get().getCargo();
            if (cargo == null) {
                throw new IllegalStateException("Cargo is null for habilidadesDestrezas ID: " + id);
            }
            Long cargoId = cargo.getId();
            habilidadesDestrezasRepository.deleteById(id);
            reorderHabilidadesDestrezas(cargoId);
        }
    }

    @Transactional
    public void updateOrder(Long cargoId, List<Long> orderedIds) {
        List<HabilidadesDestrezas> habilidadesDestrezas = findByCargoId(cargoId);
        Map<Long, HabilidadesDestrezas> habilidadesMap = habilidadesDestrezas.stream()
                .collect(Collectors.toMap(HabilidadesDestrezas::getId, habilidad -> habilidad));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long habilidadId = orderedIds.get(i);
            HabilidadesDestrezas habilidad = habilidadesMap.get(habilidadId);
            if (habilidad != null) {
                habilidad.setOrderIndex(i);
            }
        }

        habilidadesDestrezasRepository.saveAll(habilidadesMap.values());
        habilidadesDestrezasRepository.flush(); // Asegurar persistencia inmediata
    }

    private void reorderHabilidadesDestrezas(Long cargoId) {
        List<HabilidadesDestrezas> habilidadesDestrezas = findByCargoId(cargoId);
        for (int i = 0; i < habilidadesDestrezas.size(); i++) {
            habilidadesDestrezas.get(i).setOrderIndex(i);
        }
        habilidadesDestrezasRepository.saveAll(habilidadesDestrezas);
    }
}