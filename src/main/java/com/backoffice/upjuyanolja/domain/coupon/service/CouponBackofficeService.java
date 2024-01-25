package com.backoffice.upjuyanolja.domain.coupon.service;

import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.isRightDiscount;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponAddInfos;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponAddRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponDeleteRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponModifyInfos;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponModifyRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponRoomsRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponInfo;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageQueryDto;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageRooms;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.exception.InsufficientPointsException;
import com.backoffice.upjuyanolja.domain.coupon.exception.InvalidCouponInfoException;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponIssuanceRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.exception.PointNotFoundException;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CouponBackofficeService {

    private final CouponRepository couponRepository;
    private final RoomRepository roomRepository;
    private final PointRepository pointRepository;
    private final AccommodationRepository accommodationRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;

    // 쿠폰 만들기 View Response
    public CouponMakeViewResponse getRoomsByAccommodation(Long accommodationId) {
        return couponRepository.findRoomsByAccommodationId(accommodationId);
    }

    // 쿠폰 만들기
    public void createCoupon(
        final CouponMakeRequest couponMakeRequest, final Long memberId
    ) {
        final long totalPoints = couponMakeRequest.totalPoints();
        Point point = validationPoint(memberId, totalPoints);

        List<CouponRoomsRequest> couponRooms = couponMakeRequest.rooms();
        List<Coupon> coupons = new ArrayList<>();
        List<CouponIssuance> couponIssuances = new ArrayList<>();

        Coupon coupon;
        for (CouponRoomsRequest couponRoom : couponRooms) {
            // 1. 요청된 쿠폰의 할인가격이 비즈니스 요구사항에 맞는지 검증한다.
            final int discount = couponRoom.discount();
            final long roomId = couponRoom.roomId();
            if (!isRightDiscount(couponRoom.discountType(), discount)) {
                throw new InvalidCouponInfoException();
            }

            // 2. roomId와 discount로 발행된 쿠폰이 있는지 검색한다.
            final Room room = roomRepository.findById(roomId).orElseThrow(
                InvalidCouponInfoException::new);
            final Optional<Coupon> resultCoupon = couponRepository.findByRoomIdAndDiscount(
                roomId, discount);
            final int quantity = couponRoom.quantity();
            final int amount = couponRoom.eachPoint();

            if (resultCoupon.isPresent()) {
                // 3. roomId와 discount로 발행된 쿠폰이 있으면 보유 재고의 개수를 업데이트한다.
                coupon = updateCouponStock(resultCoupon.get(), quantity);
                coupons.add(coupon);
                log.info("보유한 쿠폰의 수량 증가: {} memberId: {}", quantity, memberId);
            } else {
                // 4. 위의 경우가 아니라면 새로운 id로 쿠폰을 발행한다.
                coupon = CouponMakeRequest.toEntity(couponRoom, room);
                coupons.add(coupon);
                log.info("신규 쿠폰 발급: {}, memberId: {}", quantity, memberId);
            }
            // 5. 쿠폰 발급 내역 배열 생성
            couponIssuances.add(createCouponIssuance(room, coupon, quantity, amount));
        }
        // 6. 생성된 쿠폰 저장
        couponRepository.saveAll(coupons);
        // 7. 생성된 쿠폰 발급 내역 저장
        couponIssuanceRepository.saveAll(couponIssuances);

        // 8. 업주의 보유 포인트 차감
        // todo: 도메인이 다른 서비스를 트랜잭션 안에서 호출하는 게 좋은 설계일까 고민해 보기.
        point.decreasePointBalance(totalPoints);
        pointRepository.save(point);

        // 9. 포인트 사용 이력 전달
        // Todo: 포인트 사용 내역 Point 도메인에 전달하기
        log.info("쿠폰 발급 성공. 금액: {}", totalPoints);
    }

    // 쿠폰 관리 View
    public CouponManageResponse manageCoupon(final Long accommodationId) {
        List<CouponManageQueryDto> queryResult = couponRepository.findCouponsByAccommodationId(
            accommodationId);
        if (queryResult.isEmpty()) {
            return null;
        }

        List<CouponManageRooms> collected = queryResult.stream()
            .collect(groupingBy(
                    this::createManageRoom,
                    mapping(this::createCouponInfo, toList())
                )
            ).entrySet().stream()
            .map(e -> new CouponManageRooms(e.getKey().roomId(), e.getKey().roomName(),
                e.getKey().roomPrice(), e.getValue()
            ))
            .collect(toList());

        return CouponManageResponse.builder()
            .accommodationId(accommodationId)
            .accommodationName(queryResult.get(0).accommodationName())
            .expiry(queryResult.get(0).endDate())
            .rooms(collected)
            .build();
    }

    // 쿠폰 추가 구매
    public void addonCoupon(final CouponAddRequest couponAddRequest, final long memberId) {
        // 1. 업주의 보유 포인트 검증
        final Optional<Point> resultPoint = pointRepository.findByMemberId(memberId);
        Point point = resultPoint.orElseThrow(PointNotFoundException::new);
        final int totalPoints = couponAddRequest.totalPoints();

        List<Coupon> addCoupons = new ArrayList<>();
        for (var rooms : couponAddRequest.rooms()) {
            for (var coupons : rooms.coupons()) {
                addCoupons.add(increaseCouponStock(coupons));
            }
        }
        couponRepository.saveAll(addCoupons);

        // 2. 업주의 보유 포인트 차감
        point.decreasePointBalance(totalPoints);
        pointRepository.save(point);

        // Todo: 포인트 사용 내역 Point 도메인에 전달하기
        log.info("쿠폰 추가 발급 성공. 금액: {}", totalPoints);
    }

    // Todo: 쿠폰 수정 Validation 로직 추가
    public void modifyCoupon(final CouponModifyRequest modifyRequest) {
        List<Coupon> modifyCoupons = new ArrayList<>();
        for (var rooms : modifyRequest.rooms()) {
            for (var coupons : rooms.coupons()) {
                modifyCoupons.add(modifyCoupon(coupons, modifyRequest.expiry()));
            }
        }
        log.info("쿠폰 수정 성공.");
    }

    // Todo: 쿠폰 삭제 Validation 로직 추가
    public void deleteCoupon(final CouponDeleteRequest request) {

        List<Coupon> deleteCoupons = new ArrayList<>();
        for (var rooms : request.rooms()) {
            for (var coupons : rooms.coupons()) {
                deleteCoupons.add(setupDelete(coupons.couponId()));
            }
        }
        log.info("쿠폰 삭제 성공.");
    }

    private Coupon setupDelete(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
            InvalidCouponInfoException::new);
        coupon.setupDeleted();
        return coupon;
    }

    private Coupon increaseCouponStock(final CouponAddInfos addCoupons) {
        long couponId = addCoupons.couponId();
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
            InvalidCouponInfoException::new);
        coupon.increaseCouponStock(addCoupons.buyQuantity());
        return coupon;
    }


    private Coupon modifyCoupon(final CouponModifyInfos modifyCoupons, LocalDate endDate) {
        long couponId = modifyCoupons.couponId();
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
            InvalidCouponInfoException::new);
        coupon.modifyCoupon(
            modifyCoupons.status(),
            modifyCoupons.discountType(),
            modifyCoupons.discount(),
            modifyCoupons.dayLimit(),
            endDate,
            modifyCoupons.couponType());
        return coupon;
    }

    private CouponInfo createCouponInfo(final CouponManageQueryDto dto) {
        int discount = dto.discount();
        DiscountType discountType = dto.discountType();
        return CouponInfo.builder()
            .couponId(dto.couponId())
            .status(dto.couponStatus())
            .couponName(DiscountType.makeListName(dto.discountType(), discount))
            .appliedPrice(DiscountType.makePaymentPrice(
                discountType, dto.roomPrice(), discount))
            .discountType(discountType)
            .discount(discount)
            .dayLimit(dto.dayLimit())
            .quantity(dto.stock())
            .couponType(dto.couponType())
            .build();
    }

    private CouponManageRooms createManageRoom(final CouponManageQueryDto dto) {
        return CouponManageRooms.builder()
            .roomId(dto.roomId())
            .roomName(dto.roomName())
            .roomPrice(dto.roomPrice())
            .build();
    }

    // 업주의 보유 포인트 검증
    @Transactional(readOnly = true)
    protected Point validationPoint(final Long memberId, final long requestPoint) {
        final Optional<Point> resultPoint = pointRepository.findByMemberId(memberId);
        Point point = resultPoint.orElseThrow(PointNotFoundException::new);
        final long ownerPoint = point.getTotalPointBalance();

        // 쿠폰 구매 요청 금액이 업주의 보유 포인트보다 크다면 예외 발생
        if (ownerPoint < requestPoint) {
            log.info("업주의 보유 포인트가 부족합니다. 보유 포인트: {}, 요청 포인트: {}",
                ownerPoint, requestPoint);
            throw new InsufficientPointsException();
        }
        return point;
    }

    // 정상적인 숙소 id 요청인지 검증
    @Transactional(readOnly = true)
    public void validateAccommodationRequest(
        final long accommodationId, final long currentMemberId
    ) {
        if (!couponRepository.existsAccommodationIdByMemberId(
            accommodationId, currentMemberId)) {
            log.info("숙소의 업주가 아닙니다. 업주의 id: {}, 숙소의 id: {}",
                currentMemberId, accommodationId);
            throw new NotOwnerException();
        }
        if (!accommodationRepository.existsById(accommodationId)) {
            log.info("숙소의 정보를 찾을 수 없습니다. id: {}", accommodationId);
            throw new AccommodationNotFoundException();
        }
    }
    
    // 발행 이력이 있는 쿠폰이라면 재고 수량을 업데이트 한다.
    private Coupon updateCouponStock(final Coupon coupon, final int quantity) {
        return coupon.increaseCouponStock(quantity);
    }

    private CouponIssuance createCouponIssuance(
        final Room room,
        final Coupon coupon,
        final int quantity,
        final int amount
    ) {
        return CouponIssuance.builder()
            .room(room)
            .coupon(coupon)
            .quantity(quantity)
            .amount(amount)
            .build();
    }
}
