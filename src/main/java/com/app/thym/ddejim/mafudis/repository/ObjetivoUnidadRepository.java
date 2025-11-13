package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.ObjetivoUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ObjetivoUnidadRepository extends JpaRepository<ObjetivoUnidad, Long> {
    List<ObjetivoUnidad> findByUnidadId(Long unidadId);
    List<ObjetivoUnidad> findByUnidadIdOrderByOrderIndexAsc(Long unidadId);
    @Query("SELECT MAX(o.orderIndex) FROM ObjetivoUnidad o WHERE o.unidad.id = :unidadId")
    Integer findMaxOrderIndexByUnidadId(Long unidadId);
}