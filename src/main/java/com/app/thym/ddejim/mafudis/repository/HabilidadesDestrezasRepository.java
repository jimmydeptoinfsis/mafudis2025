package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.HabilidadesDestrezas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabilidadesDestrezasRepository extends JpaRepository<HabilidadesDestrezas, Long> {
    List<HabilidadesDestrezas> findByCargoIdOrderByOrderIndexAsc(Long cargoId);
}
