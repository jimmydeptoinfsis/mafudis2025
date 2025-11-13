package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.DocumentosGenera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentosGeneraRepository extends JpaRepository<DocumentosGenera, Long> {
    List<DocumentosGenera> findByCargoIdOrderByOrderIndexAsc(Long cargoId);
}
