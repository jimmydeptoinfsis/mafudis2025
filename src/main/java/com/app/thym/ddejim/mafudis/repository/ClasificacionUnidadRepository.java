package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.ClasificacionUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClasificacionUnidadRepository extends JpaRepository<ClasificacionUnidad, Long> {
}