package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.RelacionInterna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelacionInternaRepository extends JpaRepository<RelacionInterna, Long> {
    boolean existsByOrigenIdAndDestinoId(Long origenId, Long destinoId);
    List<RelacionInterna> findByOrigenId(Long origenId);
    List<RelacionInterna> findByDestinoId(Long destinoId);
    List<RelacionInterna> findByOrigenIdIn(List<Long> origenIds);
    List<RelacionInterna> findByDestinoIdIn(List<Long> destinoIds);
}