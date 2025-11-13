package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.Dependencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DependenciaRepository extends JpaRepository<Dependencia, Long> {
}