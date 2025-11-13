package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.ConfiguracionHome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionHomeRepository extends JpaRepository<ConfiguracionHome, Long> {
}