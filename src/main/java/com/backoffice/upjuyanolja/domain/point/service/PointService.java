package com.backoffice.upjuyanolja.domain.point.service;


import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.coupon.exception.InsufficientPointsException;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.dto.request.PointChargeRequest;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeDetailResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargePageResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeReceiptResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointSummaryResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointTotalBalanceResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.TossResponse;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCategory;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.PointRefunds;
import com.backoffice.upjuyanolja.domain.point.entity.PointStatus;
import com.backoffice.upjuyanolja.domain.point.entity.PointType;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import com.backoffice.upjuyanolja.domain.point.exception.PaymentAuthorizationFailedException;
import com.backoffice.upjuyanolja.domain.point.exception.PointNotFoundException;
import com.backoffice.upjuyanolja.domain.point.exception.TossApiErrorException;
import com.backoffice.upjuyanolja.domain.point.exception.WrongRefundInfoException;
import com.backoffice.upjuyanolja.domain.point.repository.PointChargesRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointRefundsRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
import com.backoffice.upjuyanolja.domain.point.repository.PointUsageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final AccommodationQueryUseCase accommodationQueryUseCase;
    private final MemberGetService memberGetService;
    private final ObjectMapper objectMapper;

    @Value("${point.toss.url}")
    private String tossBaseUrl;
    @Value("${point.toss.secret-key}")
    private String tossKey;


    @Transactional(readOnly = true)
    public PointSummaryResponse getPointSummaryResponse(Long memberId, YearMonth rangeDate) {
        Point memberPoint = getMemberPoint(memberId);
        Long currentPoint =
            getTotalChargePoint(memberPoint, rangeDate) +
                getTotalChargePoint(memberPoint, rangeDate.minusMonths(1)) -
                getTotalUsePoint(memberPoint, rangeDate) -
                getTotalUsePoint(memberPoint, rangeDate.minusMonths(1));

        return PointSummaryResponse.of(
            getTotalChargePoint(memberPoint, rangeDate),
            getTotalUsePoint(memberPoint, rangeDate),
            currentPoint
        );
    }

    @Transactional(readOnly = true)
    public PointTotalBalanceResponse getPointTotalBalanceResponse(Long memberId){
        Point memberPoint = getMemberPoint(memberId);

        return PointTotalBalanceResponse.of(memberPoint.getTotalPointBalance());
    }

    @Transactional(readOnly = true)
    public PointChargeResponse getDetailChargePointResponse(Long chargeId) {
        PointCharges detailchargePoint = pointChargesRepository.findById(chargeId)
            .orElseThrow(PointNotFoundException::new);

        return PointChargeResponse.of(detailchargePoint);
    }

    @Transactional(readOnly = true)
    public PointChargePageResponse getPointChargePageResponse(Long memberId, Pageable pageable) {
        Long pointId = getMemberPoint(memberId).getId();
        Page<PointCharges> pointCharges = pointChargesRepository.findPageByPointId(pointId,
            pageable);

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

    public PointChargeResponse chargePoint(Long memberId, PointChargeRequest request) {
        Point memberPoint = getMemberPoint(memberId);
        TossResponse tossResponse = getTossChargeResponse(request);

        validatePointChargeRequest(request, tossResponse);
        PointCharges chargePoint = createPointCharge(memberPoint, tossResponse);
        updateTotalPointBalance(memberPoint);

        return PointChargeResponse.of(chargePoint);
    }

    public void refundPoint(Long chargeId) {
        PointCharges pointCharges = pointChargesRepository.findById(chargeId)
            .orElseThrow(PointNotFoundException::new);
        TossResponse tossResponse = getTossRefundResponse(pointCharges.getPaymentKey());

        validatePointRefund(pointCharges);
        updateChargePointRefundStatus(pointCharges);
        updateTotalPointBalance(pointCharges.getPoint());
        createPointRefund(pointCharges, tossResponse);

    }

    @Transactional(readOnly = true)
    public void validatePoint(final Long memberId, final long requestPointBalance) {
        Point memberPoint = getMemberPoint(memberId);

        if (memberPoint.getTotalPointBalance() < requestPointBalance) {
            throw new InsufficientPointsException();
        }
    }

    public void usePointForCoupon(
        final Long memberId, final Long accommodationId, final long couponPrice
    ) {
        Point memberPoint = getMemberPoint(memberId);
        List<PointCharges> pointCharges =
            pointChargesRepository.findByPointId(memberPoint.getId());
        Accommodation couponAccommodation =
            accommodationQueryUseCase.getAccommodationById(accommodationId);
        long resultPoint = 0;

        for (PointCharges pointCharge : pointCharges) {
            if (pointCharge.getPointStatus() != PointStatus.PAID) {
                continue;
            }
            resultPoint += pointCharge.getChargePoint();
            createPointUsage(pointCharge, couponAccommodation);
            updateChargePointUseStatus(pointCharge);
            if (resultPoint >= couponPrice) {
                break;
            }
        }

        resultPoint =  memberPoint.getTotalPointBalance() - couponPrice;
        memberPoint.updatePointBalance(resultPoint);
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

    private PointCharges createPointCharge(
        Point point, TossResponse tossResponse
    ) {

        PointCharges pointCharges = PointCharges.builder()
            .point(point)
            .pointStatus(PointStatus.PAID)
            .paymentKey(tossResponse.paymentKey())
            .orderName(tossResponse.orderId())
            .chargePoint(tossResponse.totalAmount())
            .chargeDate(ZonedDateTime.parse(tossResponse.approvedAt()).toLocalDateTime())
            .endDate(ZonedDateTime.parse(tossResponse.approvedAt()).toLocalDateTime().plusDays(7))
            .refundable(true)
            .build();

        pointChargesRepository.save(pointCharges);

        return pointCharges;
    }

    private PointRefunds createPointRefund(PointCharges pointCharges, TossResponse tossResponse) {

        PointRefunds pointRefunds = PointRefunds.builder()
            .pointId(pointCharges.getPoint().getId())
            .pointCharges(pointCharges)
            .refundDate(ZonedDateTime.parse(tossResponse.approvedAt()).toLocalDateTime())
            .build();

        pointRefundsRepository.save(pointRefunds);

        return pointRefunds;
    }

    private PointUsage createPointUsage(
        PointCharges pointCharges, Accommodation accommodation
    ) {
        PointUsage pointUsage = PointUsage.builder()
            .pointId(pointCharges.getPoint().getId())
            .pointcharges(pointCharges)
            .orderPrice(pointCharges.getChargePoint())
            .orderDate(LocalDateTime.now())
            .accommodation(accommodation)
            .build();

        pointUsageRepository.save(pointUsage);

        return pointUsage;
    }

    private long getTotalChargePoint(Point point, YearMonth rangeDate) {
        return pointChargesRepository.findByPointAndRefundableAndRangeDate(
                point, rangeDate
            ).stream()
            .mapToLong(PointCharges::getChargePoint)
            .sum();
    }

    private long getTotalUsePoint(Point point, YearMonth rangeDate) {
        return pointUsageRepository.findByPointAndRangeDate(
                point, rangeDate
            ).stream()
            .mapToLong(PointUsage::getOrderPrice)
            .sum();
    }

    private void updateTotalPointBalance(Point point) {
        long totalPoint = Optional.of(pointChargesRepository.sumChargePointByPaidStatus(point))
            .orElse(0L);
        point.updatePointBalance(totalPoint);
        pointRepository.save(point);
    }

    private void updateChargePointRefundStatus(PointCharges pointCharges) {
        pointCharges.updatePointStatus(PointStatus.CANCELED);
    }

    private void updateChargePointUseStatus(PointCharges pointCharges) {
        pointCharges.updatePointStatus(PointStatus.USED);
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
                        pointCharges.getOrderName(),
                        pointUsage.getOrderDate().toString(),
                        pointUsage.getOrderPrice()
                    ))
                    .toList();
            default:
                return Collections.emptyList();
        }

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

    private String createTossAuthorizations() {
        String authorizations;

        try {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode((tossKey + ":").getBytes("UTF-8"));
            authorizations = new String(encodedBytes, 0, encodedBytes.length);
        } catch (IOException e) {
            throw new TossApiErrorException();
        }

        return authorizations;
    }

    private TossResponse getTossChargeResponse(PointChargeRequest request) {

        HttpResponse<String> response;

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(tossBaseUrl + "confirm"))
                .header("Authorization", "Basic " + createTossAuthorizations())
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(
                    "{\"paymentKey\":\"" + request.paymentKey() + "\",\"amount\":\""
                        + request.amount() + "\",\"orderId\":\"" + request.orderId() + "\"}"))
                .build();

            response = HttpClient.newHttpClient()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), TossResponse.class);

        } catch (IOException | InterruptedException e) {
            throw new TossApiErrorException();
        }

    }

    private TossResponse getTossRefundResponse(String paymentKey) {

        HttpResponse<String> response;

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(tossBaseUrl + paymentKey + "/cancel"))
                .header("Authorization", "Basic " + createTossAuthorizations())
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(
                    "{\"cancelReason\":\"" + "고객 변심" + "\"}"))
                .build();

            response = HttpClient.newHttpClient()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), TossResponse.class);

        } catch (IOException | InterruptedException e) {
            throw new TossApiErrorException();
        }

    }

    private void validatePointChargeRequest(
        PointChargeRequest request, TossResponse tossResponse
    ) {
        if (!request.orderId().equals(tossResponse.orderId()) ||
            !request.paymentKey().equals(tossResponse.paymentKey()) ||
            request.amount() != tossResponse.totalAmount()) {
            throw new PaymentAuthorizationFailedException();
        }
    }

    private void validatePointRefund(PointCharges pointCharges) {
        Point point = pointCharges.getPoint();
        if (pointCharges.getChargePoint() > point.getTotalPointBalance() ||
            pointCharges.getPointStatus() != PointStatus.PAID) {
            throw new WrongRefundInfoException();
        }
    }

}
