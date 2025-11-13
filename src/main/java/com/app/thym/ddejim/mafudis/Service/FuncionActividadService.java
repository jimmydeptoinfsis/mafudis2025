package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.FuncionActividad;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.FuncionActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionActividadService {
    @Autowired
    private FuncionActividadRepository funcionActividadRepository;

    @Autowired
    private CargoRepository cargoRepository;


    public List<FuncionActividad> findByCargoId(Long cargoId) {
        return funcionActividadRepository.findByCargoIdOrderByOrderIndexAsc(cargoId);
    }

    public Optional<FuncionActividad> findById(Long id) {
        return funcionActividadRepository.findById(id);
    }

    @Transactional
    public FuncionActividad save(FuncionActividad funcionActividad) {
        if (funcionActividad == null) {
            throw new IllegalArgumentException("FuncionActividad cannot be null");
        }
        if (funcionActividad.getCargo() == null) {
            throw new IllegalArgumentException("Cargo cannot be null in FuncionActividad");
        }
        if (funcionActividad.getCargo().getId() == null) {
            throw new IllegalStateException("Cargo ID cannot be null when saving FuncionActividad");
        }

        // NUEVA LÓGICA: Distinguir entre crear y actualizar
        if (funcionActividad.getId() != null) {
            // EDITANDO: Preservar el orderIndex existente
            Optional<FuncionActividad> existente = findById(funcionActividad.getId());
            if (existente.isPresent()) {
                funcionActividad.setOrderIndex(existente.get().getOrderIndex());
            }
        } else {
            // CREANDO: Asignar nuevo orderIndex al final
            if (funcionActividad.getOrderIndex() == null) {
                List<FuncionActividad> existing = findByCargoId(funcionActividad.getCargo().getId());
                funcionActividad.setOrderIndex(existing.size());
            }
        }

        return funcionActividadRepository.save(funcionActividad);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<FuncionActividad> funcionActividad = findById(id);
        if (funcionActividad.isPresent()) {
            Cargo cargo = funcionActividad.get().getCargo();
            if (cargo == null) {
                throw new IllegalStateException("Cargo is null for funcionActividad ID: " + id);
            }
            Long cargoId = cargo.getId();
            funcionActividadRepository.deleteById(id);
            reorderFuncionesActividades(cargoId);
        }
    }

    @Transactional
    public void updateOrder(Long cargoId, List<Long> orderedIds) {
        List<FuncionActividad> funcionesActividades = findByCargoId(cargoId);
        Map<Long, FuncionActividad> funcionesMap = funcionesActividades.stream()
                .collect(Collectors.toMap(FuncionActividad::getId, func -> func));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long funcionId = orderedIds.get(i);
            FuncionActividad funcion = funcionesMap.get(funcionId);
            if (funcion != null) {
                funcion.setOrderIndex(i);
            }
        }

        funcionActividadRepository.saveAll(funcionesMap.values());
        funcionActividadRepository.flush(); // Asegurar persistencia inmediata
    }

    private void reorderFuncionesActividades(Long cargoId) {
        List<FuncionActividad> funcionesActividades = findByCargoId(cargoId);
        for (int i = 0; i < funcionesActividades.size(); i++) {
            funcionesActividades.get(i).setOrderIndex(i);
        }
        funcionActividadRepository.saveAll(funcionesActividades);
    }
}