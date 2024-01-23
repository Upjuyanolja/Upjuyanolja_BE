package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointChargesRepository extends JpaRepository<PointCharges, Long>,
    PointChargesCustomRepository {

}
