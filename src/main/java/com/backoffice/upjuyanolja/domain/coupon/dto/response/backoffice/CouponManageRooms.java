package com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice;

import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record CouponManageRooms(
    Long roomId,
    String roomName,
    int roomPrice,
    List<CouponInfo> coupons
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CouponManageRooms rooms = (CouponManageRooms) o;
        return Objects.equals(roomId, rooms.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}
