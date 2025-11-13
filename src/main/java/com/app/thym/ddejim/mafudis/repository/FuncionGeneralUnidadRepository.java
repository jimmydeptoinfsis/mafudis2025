package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.FuncionGeneralUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FuncionGeneralUnidadRepository extends JpaRepository<FuncionGeneralUnidad, Long> {
    // Método para obtener todas las funciones de una unidad.
    List<FuncionGeneralUnidad> findByUnidadId(Long unidadId);

    //Obtiene las funciones ya ordenadas por su índice.
    List<FuncionGeneralUnidad> findByUnidadIdOrderByOrderIndexAsc(Long unidadId);

    //Calcula el valor máximo del índice de orden para una unidad.
    @Query("SELECT MAX(f.orderIndex) FROM FuncionGeneralUnidad f WHERE f.unidad.id = :unidadId")
    Integer findMaxOrderIndexByUnidadId(Long unidadId);
}