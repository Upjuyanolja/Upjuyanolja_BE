package com.backoffice.upjuyanolja.domain.coupon.service;

import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.isRightDiscount;

import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponRoomsRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.exception.InsufficientPointsException;
import com.backoffice.upjuyanolja.domain.coupon.exception.InvalidCouponInfoException;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointUsage;
import com.backoffice.upjuyanolja.domain.point.exception.PointNotFoundException;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
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
    private final MemberGetService memberGetService;

    public CouponMakeViewResponse getRoomsByAccommodation(Long accommodationId) {
        return couponRepository.findRoomsIdByAccommodationId(accommodationId);
    }

    public void createCoupon(
        final CouponMakeRequest couponMakeRequest, final Member currentMember
    ) {
        final long totalPoints = couponMakeRequest.totalPoints();
        Point point = validationPoint(currentMember, totalPoints);

        List<CouponRoomsRequest> couponRooms = couponMakeRequest.rooms();
        List<Coupon> coupons = new ArrayList<>();
        List<PointUsage> pointUsages = new ArrayList<>();

        Coupon coupon;
        for (CouponRoomsRequest couponRoom : couponRooms) {
            // 1. 요청된 쿠폰의 할인가격이 비즈니스 요구사항에 맞는지 검증한다.
            if (!isRightDiscount(couponRoom.discountType(), couponRoom.discount())) {
                throw new InvalidCouponInfoException();
            }

            // 2. roomId와 discount로 발행된 쿠폰이 있는지 검색한다.
            Optional<Coupon> resultCoupon = couponRepository.findByRoomIdAndDiscount(
                couponRoom.roomId(), couponRoom.discount());

            Room room = roomRepository.findById(couponRoom.roomId()).orElseThrow(
                InvalidCouponInfoException::new);

            int quantity = couponRoom.quantity();
            // Todo: 개발 완료 후 확인용 log 삭제할 것.
            if (resultCoupon.isPresent()) {
                // 3. roomId와 discount로 발행된 쿠폰이 있으면 보유 재고의 개수를 업데이트한다.
                coupon = updateCouponStock(resultCoupon.get(), quantity);
                coupons.add(coupon);
                log.info("보유한 쿠폰의 수량 증가: {}", quantity);
            } else {
                // 4. 위의 경우가 아니라면 새로운 id로 쿠폰을 발행한다.
                coupon = CouponMakeRequest.toEntity(couponRoom, room);
                coupons.add(coupon);
                log.info("신규 쿠폰 발급: {}", quantity);
            }


        }
        couponRepository.saveAll(coupons);

        // 6. 업주의 보유 포인트 차감
        // todo: 도메인이 다른 서비스를 트랜잭션 안에서 호출하는 게 좋은 설계일까 고민해 보기.
        point.decreasePointBalance(totalPoints);
        pointRepository.save(point);

        // 7. 포인트 사용 이력 전달
        // Todo: 포인트 사용 내역 Point 도메인에 전달하기
        log.info("쿠폰 발급 성공");
    }


    private Point validationPoint(Member member, long requestPoint) {
        // 업주의 보유 포인트 검증
        Optional<Point> resultPoint = pointRepository.findByMember(member);
        Point point = resultPoint.orElseThrow(PointNotFoundException::new);
        final long ownerPoint = point.getTotalPointBalance();

        // 쿠폰 구매 요청 금액이 업주의 보유 포인트보다 크다면 예외 발생
        if (ownerPoint < requestPoint) {
            log.info("업주의 보유 포인트가 부족합니다. 보유 포인트: {}, 요청 포인트: {}", ownerPoint, requestPoint);
            throw new InsufficientPointsException();
        }
        return resultPoint.get();
    }

    // 업주의 회원 id와 등록된 숙소 id가 매치되는지 검증
    @Transactional(readOnly = true)
    public boolean validateCouponRequest(
        CouponMakeRequest couponMakeRequest, long currentMemberId
    ) {
        long accommodationId = couponMakeRequest.accommodationId();

        if (!couponRepository.existsAccommodationIdByMemberId(
            accommodationId, currentMemberId)) {
            throw new AccommodationNotFoundException();
        }
        return true;
    }

    // 발행 이력이 있는 쿠폰이라면 재고 수량을 업데이트 한다.
    private Coupon updateCouponStock(Coupon coupon, int quantity) {
        coupon.increaseCouponStock(quantity);
        return coupon;
    }

    // 발행 이력이 없다면 새로 쿠폰을 생성한다.
    private Coupon createCoupon(CouponRoomsRequest request, Room room) {
        return CouponMakeRequest.toEntity(request, room);
    }
}
