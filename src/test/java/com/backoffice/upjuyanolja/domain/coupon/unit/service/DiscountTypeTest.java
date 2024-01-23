package com.backoffice.upjuyanolja.domain.coupon.unit.service;

import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.FLAT;
import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.RATE;
import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.makePaymentPrice;
import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType.isRightDiscount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
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
        int price1 = makePaymentPrice(FLAT, 100000, 20000);
        int price2 = makePaymentPrice(RATE, 100000, 5);

        // when & then
        assertThat(price1).isEqualTo(80000);
        assertThat(price2).isEqualTo(95000);
    }

    @DisplayName("객실 가격과 할인율을 넣으면 할인되는 금액을 보여준다.")
    @Test
    public void convertRateToFlatDiscount_Test() throws Exception {
        // given
        int discount = 5; // 5%
        int price = 10000;
        int result = DiscountType.percentToFlatDiscount(price, discount);

        // when & Then
        assertThat(result).isEqualTo(500);
    }

    @DisplayName("할인 유형과 할인가(할인율)을 입력하면 view에 응답할 쿠폰 이름을 만들어서 보여준다." )
    @Test
    public void makeDicountCouponName() throws Exception {
        // given
        int discount1 = 5000;
        int discount2 = 10;
        String mockName1 = DiscountType.makeTitleName(FLAT, discount1);
        String mockName2 = DiscountType.makeTitleName(RATE, discount2);

        // when & Then
        assertThat(mockName1).isEqualTo("5,000원 할인 쿠폰");
        assertThat(mockName2).isEqualTo("10% 할인 쿠폰");
    }
}