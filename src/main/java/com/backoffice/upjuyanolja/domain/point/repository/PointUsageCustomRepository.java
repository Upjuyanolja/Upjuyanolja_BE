package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import java.time.YearMonth;
import java.util.List;

public interface PointUsageCustomRepository {

    List<PointUsage> findByPointAndRangeDate(Point point, YearMonth rangeDate);
}
