package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.RelacionExterna;
import com.app.thym.ddejim.mafudis.repository.RelacionExternaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RelacionExternaService {

    private static final Logger LOGGER = Logger.getLogger(RelacionExternaService.class.getName());

    @Autowired
    private RelacionExternaRepository relacionExternaRepository;

    @Transactional(readOnly = true)
    public List<RelacionExterna> findAll() {
        LOGGER.info("Fetching all RelacionExterna sorted by orderIndex");
        return relacionExternaRepository.findAllByOrderByOrderIndexAsc();
    }

    @Transactional(readOnly = true)
    public Optional<RelacionExterna> findById(Long id) {
        LOGGER.info("Fetching RelacionExterna by ID: " + id);
        return relacionExternaRepository.findById(id);
    }

    @Transactional
    public RelacionExterna save(RelacionExterna relacionExterna) {
        if (relacionExterna == null) {
            LOGGER.severe("RelacionExterna cannot be null");
            throw new IllegalArgumentException("RelacionExterna cannot be null");
        }
        if (relacionExterna.getOrderIndex() == null) {
            List<RelacionExterna> existing = findAll();
            relacionExterna.setOrderIndex(existing.size());
            LOGGER.info("Assigned orderIndex: " + existing.size() + " to new RelacionExterna");
        }
        LOGGER.info("Saving RelacionExterna: " + relacionExterna.getNombre());
        return relacionExternaRepository.save(relacionExterna);
    }

    @Transactional
    public void deleteById(Long id) {
        LOGGER.info("Deleting RelacionExterna ID: " + id);
        relacionExternaRepository.deleteById(id);
        reorderRelaciones();
    }

    @Transactional(readOnly = true)
    public Set<RelacionExterna> findByIds(List<Long> ids) {
        LOGGER.info("Fetching RelacionExterna by IDs: " + ids);
        return relacionExternaRepository.findAllById(ids).stream().collect(Collectors.toSet());
    }

    @Transactional
    public void updateOrder(List<Long> orderedIds) {
        LOGGER.info("Updating order with IDs: " + orderedIds);
        List<RelacionExterna> relaciones = relacionExternaRepository.findAllById(orderedIds);
        AtomicInteger index = new AtomicInteger(0);
        orderedIds.forEach(id -> {
            int orderIndex = index.getAndIncrement();
            relaciones.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .ifPresent(r -> {
                        r.setOrderIndex(orderIndex);
                        LOGGER.info("Set orderIndex: " + orderIndex + " for RelacionExterna ID: " + id);
                    });
        });
        relacionExternaRepository.saveAll(relaciones);
        LOGGER.info("Saved updated order for " + relaciones.size() + " RelacionExterna items");
    }

    @Transactional
    private void reorderRelaciones() {
        List<RelacionExterna> relaciones = findAll();
        for (int i = 0; i < relaciones.size(); i++) {
            relaciones.get(i).setOrderIndex(i);
        }
        relacionExternaRepository.saveAll(relaciones);
        LOGGER.info("Reordered all relaciones: " + relaciones.size() + " items");
    }
}