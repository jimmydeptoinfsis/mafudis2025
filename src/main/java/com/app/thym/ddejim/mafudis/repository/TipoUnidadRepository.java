package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.TipoUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoUnidadRepository extends JpaRepository<TipoUnidad, Long> {
}