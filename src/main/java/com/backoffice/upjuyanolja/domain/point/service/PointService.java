package com.backoffice.upjuyanolja.domain.point.service;

import static com.backoffice.upjuyanolja.domain.point.entity.PointType.CHARGE;

import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.dto.request.PointChargeRequest;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointChargeResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.PointSummaryResponse;
import com.backoffice.upjuyanolja.domain.point.dto.response.TossChargeResponse;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointCharges;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import com.backoffice.upjuyanolja.domain.point.exception.PaymentAuthorizationFailedException;
import com.backoffice.upjuyanolja.domain.point.exception.TossApiErrorException;
import com.backoffice.upjuyanolja.domain.point.repository.PointChargesRepository;
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
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointChargesRepository pointChargesRepository;
    private final PointUsageRepository pointUsageRepository;
    private final MemberGetService memberGetService;
    private final ObjectMapper objectMapper;


    @Value("${point.toss.url}")
    private String tossBaseUrl;
    @Value("${point.toss.secret-key}")
    private String tossKey;

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

    public PointChargeResponse getChargePointResponse(Long memberId, PointChargeRequest request) {
        Point memberPoint = getMemberPoint(memberId);
        TossChargeResponse tossResponse = getTossChargeResponse(request);

        validatePointChargeRequest(request, tossResponse);
        PointCharges chargePoint = createChargePoint(tossResponse);
        updateTotalPoint(memberPoint);

        return PointChargeResponse.of(chargePoint);
    }

    private PointCharges createChargePoint(
        TossChargeResponse tossResponse
    ) {
        return PointCharges.builder()
            .pointType(CHARGE)
            .paymentKey(tossResponse.paymentKey())
            .orderName(tossResponse.orderId())
            .chargePoint(tossResponse.amount())
            .chargeDate(LocalDateTime.parse(tossResponse.approvedAt()))
            .endDate(LocalDateTime.parse(tossResponse.approvedAt()).plusDays(7))
            .refundable(true)
            .build();
    }

    private void updateTotalPoint(Point point) {
        long chargeSum = Optional.of(pointChargesRepository.sumChargePointByRefundable(point))
            .orElse(0L);
        point.updatePoint(chargeSum);
        pointRepository.save(point);
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

    private TossChargeResponse getTossChargeResponse(PointChargeRequest request) {

        HttpResponse<String> response;

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                .header("Authorization", "Basic " + createTossAuthorizations())
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(
                    "{\"paymentKey\":\"" + request.paymentKey() + "\",\"amount\":\""
                        + request.amount() + "\",\"orderId\":\"" + request.orderId() + "\"}"))
                .build();

            response = HttpClient.newHttpClient()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(),
                TossChargeResponse.class);

        } catch (IOException | InterruptedException e) {
            throw new TossApiErrorException();
        }

    }

    private void validatePointChargeRequest(
        PointChargeRequest request, TossChargeResponse tossResponse
    ) {
        if (!request.orderId().equals(tossResponse.orderId()) ||
            !request.paymentKey().equals(tossResponse.paymentKey()) ||
            request.amount() != tossResponse.amount()) {
            throw new PaymentAuthorizationFailedException();
        }
    }
}
