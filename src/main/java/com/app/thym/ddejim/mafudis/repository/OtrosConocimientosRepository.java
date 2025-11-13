package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.OtrosConocimientos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtrosConocimientosRepository extends JpaRepository<OtrosConocimientos, Long> {
    List<OtrosConocimientos> findByCargoIdOrderByOrderIndexAsc(Long cargoId);
}