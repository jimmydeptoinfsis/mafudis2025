package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.RelacionInterna;
import com.app.thym.ddejim.mafudis.model.Tag;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.RelacionInternaRepository;
import com.app.thym.ddejim.mafudis.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CargoService {

    private static final Logger LOGGER = Logger.getLogger(CargoService.class.getName());

    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private RelacionInternaRepository relacionInternaRepository;

    @Transactional(readOnly = true)
    public List<Cargo> findAll() {
        LOGGER.info("Fetching all Cargos");
        return cargoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cargo> findById(Long id) {
        LOGGER.info("Fetching Cargo by ID: " + id);
        return cargoRepository.findById(id);
    }

    @Transactional
    public Cargo save(Cargo cargo) {
        if (cargo == null) {
            LOGGER.severe("Cargo cannot be null");
            throw new IllegalArgumentException("Cargo cannot be null");
        }
        LOGGER.info("Saving Cargo: " + cargo.getName());
        return cargoRepository.save(cargo);
    }

    @Transactional
    public void deleteById(Long id) {
        LOGGER.info("Deleting Cargo ID: " + id);
        cargoRepository.deleteById(id);
    }

    @Transactional
    public void updateRelacionesInternas(Long cargoId, Set<Long> relacionInternaIds) {
        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new RuntimeException("Cargo no encontrado"));

        // Convert Set to List if needed
        List<RelacionInterna> relaciones = relacionInternaIds.isEmpty()
                ? Collections.emptyList()
                : relacionInternaRepository.findAllById(new ArrayList<>(relacionInternaIds));

        cargo.setRelacionesInternas(new HashSet<>(relaciones));

        // Update order indexes
        Map<RelacionInterna, Integer> newOrder = new HashMap<>();
        int index = 0;
        for (RelacionInterna relacion : relaciones) {
            newOrder.put(relacion, index++);
        }
        //cargo.setRelacionesInternasOrder(newOrder);

        cargoRepository.save(cargo);
    }

    public void reorderRelacionesInternas(Long cargoId, List<Long> orderedRelacionIds) {
        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new RuntimeException("Cargo no encontrado"));

        Map<RelacionInterna, Integer> newOrder = new HashMap<>();
        for (int i = 0; i < orderedRelacionIds.size(); i++) {
            Long relacionId = orderedRelacionIds.get(i);
            RelacionInterna relacion = cargo.getRelacionesInternas().stream()
                    .filter(r -> r.getId().equals(relacionId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("RelacionInterna no encontrada: " + relacionId));
            newOrder.put(relacion, i);
        }

        //cargo.setRelacionesInternasOrder(newOrder);
        cargoRepository.save(cargo);
    }

}