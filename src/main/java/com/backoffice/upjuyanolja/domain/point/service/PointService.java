package com.backoffice.upjuyanolja.domain.point.service;

import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeDetailResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargePageResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeReceiptResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointSummaryResponse;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCategory;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.PointRefunds;
import com.backoffice.upjuyanolja.domain.point.entity.PointType;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import com.backoffice.upjuyanolja.domain.point.repository.PointChargesRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointRefundsRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointUsageRepository;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointChargesRepository pointChargesRepository;
    private final PointRefundsRepository pointRefundsRepository;
    private final PointUsageRepository pointUsageRepository;
    private final MemberGetService memberGetService;

    @Transactional(readOnly = true)
    public PointSummaryResponse getPointSummaryResponse(Long memberId, YearMonth rangeDate) {
        Point ownerPoint = getMemberPoint(memberId);
        Long currentPoint =
            getChargePoint(ownerPoint, rangeDate) +
                getChargePoint(ownerPoint, rangeDate.minusMonths(1)) -
                getUsePoint(ownerPoint, rangeDate) -
                getUsePoint(ownerPoint, rangeDate.minusMonths(1));

        return PointSummaryResponse.of(
            getChargePoint(ownerPoint, rangeDate),
            getUsePoint(ownerPoint, rangeDate),
            currentPoint
        );
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


    public PointChargePageResponse getChargePoints(Long memberId, Pageable pageable) {
        Long pointId = getMemberPoint(memberId).getId();
        Page<PointCharges> pointCharges = pointChargesRepository.findByPointId(pointId, pageable);

        return PointChargePageResponse.of(new PageImpl<>(
                pointCharges.stream()
                    .map(pointCharge -> PointChargeDetailResponse.of(
                        pointCharge, getPointChargeCategoryAndType(pointCharge).get(0),
                        getPointChargeCategoryAndType(pointCharge).get(1),
                        getPointChargeReceiptResponse(pointCharge))
                    )
                    .toList(),
                pageable,
                pointCharges.getTotalElements()
            )
        );
    }

    private List<String> getPointChargeCategoryAndType(PointCharges pointCharges) {
        List<String> results = new ArrayList<>();

        switch (pointCharges.getPointStatus()) {
            case PAID:
                results.add(PointCategory.CHARGE.getDescription());
                results.add(PointType.POINT.getDescription());
            case CANCELED:
                results.add(PointCategory.REFUND.getDescription());
                results.add(PointType.REFUND.getDescription());
            case USED:
                results.add(PointCategory.USE.getDescription());
                results.add(PointType.POINT.getDescription());
        }
        return results;

    }

    private List<PointChargeReceiptResponse> getPointChargeReceiptResponse(
        PointCharges pointCharges) {

        switch (pointCharges.getPointStatus()) {
            case PAID:
                return Collections.singletonList(PointChargeReceiptResponse.of(
                    pointCharges.getOrderName(),
                    pointCharges.getChargeDate().toString(),
                    pointCharges.getChargePoint()
                ));
            case CANCELED:
                PointRefunds pointRefund = pointRefundsRepository.findByPointCharges(
                    pointCharges);
                return Collections.singletonList(PointChargeReceiptResponse.of(
                    pointCharges.getOrderName(),
                    pointRefund.getRefundDate().toString(),
                    pointCharges.getChargePoint()
                ));
            case USED:
                return pointUsageRepository.findByPointCharges(pointCharges).stream()
                    .map(pointUsage -> PointChargeReceiptResponse.of(
                        pointUsage.getOrderName(),
                        pointUsage.getOrderDate().toString(),
                        pointUsage.getOrderPrice()
                    ))
                    .toList();
            default:
                return Collections.emptyList();
        }

    }

}
