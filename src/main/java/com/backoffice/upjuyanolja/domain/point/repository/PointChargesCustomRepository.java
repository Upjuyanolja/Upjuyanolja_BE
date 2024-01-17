package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PointChargesCustomRepository {

    List<PointCharges> findByPointAndRefundableAndChargeDateWithin(Point point,
        YearMonth rangeDate);
}
