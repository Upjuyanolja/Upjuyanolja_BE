package com.backoffice.upjuyanolja.domain.coupon.entity;

import static com.backoffice.upjuyanolja.domain.coupon.entity.DiscountRestrictions.*;
import com.backoffice.upjuyanolja.domain.coupon.exception.WrongCouponInfoException;
import java.text.DecimalFormat;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;

@Getter
public enum DiscountType {

    FLAT(
        "원 할인 쿠폰",
        "원 쿠폰",
        (titleName, discount) -> {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedNumber = decimalFormat.format(discount);
            StringBuilder sb = new StringBuilder(formattedNumber);
            sb.append(titleName);
            return sb.toString();
        },
        (listName, discount) -> {
            StringBuilder sb = new StringBuilder(discount);
            sb.append(listName);
            return sb.toString();
        },
        discount -> {
            if (discount.intValue() >= getMinPrice() && discount.intValue() <= getMaxPrice()
                    && discount.intValue() % 1000 == 0) {
                return true;
            }
            throw new WrongCouponInfoException();
        },
        (price, discount) -> price.intValue() - discount.intValue()
    ),

    RATE(
        "%할인 쿠폰",
        "%쿠폰",
        (titleName, discount) -> {
            StringBuilder sb = new StringBuilder(discount);
            sb.append(titleName);
            return sb.toString();
        },
        (listName, discount) -> {
            StringBuilder sb = new StringBuilder(discount);
            sb.append(listName);
            return sb.toString();
        },
        discount -> {
            if (discount >= getMinRate() && discount <= getMaxRate()
                && discount % 1 == 0) {
                return true;
            }
            throw new WrongCouponInfoException();
        },
        (price, discount) -> {
            int p = price.intValue();
            double d = 1 - (discount.intValue() / 100.0);
            return (int) (p * d);
        }
    );

    private final String titleName;
    private final String listName;
    // "1,000원 할인 쿠폰" 또는 "10% 할인 쿠폰" 문자열을 만들어서 반환
    private final BiFunction<String, Integer, String> makeTitleFormat;
    // "1000원 쿠폰" 또는 "10% 쿠폰" 문자열을 만들어서 반환
    private final BiFunction<String, Integer, String> makeListFormat;
    // 할인가격이 비즈니스 로직에서 정한 정상범위내에 있는지 확인
    private final Function<Integer, Boolean> discountValidate;
    // 고객이 결제한 최종 금액을 계산하여 반환
    private final BiFunction<Integer, Integer, Integer> calcAmount;

    DiscountType(
        String titleName,
        String listName,
        BiFunction<String, Integer, String> makeTitleFormat,
        BiFunction<String, Integer, String> makeListFormat,
        Function<Integer, Boolean> discountValidate,
        BiFunction<Integer, Integer, Integer> calcAmount
    ) {
        this.titleName = titleName;
        this.listName = listName;
        this.makeTitleFormat = makeTitleFormat;
        this.makeListFormat = makeListFormat;
        this.discountValidate = discountValidate;
        this.calcAmount = calcAmount;
    }

    public static String makeTitleName(DiscountType discountType, int discount) {
        return discountType.makeTitleFormat.apply(discountType.getTitleName(), discount);
    }

    public static String makeListName(DiscountType discountType, int discount) {
        return discountType.makeTitleFormat.apply(discountType.getListName(), discount);
    }

    public static Boolean isRightDiscount(DiscountType discountType, Integer discount) {
        return discountType.discountValidate.apply(discount);
    }

    public static int getPaymentPrice(
        DiscountType discountType,
        Integer price,
        Integer discount
    ) {
        return discountType.calcAmount.apply(price, discount);
    }

}