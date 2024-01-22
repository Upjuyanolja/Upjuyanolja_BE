package com.backoffice.upjuyanolja.domain.coupon.entity;


import com.backoffice.upjuyanolja.domain.coupon.exception.InsufficientCouponStockException;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(
        name = "UniqueRoomIdAndDiscount",
        columnNames = {"roomId", "discount"})
})
public class Coupon extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("쿠폰 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room_id")
    @Comment("객실 식별자")
    private Room room;

    @Column(nullable = false, name = "coupon_type")
    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 유형")
    private CouponType couponType;

    @Column(nullable = false, name = "discount_type")
    @Enumerated(EnumType.STRING)
    @Comment("할인 유형")
    private DiscountType discountType;

    @Column(nullable = false, name = "coupon_status")
    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 상태")
    private CouponStatus couponStatus;

    @Column(nullable = false)
    @Comment("할인 가격(할인 율)")
    private int discount;

    @Column(nullable = false, name = "end_date")
    @Comment("쿠폰 노출 만료일")
    private LocalDate endDate;

    @Column(nullable = false, name = "day_limit")
    @Comment("일일 사용 한도")
    private int dayLimit;

    @Column(nullable = false, name = "stock")
    @Comment("쿠폰 개수(재고)")
    private int stock;

    @Builder
    public Coupon(
        Long id,
        Room room,
        CouponType couponType,
        DiscountType discountType,
        CouponStatus couponStatus,
        int discount,
        LocalDate endDate,
        int dayLimit,
        int stock
    ) {
        this.id = id;
        this.room = room;
        this.couponType = couponType;
        this.discountType = discountType;
        this.couponStatus = couponStatus;
        this.discount = discount;
        this.endDate = endDate;
        this.dayLimit = dayLimit;
        this.stock = stock;
    }

    public Coupon increaseCouponStock(int quantity) {
        this.stock += quantity;
        return this;
    }

    public Coupon decreaseCouponStock(int quantity) {
        if (this.stock - quantity < 0) {
            throw new InsufficientCouponStockException();
        }
        this.stock -= quantity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coupon coupon = (Coupon) o;
        return Objects.equals(getId(), coupon.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
