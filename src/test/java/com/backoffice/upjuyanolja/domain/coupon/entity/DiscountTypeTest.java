package com.backoffice.upjuyanolja.domain.coupon.entity;

import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backoffice.upjuyanolja.domain.coupon.exception.InvalidCouponInfoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiscountTypeTest {

    @DisplayName("잘못된 쿠폰 할인 정보를 입력하면 예외가 발생한다.")
    @Test
    public void invalidDiscountThenThrowExceptionTest() throws Exception {
        // given
        int discountFlat = 100;
        DiscountType type1 = FLAT;

        int discountRate = 55;
        DiscountType type2 = RATE;

        // when & Then
        assertThatThrownBy(() -> isRightDiscount(type1, discountFlat))
            .isInstanceOf(InvalidCouponInfoException.class);

        assertThatThrownBy(() -> isRightDiscount(type2, discountRate))
            .isInstanceOf(InvalidCouponInfoException.class);
    }

    @DisplayName("할인 종류와 객실 가격을 넣었을 때 반환하는 할인된 객실 가격을 검증한다.")
    @Test
    public void flatDiscountAmountTest() throws Exception {
        // given
        int price1 = getPaymentPrice(FLAT, 100000, 20000);
        int price2 = getPaymentPrice(RATE, 100000, 5);

        // when & then
        assertThat(price1).isEqualTo(80000);
        assertThat(price2).isEqualTo(95000);
    }

}