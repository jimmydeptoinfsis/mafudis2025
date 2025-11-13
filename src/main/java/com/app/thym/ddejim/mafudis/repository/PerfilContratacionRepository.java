package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.PerfilContratacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfilContratacionRepository extends JpaRepository<PerfilContratacion, Long> {
    List<PerfilContratacion> findByCargoIdOrderByOrderIndexAsc(Long cargoId);
}
