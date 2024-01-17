package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import com.backoffice.upjuyanolja.domain.point.entity.QPointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.QPointUsage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointUsageCustomRepositoryImpl implements PointUsageCustomRepository {

    private final JPAQueryFactory query;
    private final QPointUsage qPointUsage = QPointUsage.pointUsage;

    @Override
    public List<PointUsage> findByPointAndChargeDateWithin(
        Point point, YearMonth rangeDate
    ) {
        List<PointUsage> result = getPointUsages(point, rangeDate);
        return result;
    }

    private List<PointUsage> getPointUsages(
        Point point, YearMonth rangeDate
    ) {
        return query.selectFrom(qPointUsage)
            .where(qPointUsage.point.eq(point)
                .and(eqChargeDate(rangeDate))
            )
            .fetch();
    }

    private BooleanExpression eqChargeDate(YearMonth rangeDate) {
        BooleanExpression yearExpression =
            qPointUsage.orderDate.year().eq(rangeDate.getYear());
        BooleanExpression monthExpression =
            qPointUsage.orderDate.month().eq(rangeDate.getMonthValue());

        return yearExpression.and(monthExpression);
    }
}
