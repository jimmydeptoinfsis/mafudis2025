package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.DocumentosGenera;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.DocumentosGeneraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentosGeneraService {
    @Autowired
    private DocumentosGeneraRepository documentosGeneraRepository;

    @Autowired
    private CargoRepository cargoRepository;

    public List<DocumentosGenera> findByCargoId(Long cargoId) {
        return documentosGeneraRepository.findByCargoIdOrderByOrderIndexAsc(cargoId);
    }

    public Optional<DocumentosGenera> findById(Long id) {
        return documentosGeneraRepository.findById(id);
    }

    @Transactional
    public DocumentosGenera save(DocumentosGenera documentosGenera) {
        if (documentosGenera == null) {
            throw new IllegalArgumentException("DocumentosGenera cannot be null");
        }
        if (documentosGenera.getCargo() == null) {
            throw new IllegalArgumentException("Cargo cannot be null in DocumentosGenera");
        }
        if (documentosGenera.getCargo().getId() == null) {
            throw new IllegalStateException("Cargo ID cannot be null when saving DocumentosGenera");
        }

        // NUEVA LÓGICA: Distinguir entre crear y actualizar
        if (documentosGenera.getId() != null) {
            // EDITANDO: Preservar el orderIndex existente
            Optional<DocumentosGenera> existente = findById(documentosGenera.getId());
            if (existente.isPresent()) {
                documentosGenera.setOrderIndex(existente.get().getOrderIndex());
            }
        } else {
            // CREANDO: Asignar nuevo orderIndex al final
            if (documentosGenera.getOrderIndex() == null) {
                List<DocumentosGenera> existing = findByCargoId(documentosGenera.getCargo().getId());
                documentosGenera.setOrderIndex(existing.size());
            }
        }

        return documentosGeneraRepository.save(documentosGenera);
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<DocumentosGenera> documentosGenera = findById(id);
        if (documentosGenera.isPresent()) {
            Cargo cargo = documentosGenera.get().getCargo();
            if (cargo == null) {
                throw new IllegalStateException("Cargo is null for documentosGenera ID: " + id);
            }
            Long cargoId = cargo.getId();
            documentosGeneraRepository.deleteById(id);
            reorderDocumentosGenera(cargoId);
        }
    }

    @Transactional
    public void updateOrder(Long cargoId, List<Long> orderedIds) {
        List<DocumentosGenera> documentosGenera = findByCargoId(cargoId);
        Map<Long, DocumentosGenera> documentosMap = documentosGenera.stream()
                .collect(Collectors.toMap(DocumentosGenera::getId, doc -> doc));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long docId = orderedIds.get(i);
            DocumentosGenera documento = documentosMap.get(docId);
            if (documento != null) {
                documento.setOrderIndex(i);
            }
        }

        documentosGeneraRepository.saveAll(documentosMap.values());
        documentosGeneraRepository.flush(); // Asegurar persistencia inmediata
    }

    private void reorderDocumentosGenera(Long cargoId) {
        List<DocumentosGenera> documentosGenera = findByCargoId(cargoId);
        for (int i = 0; i < documentosGenera.size(); i++) {
            documentosGenera.get(i).setOrderIndex(i);
        }
        documentosGeneraRepository.saveAll(documentosGenera);
    }
}
