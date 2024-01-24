package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.PointRefunds;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRefundsRepository extends JpaRepository<PointRefunds, Long> {

    Optional<PointRefunds> findByPointCharges(PointCharges pointCharges);
}
