package com.backoffice.upjuyanolja.domain.coupon.entity;

import java.util.function.Function;
import lombok.Getter;

public class CouponType {

    public static final Integer MIN_WON_DISCOUNT = 1000;
    public static final Integer MAX_WON_DISCOUNT = 50000;

    public static final Integer MIN_PERCENT_DISCOUNT = 1;
    public static final Integer MAX_PERCENT_DISCOUNT = 50;

    @Getter
    public enum Type {
        FLAT("원 할인", price -> (price >= MIN_WON_DISCOUNT && price <= MAX_WON_DISCOUNT)
            && price % 1000 == 0),

        PERCENTAGE("% 할인", price -> (price >= MIN_PERCENT_DISCOUNT && price <= MAX_PERCENT_DISCOUNT)
            && price % 1 == 0);

        private final String name;
        private final Function<Integer, Boolean> validateFunction;

        private Type(String name, Function<Integer, Boolean> validateFunction) {
            this.name = name;
            this.validateFunction = validateFunction;
        }

        public void validatePrice(Integer price) {
            if (!validateFunction.apply(price)) {
                throw new RuntimeException();
            }
        }
    }

}

