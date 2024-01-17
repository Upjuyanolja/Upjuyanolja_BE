package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodationOwnership;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.AccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomsResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.entity.QCoupon;
import com.backoffice.upjuyanolja.domain.point.entity.QPoint;
import com.backoffice.upjuyanolja.domain.room.entity.QRoom;
import com.backoffice.upjuyanolja.domain.room.entity.QRoomPrice;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QAccommodation qAccommodation = QAccommodation.accommodation;
    QRoom qRoom = QRoom.room;
    QRoomPrice qRoomPrice = QRoomPrice.roomPrice;
    QAccommodationOwnership qOwnership = QAccommodationOwnership.accommodationOwnership;
    QPoint qPoint = QPoint.point;
    QCoupon qCoupon = QCoupon.coupon;

    @Override
    public CouponMakeViewResponse findRoomsByAccommodationId(Long accommodationId) {

        AccommodationResponse accommodationResponse = queryFactory.
            select(Projections.constructor(AccommodationResponse.class,
                qAccommodation.id, qAccommodation.name))
            .from(qAccommodation)
            .where(qAccommodation.id.eq(accommodationId))
            .fetchOne();

        List<CouponRoomsResponse> roomListRespons = queryFactory
            .select(Projections.constructor(CouponRoomsResponse.class,
                qRoom.id, qRoom.name, qRoomPrice.offWeekDaysMinFee.as("roomPrice")))
            .from(qRoom)
            .join(qRoomPrice).on(qRoom.price.id.eq(qRoomPrice.id))
            .where(qRoom.accommodation.id.eq(accommodationId))
            .fetch();

        return CouponMakeViewResponse.of(accommodationResponse, roomListRespons);
    }

    /**
     * accommodation_ownership 테이블에서 accommodationId와 memberId를 and 조건으로 검색하여 데이터가 존재하면 true, 존재하지
     * 않으면 false 반환.
     *
     * @param accommodationId : 숙소 식별자
     * @param memberId        : 회원 식별자
     * @return ture / false
     */
    @Override
    public boolean existsAccommodationIdByMemberId(Long accommodationId, Long memberId) {
        return queryFactory.selectOne()
            .from(qOwnership)
            .where(qOwnership.member.id.eq(memberId)
                .and(qOwnership.accommodation.id.eq(accommodationId)))
            .fetchFirst() != null;
    }

    /**
     * 업주가 현재 보유한 포인트 값을 반환
     *
     * @param memberId : 업주 식별자(회원 식별자)
     * @return 업주가 현재 보유한 포인트
     */
    @Override
    public Integer getOwnerPoint(Long memberId) {
        return queryFactory.selectOne()
            .from(qPoint)
            .where(qPoint.member.id.eq(memberId))
            .fetchOne();
    }

    /**
     * 포인트 테이블에서 현재 로그인한 memberId가 존재하는지 검증
     *
     * @param memberId : 업주 식별자(회원 식별자)
     * @return 포인트 테이블에 회원 ID가 존재하면 true / 아니라면 false
     */
    @Override
    public boolean existsPointIdByMemberId(Long memberId) {
        return queryFactory.selectOne()
            .from(qPoint)
            .where(qPoint.member.id.eq(memberId))
            .fetchFirst() != null;
    }

    /**
     * 쿠폰 테이블에서 roomId, discount, discount_type 으로 발행된 쿠폰이 있는지 검색한다.
     *
     * @param seekRoomId
     * @param discountType
     * @param discount
     * @return
     */
    @Override
    public Integer findByPresentRoomId(
        Long seekRoomId,
        int discount,
        DiscountType discountType
    ) {
        return queryFactory.selectOne()
            .from(qCoupon)
            .where(qCoupon.room.id.eq(seekRoomId)
                .and(qCoupon.discount.eq(discount)
                    .and(qCoupon.discountType.eq(discountType))))
            .fetchOne();
    }
}
