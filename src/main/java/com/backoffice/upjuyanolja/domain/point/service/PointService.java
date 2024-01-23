package com.backoffice.upjuyanolja.domain.point.service;

import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointSummaryResponse;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import com.backoffice.upjuyanolja.domain.point.repository.PointChargesRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointUsageRepository;
import java.time.YearMonth;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointChargesRepository pointChargesRepository;
    private final PointUsageRepository pointUsageRepository;
    private final MemberGetService memberGetService;

    public PointSummaryResponse getSummary(Long memberId, YearMonth rangeDate) {
        Point ownerPoint = getMemberPoint(memberId);
        Long currentPoint =
            getChargePoint(ownerPoint, rangeDate) +
                getChargePoint(ownerPoint, rangeDate.minusMonths(1)) -
                getUsePoint(ownerPoint, rangeDate) -
                getUsePoint(ownerPoint, rangeDate.minusMonths(1));

        return PointSummaryResponse.builder()
            .chargePoint(getChargePoint(ownerPoint, rangeDate))
            .usePoint(getUsePoint(ownerPoint, rangeDate))
            .currentPoint(currentPoint)
            .build();
    }

    private Point getMemberPoint(Long memberId) {
        return pointRepository.findByMemberId(memberId)
            .orElseGet(() -> createPoint(memberId));
    }

    private Point createPoint(Long memberId) {
        Point newPoint = Point.builder()
            .totalPointBalance(0)
            .member(memberGetService.getMemberById(memberId))
            .build();

        pointRepository.save(newPoint);

        return newPoint;
    }

    private void updateTotalPoint(Point point) {
        long chargeSum = Optional.of(pointChargesRepository.sumChargePointByRefundable(point))
            .orElse(0L);
        point.updatePoint(chargeSum);
        pointRepository.save(point);
    }

    private long getChargePoint(Point point, YearMonth rangeDate) {
        return pointChargesRepository.findByPointAndRefundableAndRangeDate(
                point, rangeDate
            ).stream()
            .mapToLong(PointCharges::getChargePoint)
            .sum();
    }

    private long getUsePoint(Point point, YearMonth rangeDate) {
        return pointUsageRepository.findByPointAndRangeDate(
                point, rangeDate
            ).stream()
            .mapToLong(PointUsage::getOrderPrice)
            .sum();
    }
}
