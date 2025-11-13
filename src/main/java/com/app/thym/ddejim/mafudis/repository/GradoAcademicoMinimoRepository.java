package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.GradoAcademicoMinimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradoAcademicoMinimoRepository extends JpaRepository<GradoAcademicoMinimo, Long> {
    List<GradoAcademicoMinimo> findByCargoIdOrderByOrderIndexAsc(Long cargoId);
}
