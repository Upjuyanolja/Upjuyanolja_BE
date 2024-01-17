package com.backoffice.upjuyanolja.domain.coupon.service;

import com.backoffice.upjuyanolja.domain.coupon.dto.request.CouponRoomsRequest;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponBackofficePrincipalService {

    private final CouponRepository couponRepository;

    public Object createCoupon(List<CouponRoomsRequest> couponRooms) {
        for (CouponRoomsRequest couponRoom : couponRooms) {
            // 1. 먼저 roomId와 discount로 발행된 쿠폰이 있는지 검색한다.
            Integer presentCouponRoomId = couponRepository.findByPresentRoomId(
                couponRoom.roomId(),
                couponRoom.discount(),
                couponRoom.discountType()
            );

            if (presentCouponRoomId != null) {
                // 2. roomId와 discount로 발행된 쿠폰이 있으면 보유 재고의 개수를 업데이트한다.

            } else {
                // 3. 위의 경우가 아니라면 새로운 id로 쿠폰을 발행한다.

            }

            // 4. 업주의 보유 포인트 차감 -> 메시지 발행?

        }

        return null;
    }

}
