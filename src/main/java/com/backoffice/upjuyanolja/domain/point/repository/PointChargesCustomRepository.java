package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import java.time.YearMonth;
import java.util.List;

public interface PointChargesCustomRepository {

    Long sumChargePointByRefundable(Point point);

    List<PointCharges> findByPointAndRefundableAndRangeDate(Point point,
        YearMonth rangeDate);
}
