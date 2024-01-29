package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointChargesRepository extends JpaRepository<PointCharges, Long>,
    PointChargesCustomRepository {

    Page<PointCharges> findPageByPointIdOrderByIdDesc(Long pointId, Pageable pageable);

    List<PointCharges> findByPointId(Long pointId);

}
