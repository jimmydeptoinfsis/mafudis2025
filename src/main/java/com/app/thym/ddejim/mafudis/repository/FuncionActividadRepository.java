package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.FuncionActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionActividadRepository extends JpaRepository<FuncionActividad, Long> {
    List<FuncionActividad> findByCargoIdOrderByOrderIndexAsc(Long cargoId);
}
