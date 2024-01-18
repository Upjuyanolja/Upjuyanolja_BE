package com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice;

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
    Long roomId,
    DiscountType discountType,
    int discount,
    int quantity,
    int eachPoint
) {
}
