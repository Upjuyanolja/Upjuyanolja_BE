package com.backoffice.upjuyanolja.domain.coupon.repository;

import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodationOwnership;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.AccommodationResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageQueryDto;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponRoomsResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.QCoupon;
import com.backoffice.upjuyanolja.domain.room.entity.QRoom;
import com.backoffice.upjuyanolja.domain.room.entity.QRoomPrice;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    QCoupon qCoupon = QCoupon.coupon;

    @Override
    public CouponMakeViewResponse findRoomsByAccommodationId(Long accommodationId) {

        AccommodationResponse accommodationResponse = queryFactory.
            select(Projections.constructor(AccommodationResponse.class,
                                           qAccommodation.id, qAccommodation.name
            ))
            .from(qAccommodation)
            .where(qAccommodation.id.eq(accommodationId))
            .fetchOne();

        List<CouponRoomsResponse> roomListRespons = queryFactory
            .select(Projections.constructor(
                CouponRoomsResponse.class,
                qRoom.id,
                qRoom.name,
                qRoomPrice.offWeekDaysMinFee.as("roomPrice")
            ))
            .from(qRoom)
            .join(qRoomPrice).on(qRoom.price.id.eq(qRoomPrice.id))
            .where(qRoom.accommodation.id.eq(accommodationId))
            .fetch();

        return CouponMakeViewResponse.of(accommodationResponse, roomListRespons);
    }

    /**
     * accommodation_ownership 테이블에서 accommodationId와 memberId를 and 조건으로 검색하.
     * 데이터가 존재하면 true, 존재하지 않으면 false 반환.
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
            .fetchOne() != null;
    }

    @Override
    public List<CouponManageQueryDto> findCouponsByAccommodationId(Long accommodationId) {
        return queryFactory.select(
                Projections.constructor(CouponManageQueryDto.class,
                    qAccommodation.id, qAccommodation.name, qCoupon.endDate, qRoom.id,
                    qRoom.name, qRoomPrice.offWeekDaysMinFee.as("roomPrice"),
                    qCoupon.id, qCoupon.couponStatus, qCoupon.discountType, qCoupon.discount,
                    qCoupon.dayLimit, qCoupon.stock, qCoupon.couponType)
                )
            .from(qCoupon)
            .join(qRoom).on(qCoupon.room.id.eq(qRoom.id))
            .join(qAccommodation).on(qRoom.accommodation.id.eq(qAccommodation.id))
            .join(qRoomPrice).on(qRoom.price.id.eq(qRoomPrice.id))
            .where(qAccommodation.id.eq(accommodationId)
                .and(qCoupon.couponStatus.ne(CouponStatus.DELETED)))
            .orderBy(qRoom.id.asc())
            .fetch();
    }
}
