package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.RelacionExterna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelacionExternaRepository extends JpaRepository<RelacionExterna, Long> {
    @Query("SELECT r FROM RelacionExterna r ORDER BY r.orderIndex ASC")
    List<RelacionExterna> findAllByOrderByOrderIndexAsc();
}