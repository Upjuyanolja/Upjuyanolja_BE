package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

import static com.backoffice.upjuyanolja.domain.coupon.config.CouponProperties.getDayLimit;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record CouponMakeRequest(
    @NotNull(message = "숙소 식별자는 필수입니다.")
    Long accommodationId,
    @NotNull(message = "합계 금액은 필수입니다.")
    @Min(value = 1000, message = "합계 금액을 확인하여 주세요.")
    long totalPoints,
    @NotNull
    List<CouponRoomsRequest> rooms
) {

    public static Coupon toEntity(
        CouponRoomsRequest request,
        Room room
    ) {
        return Coupon.builder()
            .room(room) // 객실 인스턴스
            .discountType(request.discountType()) // 정액-정률
            .discount(request.discount()) // 할인가-할인율
            .couponType(CouponType.ALL_DAYS) // 초기값 상시(ALL_DAYS)
            .couponStatus(CouponStatus.ENABLE) // 쿠폰 상태 -> 발급 중(ENABLE)
            .endDate(LocalDate.now().plusMonths(1)) // 기본 5주(한달)
            .dayLimit(getDayLimit()) // 초기값 무제한(-1), 일일사용한도
            .stock(request.quantity()) // 쿠폰 재고(수량)
            .build();
    }
}
