package com.backoffice.upjuyanolja.domain.point.service;


import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationCommandService;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.coupon.exception.InsufficientPointsException;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponIssuanceGetService;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.dto.request.PointChargeRequest;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeDetailResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargePageResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeReceiptResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointSummaryResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointTotalBalanceResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointTotalDetailResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointTotalPageResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointUsageCouponReceiptResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointUsageDetailReceiptResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointUsageDetailResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointUsagePageResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointUsageReceiptResponse;
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
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
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

    private final AccommodationCommandService accommodationCommandService;
    private final CouponIssuanceGetService couponIssuanceGetService;
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
    public PointTotalBalanceResponse getPointTotalBalanceResponse(Long memberId) {
        Point memberPoint = getMemberPoint(memberId);

        return PointTotalBalanceResponse.of(memberPoint.getTotalPointBalance());
    }

    @Transactional(readOnly = true)
    public PointChargePageResponse getPointChargePageResponse(Long memberId, Pageable pageable) {
        Long pointId = getMemberPoint(memberId).getId();
        Page<PointCharges> pagePointCharges = pointChargesRepository.findPageByPointId(pointId,
            pageable);
        List<PointCharges> pointCharges = new ArrayList<>();
        for (PointCharges pointCharge : pagePointCharges) {
            pointCharges.add(pointCharge);
        }

        return PointChargePageResponse.of(new PageImpl<>(
                getPointChargeDetailResponses(pointCharges),
                pageable,
                pagePointCharges.getTotalElements()
            )
        );
    }

    @Transactional(readOnly = true)
    public PointChargeResponse getDetailChargePointResponse(Long chargeId) {
        PointCharges detailChargePoint = pointChargesRepository.findById(chargeId)
            .orElseThrow(PointNotFoundException::new);

        return PointChargeResponse.of(detailChargePoint);
    }

    @Transactional(readOnly = true)
    public PointUsagePageResponse getPointUsagePageResponse(Long memberId, Pageable pageable) {
        Long pointId = getMemberPoint(memberId).getId();
        Page<PointUsage> pagePointUsages = pointUsageRepository.findPageByPointId(
            pointId, pageable
        );
        List<PointUsage> pointUsages = new ArrayList<>();
        for (PointUsage pointusage : pagePointUsages) {
            pointUsages.add(pointusage);
        }

        return PointUsagePageResponse.of(new PageImpl<>(
                getPointUsageDetailResponses(pointUsages),
                pageable,
                pagePointUsages.getTotalElements()
            )
        );
    }

    @Transactional(readOnly = true)
    public PointTotalPageResponse getTotalPointPageResponse(Long memberId, Pageable pageable) {
        Long pointId = getMemberPoint(memberId).getId();
        List<PointChargeDetailResponse> chargeDetailResponses =
            getPointChargeDetailResponses(pointChargesRepository.findByPointId(pointId));
        List<PointUsageDetailResponse> usageDetailResponses =
            getPointUsageDetailResponses(pointUsageRepository.findByPointId(pointId));
        List<PointTotalDetailResponse> pointTotalDetailResponses = new ArrayList<>();
        List<PointTotalDetailResponse> result = new ArrayList<>();

        for (PointChargeDetailResponse charge : chargeDetailResponses) {
            pointTotalDetailResponses.add(PointTotalDetailResponse.of(
                charge.category(),
                charge.type(),
                charge.status(),
                charge.name(),
                "",
                charge.trade(),
                charge.amount(),
                charge.receipt().tradeAt(),
                charge.receipt()
            ));
        }
        for (PointUsageDetailResponse usage : usageDetailResponses) {
            pointTotalDetailResponses.add(PointTotalDetailResponse.of(
                usage.category(),
                usage.type(),
                usage.status(),
                usage.name(),
                usage.description(),
                usage.trade(),
                usage.amount(),
                usage.receipt().tradeAt(),
                usage.receipt()
            ));
        }
        pointTotalDetailResponses.sort(Comparator.comparing(PointTotalDetailResponse::date));

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = Math.min(startIndex + pageable.getPageSize(),
            pointTotalDetailResponses.size());

        for (int i = startIndex; i < endIndex; i++) {
            result.add(PointTotalDetailResponse.from(i+1, pointTotalDetailResponses.get(i)));
        }

        long total = pointTotalDetailResponses.size();

        return PointTotalPageResponse.of(
            new PageImpl<>(
                result,
                pageable,
                total
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
        updateChargePointStatus(pointCharges, PointStatus.CANCELED);
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

    public PointUsage usePointForCoupon(final Long memberId, final long totalPrice) {
        Point memberPoint = getMemberPoint(memberId);
        PointUsage resultPointUsage = createPointUsage(memberPoint, totalPrice);

        useChargePointForCoupon(totalPrice, memberPoint.getId(), memberPoint);

        return resultPointUsage;
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
            .remainPoint(0L)
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
        Point point, long orderPrice
    ) {
        PointUsage pointUsage = PointUsage.builder()
            .point(point)
            .orderPrice(orderPrice)
            .orderDate(LocalDateTime.now())
            .build();

        pointUsageRepository.save(pointUsage);

        return pointUsage;
    }

    private long getTotalChargePoint(Point point, YearMonth rangeDate) {
        return pointChargesRepository.findByPointByStatusAndRangeDate(
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
        long totalPoint =
            Optional.ofNullable(pointChargesRepository.sumTotalPaidPoint(point)).orElse(0L) +
                Optional.ofNullable(pointChargesRepository.sumTotalRemainedPoint(point)).orElse(0L);
        point.updatePointBalance(totalPoint);
        pointRepository.save(point);
    }

    private void updateChargePointStatus(PointCharges pointCharges, PointStatus pointStatus) {
        pointCharges.updatePointStatus(pointStatus);
        pointCharges.updateRefundable(false);
    }

    private void updateChargeRemainPoint(PointCharges pointCharges, long totalPrice) {
        long remainPoint = pointCharges.getChargePoint() - totalPrice;
        pointCharges.updateRemainPoint(remainPoint);
        pointChargesRepository.save(pointCharges);
    }

    private void useChargePointForCoupon(long totalPrice, Long pointChargeId, Point memberPoint) {
        List<PointCharges> pointCharges =
            pointChargesRepository.findByPointId(pointChargeId);
        long resultPoint = memberPoint.getTotalPointBalance();

        for (PointCharges pointCharge : pointCharges) {
            if (resultPoint >= totalPrice) {
                break;
            }

            switch (pointCharge.getPointStatus()) {
                case PAID:
                    resultPoint += pointCharge.getChargePoint();
                    if (pointCharge.getChargePoint() > totalPrice) {
                        updateChargePointStatus(pointCharge, PointStatus.REMAINED);
                        updateChargeRemainPoint(pointCharge, totalPrice);
                    }
                    updateChargePointStatus(pointCharge, PointStatus.USED);
                    break;
                case REMAINED:
                    resultPoint += pointCharge.getRemainPoint();
                    if (totalPrice >= resultPoint) {
                        updateChargePointStatus(pointCharge, PointStatus.USED);
                        updateChargeRemainPoint(pointCharge, pointCharge.getRemainPoint());
                    }
                    updateChargeRemainPoint(pointCharge, (resultPoint - totalPrice));
                    break;
            }

        }
        updateTotalPointBalance(memberPoint);
    }

    private List<PointChargeDetailResponse> getPointChargeDetailResponses(
        List<PointCharges> pointCharges
    ) {
        return pointCharges.stream()
            .map(pointCharge -> PointChargeDetailResponse.of(
                    pointCharge, getPointChargeCategoryAndType(pointCharge).get(0),
                    getPointChargeCategoryAndType(pointCharge).get(1),
                    getPointChargeReceiptResponse(pointCharge)
                )
            )
            .toList();
    }

    private PointChargeReceiptResponse getPointChargeReceiptResponse(
        PointCharges pointCharges) {

        switch (pointCharges.getPointStatus()) {
            case PAID:
            case USED:
            case REMAINED:
                return PointChargeReceiptResponse.of(
                    pointCharges.getOrderName(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(pointCharges.getChargeDate()),
                    pointCharges.getChargePoint()
                );
            case CANCELED:
                PointRefunds pointRefund = pointRefundsRepository.findByPointCharges(pointCharges);
                return PointChargeReceiptResponse.of(
                    pointCharges.getOrderName(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(pointRefund.getRefundDate()),
                    pointCharges.getChargePoint()
                );
            default:
                return PointChargeReceiptResponse.builder().build();
        }

    }

    private List<String> getPointChargeCategoryAndType(PointCharges pointCharges) {
        List<String> results = new ArrayList<>();

        switch (pointCharges.getPointStatus()) {
            case PAID:
                results.add(PointCategory.CHARGE.getDescription());
                results.add(PointType.POINT.getDescription());
                break;
            case CANCELED:
                results.add(PointCategory.REFUND.getDescription());
                results.add(PointType.REFUND.getDescription());
                break;
            case USED:
                results.add(PointCategory.USE.getDescription());
                results.add(PointType.POINT.getDescription());
                break;
            case REMAINED:
                results.add(PointCategory.CHARGE.getDescription());
                results.add(PointType.POINT.getDescription());
                break;
        }
        return results;

    }

    private List<PointUsageDetailResponse> getPointUsageDetailResponses(
        List<PointUsage> pointUsages) {
        return pointUsages.stream()
            .map(pointUsage -> {
                    List<CouponIssuance> couponIssuances =
                        couponIssuanceGetService.getCouponIssuanceByPointUsage(pointUsage);
                    if (couponIssuances.isEmpty()) {
                        return PointUsageDetailResponse.builder().build();
                    }
                    Map<Room, List<Coupon>> couponIssuancesMap =
                        createCouponIssuancesMap(couponIssuances);
                    CouponIssuance selectCouponIssuance = couponIssuances.get(0);
                    String accommodationName =
                        accommodationCommandService
                            .findAccommodationByRoomId(selectCouponIssuance.getRoom().getId())
                            .getName();

                    return PointUsageDetailResponse.of(
                        pointUsage,
                        getPointUsageDescription(
                            selectCouponIssuance, couponIssuancesMap.keySet().size(),
                            accommodationName
                        ),
                        getPointUsageTrade(couponIssuances),
                        pointUsage.getOrderPrice(),
                        getPointUsageReceiptResponse(
                            pointUsage, accommodationName,
                            couponIssuancesMap, couponIssuances
                        )
                    );
                }
            )
            .toList();
    }

    private Map<Room, List<Coupon>> createCouponIssuancesMap(
        List<CouponIssuance> couponIssuances
    ) {
        return couponIssuances.stream()
            .collect(Collectors.groupingBy(
                couponIssuance -> couponIssuance.getRoom(),
                Collectors.mapping(
                    couponIssuance -> couponIssuance.getCoupon(),
                    Collectors.toList()
                )
            ));
    }

    private String getPointUsageDescription(
        CouponIssuance couponIssuance, int size, String accommodationName
    ) {
        StringBuilder stringBuilder = new StringBuilder();

        if (size > 2) {
            stringBuilder.append(
                couponIssuance.getRoom().getName() + " 외 " + String.valueOf(size - 1) + "건"
            );

        } else {
            stringBuilder.append(couponIssuance.getRoom().getName());
        }

        stringBuilder.append(" | " + accommodationName);

        return stringBuilder.toString();
    }

    private long getPointUsageTrade(List<CouponIssuance> couponIssuancesMap) {
        return couponIssuancesMap.stream()
            .mapToLong(CouponIssuance::getQuantity)
            .sum();
    }

    private PointUsageReceiptResponse getPointUsageReceiptResponse(
        PointUsage pointUsages, String accommodationName,
        Map<Room, List<Coupon>> couponIssuancesMap, List<CouponIssuance> couponIssuances
    ) {
        return PointUsageReceiptResponse.of(
            getPointUsageReceiptOrderId(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(pointUsages.getOrderDate()),
            accommodationName,
            getPointUsageDetailReceiptResponse(couponIssuancesMap, couponIssuances)
        );
    }

    private String getPointUsageReceiptOrderId() {
        return "O-" + ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }

    private List<PointUsageDetailReceiptResponse> getPointUsageDetailReceiptResponse(
        Map<Room, List<Coupon>> couponIssuanceMaps, List<CouponIssuance> couponIssuances
    ) {
        return couponIssuanceMaps.entrySet().stream()
            .map(entry -> PointUsageDetailReceiptResponse.of(
                    entry.getKey().getName(),
                    getPointUsageCouponReceiptResponse(entry.getKey(), couponIssuances)
                )
            )
            .toList();
    }

    private List<PointUsageCouponReceiptResponse> getPointUsageCouponReceiptResponse(
        Room room, List<CouponIssuance> couponIssuances) {
        return couponIssuances.stream()
            .filter(couponIssuance -> couponIssuance.getRoom().equals(room))
            .map(couponIssuance -> {
                    Coupon coupon = couponIssuance.getCoupon();
                    return PointUsageCouponReceiptResponse.of(
                        coupon.getDiscount() + "원 쿠폰 | " + coupon.getDiscount() / 10 + "p",
                        couponIssuance.getQuantity(),
                        couponIssuance.getAmount()
                    );
                }
            )
            .toList();
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
