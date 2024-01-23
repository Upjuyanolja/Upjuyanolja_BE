package com.backoffice.upjuyanolja.domain.coupon.entity;

import static com.backoffice.upjuyanolja.domain.coupon.config.CouponProperties.*;

import com.backoffice.upjuyanolja.domain.coupon.exception.InvalidCouponInfoException;
import java.text.DecimalFormat;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import lombok.Getter;

@Getter
public enum DiscountType {

    FLAT(
        "원 할인 쿠폰",
        "원 쿠폰",
        "원",
        "원 할인",
        (titleName, discount) -> {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedNumber = decimalFormat.format(discount);
            StringBuilder sb = new StringBuilder(formattedNumber);
            sb.append(titleName);
            return sb.toString();
        },
        (listName, discount) -> {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedNumber = decimalFormat.format(discount);
            StringBuilder sb = new StringBuilder(formattedNumber);
            sb.append(listName);
            return sb.toString();
        },
        (shortName, discount) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(discount).append(shortName);
            return sb.toString();
        },
        (detailName, discount) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(discount).append(detailName);
            return sb.toString();
        },
        discount -> {
            if (discount.intValue() >= getMinPrice() && discount.intValue() <= getMaxPrice()
                && discount.intValue() % 1000 == 0) {
                return true;
            }
            throw new InvalidCouponInfoException();
        },
        (price, discount) -> price.intValue() - discount.intValue()
    ),

    RATE(
        "% 할인 쿠폰",
        "% 쿠폰",
        "%",
        "% 할인",
        (titleName, discount) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(discount).append(titleName);
            return sb.toString();
        },
        (listName, discount) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(discount).append(listName);
            return sb.toString();
        },
        (shortName, discount) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(discount).append(shortName);
            return sb.toString();
        },
        (detailName, discount) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(discount).append(detailName);
            return sb.toString();
        },
        discount -> {
            if (discount >= getMinRate() && discount <= getMaxRate()
                && discount % 1 == 0) {
                return true;
            }
            throw new InvalidCouponInfoException();
        },
        (price, discount) -> {
            int p = price.intValue();
            double d = 1 - (discount.intValue() * 0.01);
            return (int) (p * d);
        }
    );

    private final String titleName;
    private final String listName;
    private final String shortName;
    private final String detailName;
    private final BiFunction<String, Integer, String> makeTitleFormat;
    private final BiFunction<String, Integer, String> makeListFormat;
    private final BiFunction<String, Integer, String> makeShortFormat;
    private final BiFunction<String, Integer, String> makeDetailFormat;
    private final Predicate<Integer> discountValidate;
    private final BiFunction<Integer, Integer, Integer> calcAmount;

    DiscountType(
        final String titleName,
        final String listName,
        final String shortName,
        final String detailName,
        final BiFunction<String, Integer, String> makeTitleFormat,
        final BiFunction<String, Integer, String> makeListFormat,
        final BiFunction<String, Integer, String> makeShortFormat,
        final BiFunction<String, Integer, String> makeDetailFormat,
        final Predicate<Integer> discountValidate,
        final BiFunction<Integer, Integer, Integer> calcAmount
    ) {
        this.titleName = titleName;
        this.listName = listName;
        this.detailName = detailName;
        this.shortName = shortName;
        this.makeTitleFormat = makeTitleFormat;
        this.makeListFormat = makeListFormat;
        this.makeShortFormat = makeShortFormat;
        this.makeDetailFormat = makeDetailFormat;
        this.discountValidate = discountValidate;
        this.calcAmount = calcAmount;
    }

    public static String makeTitleName(final DiscountType discountType, final int discount) {
        return discountType.makeTitleFormat.apply(discountType.getTitleName(), discount);
    }

    public static String makeListName(final DiscountType discountType, final int discount) {
        return discountType.makeListFormat.apply(discountType.getListName(), discount);
    }

    public static String makeShortName(final DiscountType discountType, final int discount) {
        return discountType.makeShortFormat.apply(discountType.getShortName(), discount);
    }

    public static String makeDetailName(final DiscountType discountType, final int discount) {
        return discountType.makeDetailFormat.apply(discountType.getDetailName(), discount);
    }

    public static Boolean isRightDiscount(final DiscountType discountType, final Integer discount) {
        return discountType.discountValidate.test(discount);
    }

    // 쿠폰 할인이 적용된 객실 가격을 반환한다.
    public static int makePaymentPrice(
        final DiscountType discountType,
        final Integer price,
        final Integer discount
    ) {
        return discountType.calcAmount.apply(price, discount);
    }

    // X% 할인율을 정액으로 얼마 할인되는지 보여준다.
    public static int percentToFlatDiscount(final int price, final int discount) {
        double decimal = discount / 100.0;
        return (int) (price * decimal);
    }
}
