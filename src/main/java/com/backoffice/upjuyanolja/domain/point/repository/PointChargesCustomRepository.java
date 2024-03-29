package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import java.time.YearMonth;
import java.util.List;

public interface PointChargesCustomRepository {

    Long sumTotalPaidPoint(Point point);

    Long sumTotalRemainedPoint(Point point);

    List<PointCharges> findByPointByStatusAndRangeDate(Point point,
        YearMonth rangeDate);
}
