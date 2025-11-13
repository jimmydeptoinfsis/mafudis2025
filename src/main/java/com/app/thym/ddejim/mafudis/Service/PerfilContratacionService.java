package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.PerfilContratacion;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.PerfilContratacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PerfilContratacionService {
    @Autowired
    private PerfilContratacionRepository perfilContratacionRepository;

    @Autowired
    private CargoRepository cargoRepository;

    public List<PerfilContratacion> findByCargoId(Long cargoId) {
        return perfilContratacionRepository.findByCargoIdOrderByOrderIndexAsc(cargoId);
    }

    public Optional<PerfilContratacion> findById(Long id) {
        return perfilContratacionRepository.findById(id);
    }

    @Transactional
    public PerfilContratacion save(PerfilContratacion perfilContratacion) {
        if (perfilContratacion == null) {
            throw new IllegalArgumentException("PerfilContratacion cannot be null");
        }
        if (perfilContratacion.getCargo() == null) {
            throw new IllegalArgumentException("Cargo cannot be null in PerfilContratacion");
        }
        if (perfilContratacion.getCargo().getId() == null) {
            throw new IllegalStateException("Cargo ID cannot be null when saving PerfilContratacion");
        }

        // NUEVA LÓGICA: Distinguir entre crear y actualizar
        if (perfilContratacion.getId() != null) {
            // EDITANDO: Preservar el orderIndex existente
            Optional<PerfilContratacion> existente = findById(perfilContratacion.getId());
            if (existente.isPresent()) {
                perfilContratacion.setOrderIndex(existente.get().getOrderIndex());
            }
        } else {
            // CREANDO: Asignar nuevo orderIndex al final
            if (perfilContratacion.getOrderIndex() == null) {
                List<PerfilContratacion> existing = findByCargoId(perfilContratacion.getCargo().getId());
                perfilContratacion.setOrderIndex(existing.size());
            }
        }

        return perfilContratacionRepository.save(perfilContratacion);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<PerfilContratacion> perfilContratacion = findById(id);
        if (perfilContratacion.isPresent()) {
            Cargo cargo = perfilContratacion.get().getCargo();
            if (cargo == null) {
                throw new IllegalStateException("Cargo is null for perfilContratacion ID: " + id);
            }
            Long cargoId = cargo.getId();
            perfilContratacionRepository.deleteById(id);
            reorderPerfilesContratacion(cargoId);
        }
    }

    @Transactional
    public void updateOrder(Long cargoId, List<Long> orderedIds) {
        List<PerfilContratacion> perfilesContratacion = findByCargoId(cargoId);
        Map<Long, PerfilContratacion> perfilesMap = perfilesContratacion.stream()
                .collect(Collectors.toMap(PerfilContratacion::getId, perfil -> perfil));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long perfilId = orderedIds.get(i);
            PerfilContratacion perfil = perfilesMap.get(perfilId);
            if (perfil != null) {
                perfil.setOrderIndex(i);
            }
        }

        perfilContratacionRepository.saveAll(perfilesMap.values());
        perfilContratacionRepository.flush(); // Asegurar persistencia inmediata
    }

    private void reorderPerfilesContratacion(Long cargoId) {
        List<PerfilContratacion> perfilesContratacion = findByCargoId(cargoId);
        for (int i = 0; i < perfilesContratacion.size(); i++) {
            perfilesContratacion.get(i).setOrderIndex(i);
        }
        perfilContratacionRepository.saveAll(perfilesContratacion);
    }
}
