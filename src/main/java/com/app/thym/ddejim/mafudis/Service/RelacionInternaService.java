package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.RelacionInterna;
import com.app.thym.ddejim.mafudis.repository.RelacionInternaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RelacionInternaService {

    private final RelacionInternaRepository relacionRepo;

    public RelacionInternaService(RelacionInternaRepository relacionRepo) {
        this.relacionRepo = relacionRepo;
    }

    public List<RelacionInterna> listar() {
        return relacionRepo.findAll();
    }

    public Optional<RelacionInterna> obtenerPorId(Long id) {
        return relacionRepo.findById(id);
    }

    public RelacionInterna guardar(RelacionInterna relacion) {
        return relacionRepo.save(relacion);
    }

    public void eliminar(Long id) {
        relacionRepo.deleteById(id);
    }

    public boolean existeRelacion(Long origenId, Long destinoId) {
        return relacionRepo.existsByOrigenIdAndDestinoId(origenId, destinoId);
    }

    public List<RelacionInterna> findByOrigenId(Long origenId) {
        return relacionRepo.findByOrigenId(origenId);
    }

    public List<RelacionInterna> findByDestinoId(Long destinoId) {
        return relacionRepo.findByDestinoId(destinoId);
    }
}
