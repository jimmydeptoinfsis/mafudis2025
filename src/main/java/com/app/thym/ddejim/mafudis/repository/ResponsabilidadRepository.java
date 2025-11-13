package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.Responsabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponsabilidadRepository extends JpaRepository<Responsabilidad, Long> {
    List<Responsabilidad> findByCargoIdOrderByOrderIndexAsc(Long cargoId); // Changed "Orden" to "OrderIndex"
}