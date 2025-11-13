package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.NivelJerarquico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NivelJerarquicoRepository extends JpaRepository<NivelJerarquico, Long> {
}