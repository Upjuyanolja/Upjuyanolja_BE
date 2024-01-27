package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointUsageRepository extends JpaRepository<PointUsage, Long>,
    PointUsageCustomRepository {

    Page<PointUsage> findPageByPointId(Long pointId, Pageable pageable);

}
