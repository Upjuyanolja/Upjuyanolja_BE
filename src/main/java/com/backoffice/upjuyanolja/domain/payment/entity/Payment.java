package com.backoffice.upjuyanolja.domain.payment.entity;

import com.backoffice.upjuyanolja.global.common.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("결제 식별자")
    private Long id;

    @Column(nullable = false, name = "pay_method")
    @Enumerated(value = EnumType.STRING)
    @Comment("결제 수단")
    private PayMethod payMethod;

    @Column(nullable = false, name = "room__price")
    @Comment("객실 이용료")
    private Integer roomPrice; // 결제 당시의 객실 이용료

    @Column(nullable = false, name = "used_coupon")
    @Comment("쿠폰 사용 여부")
    private Boolean usedCoupon;

    @Column(nullable = false, name = "total_price")
    @Comment("결제 금액")
    private Integer amount;

    @Builder
    public Payment(
        Long id,
        PayMethod payMethod,
        Integer roomPrice,
        Boolean usedCoupon,
        Integer amount
    ) {
        this.id = id;
        this.payMethod = payMethod;
        this.roomPrice = roomPrice;
        this.usedCoupon = usedCoupon;
        this.amount = amount;
    }
}
