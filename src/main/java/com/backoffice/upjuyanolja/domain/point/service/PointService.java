package com.backoffice.upjuyanolja.domain.point.service;

import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.member.service.OwnerGetService;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointChargesRepository pointChargesRepository;
    private final PointUsageRepository pointUsageRepository;
    private final OwnerGetService ownerGetService;

    public PointSummaryResponse getSummary(Long ownerId, YearMonth rangeDate) {
        Point ownerPoint = getownerPoint(ownerId);
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

    private Point getownerPoint(Long ownerId) {
        return pointRepository.findByOwnerId(ownerId)
            .orElseGet(() -> createPoint(ownerId));
    }

    private Point createPoint(Long ownerId) {
        Point newPoint = Point.builder()
            .totalPointBalance(0)
            .owner(ownerGetService.getOwnerById(ownerId))
            .build();

        pointRepository.save(newPoint);
        getAvailablePoint(newPoint);

        return newPoint;
    }

    private void getAvailablePoint(Point point) {
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
