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
import java.util.List;
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
    private final MemberGetService memberGetService;

    public PointSummaryResponse getSummary(Long currentMemberId, YearMonth rangeDate) {
        Point memberPoint = getMemberPoint(currentMemberId);
        Long currentPoint =
            getChargePoint(memberPoint, rangeDate) +
                getChargePoint(memberPoint, rangeDate.minusMonths(1)) -
                getUsePoint(memberPoint, rangeDate) -
                getUsePoint(memberPoint, rangeDate.minusMonths(1));

        updatePoint(memberPoint, currentPoint, rangeDate);

        return PointSummaryResponse.builder()
            .chargePoint(getChargePoint(memberPoint, rangeDate))
            .usePoint(getUsePoint(memberPoint, rangeDate))
            .currentPoint(currentPoint)
            .build();
    }

    private Point getMemberPoint(Long currentMemberId) {
        return pointRepository.findByMemberId(currentMemberId)
            .orElseGet(() -> createPoint(currentMemberId));
    }

    private Point createPoint(Long currentMemberId) {
        Point newPoint = Point.builder()
            .pointBalance(0)
            .standardDate(YearMonth.now())
            .member(memberGetService.getMemberById(currentMemberId))
            .build();

        pointRepository.save(newPoint);

        return newPoint;
    }

    private void updatePoint(Point point, Long pointBalance, YearMonth rangeDate){
        point.updatePoint(pointBalance,rangeDate);
        pointRepository.save(point);
    }

    private long getChargePoint(Point point, YearMonth rangeDate) {
        return pointChargesRepository.findByPointAndRefundableAndChargeDateWithin(
                point, rangeDate
            ).stream()
            .mapToLong(PointCharges::getChargePoint)
            .sum();
    }

    private long getUsePoint(Point point, YearMonth rangeDate) {
        List<PointUsage> byPointAndChargeDateWithin = pointUsageRepository.findByPointAndChargeDateWithin(
            point, rangeDate
        );
        return pointUsageRepository.findByPointAndChargeDateWithin(
                point, rangeDate
            ).stream()
            .mapToLong(PointUsage::getOrderPrice)
            .sum();
    }
}
