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


    @Column(nullable = false, name = "final_price")
    @Comment("최종 가격")
    private int finalPrice;
}
