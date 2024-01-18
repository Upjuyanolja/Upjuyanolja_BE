package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.QPointCharges;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointChargesCustomRepositoryImpl implements PointChargesCustomRepository {

    private final JPAQueryFactory query;
    private final QPointCharges qPointCharges = QPointCharges.pointCharges;

    @Override
    public List<PointCharges> findByPointAndRefundableAndChargeDateWithin(
        Point point, YearMonth rangeDate
    ) {
        List<PointCharges> result = getPointCharges(point, rangeDate);
        return result;
    }

    private List<PointCharges> getPointCharges(
        Point point, YearMonth rangeDate
    ) {
        return query.selectFrom(qPointCharges)
            .where(qPointCharges.point.eq(point)
                .and(qPointCharges.refundable.isTrue())
                .and(eqChargeDate(rangeDate))
            )
            .fetch();
    }

    private BooleanExpression eqChargeDate(YearMonth rangeDate) {
        BooleanExpression yearExpression =
            qPointCharges.chargeDate.year().eq(rangeDate.getYear());
        BooleanExpression monthExpression =
            qPointCharges.chargeDate.month().eq(rangeDate.getMonthValue());

        return yearExpression.and(monthExpression);
    }
}
