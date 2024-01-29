package com.backoffice.upjuyanolja.domain.point.repository;

import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.PointStatus;
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
    public Long sumTotalPaidPoint(Point point){
        return query.select(qPointCharges.chargePoint.sum())
            .from(qPointCharges)
            .where(qPointCharges.pointStatus.eq(PointStatus.PAID))
            .fetchFirst();
    }

    @Override
    public Long sumTotalRemainedPoint(Point point){
        return query.select(qPointCharges.remainPoint.sum())
            .from(qPointCharges)
            .where(qPointCharges.pointStatus.eq(PointStatus.REMAINED))
            .fetchFirst();
    }

    @Override
    public List<PointCharges> findByPointByStatusAndRangeDate(
        Point point, YearMonth rangeDate
    ) {
        return query.selectFrom(qPointCharges)
            .where(isPointAvailableStatus(point)
                .and(eqChargeDate(rangeDate))
            )
            .fetch();
    }


    private BooleanExpression isPointAvailableStatus(Point point) {
        return qPointCharges.point.eq(point)
            .and(qPointCharges.pointStatus.ne(PointStatus.CANCELED));
    }

    private BooleanExpression eqChargeDate(YearMonth rangeDate) {
        BooleanExpression yearExpression =
            qPointCharges.chargeDate.year().eq(rangeDate.getYear());
        BooleanExpression monthExpression =
            qPointCharges.chargeDate.month().eq(rangeDate.getMonthValue());

        return yearExpression.and(monthExpression);
    }
}
