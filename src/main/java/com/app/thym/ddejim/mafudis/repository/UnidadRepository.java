package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {
}