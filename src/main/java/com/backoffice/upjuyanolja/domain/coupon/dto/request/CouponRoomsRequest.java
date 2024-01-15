package com.backoffice.upjuyanolja.domain.coupon.dto.request;

import static com.backoffice.upjuyanolja.domain.coupon.config.CouponProperties.getDayLimit;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CouponRoomsRequest(
    @NotNull(message = "객실 식별자는 필수입니다.")
    Long roomId,
    @NotEmpty(message = "쿠폰 유형을 지정해 주세요.")
    DiscountType discountType,
    @NotNull(message = "할인 가격(율)을 지정해 주세요.")
    int discount,
    @NotNull(message = "쿠폰의 수량을 입력해 주세요.")
    @Min(value = 1, message = "쿠폰의 수량은 1개 이상이어야 합니다.")
    int quantity
) {

    /**
     * 새로운 쿠폰을 만든다.
     * @param room 객실 객체
     * @param discountType 쿠폰 유형(정액/정률)
     * @param discount 할인 금액(할인율)
     * @param quantity 발급 수량
     * @return
     */
    public static Coupon toEntity(
        Room room,
        DiscountType discountType,
        int discount,
        int quantity
    ) {
        return Coupon.builder()
            .room(room) // 객실 인스턴스
            .discountType(discountType) // 정액-정률
            .discount(discount) // 할인가-할인율
            .couponType(CouponType.ALL_DAYS) // 초기값 상시(ALL_DAYS)
            .couponStatus(CouponStatus.ENABLE) // 쿠폰 상태 -> 발급 중(ENABLE)
            .endDate(LocalDate.now().plusWeeks(5)) // 초기값 5주
            .dayLimit(getDayLimit()) // 초기값 무제한(-1), 일일사용한도
            .stock(quantity) // 쿠폰 재고(수량)
            .build();
    }
}
