package com.backoffice.upjuyanolja.domain.accommodation.service;

import static com.backoffice.upjuyanolja.global.exception.ErrorCode.ACCOMMODATION_NOT_FOUND;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Room;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final CouponService couponService;

    @Transactional(readOnly = true)
    public AccommodationPageResponse findAccommodations(
        String category, boolean hasCoupon, String keyword, Pageable pageable
    ) {
        List<Accommodation> accommodations = accommodationRepository
            .findAllByCategoryAndName(category, keyword);

        return AccommodationPageResponse.from(
            new PageImpl<>(
                accommodations.stream()
                    .filter(
                        accommodation -> !hasCoupon || this.checkCouponAvailability(accommodation))
                    .map(accommodation -> AccommodationSummaryResponse.from(
                        accommodation, getLowestPrice(accommodation),
                        getDiscountPrice(accommodation),
                        getCouponName(accommodation)
                    ))
                    .toList(),
                pageable,
                accommodations.size()
            )
        );
    }

    private boolean checkCouponAvailability(Accommodation accommodation) {
        return accommodation.getRooms().stream()
            .anyMatch(room -> !room.getCouponRooms().isEmpty());
    }

    private int getLowestPrice(Accommodation accommodation) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> i));

        for (Room room : accommodation.getRooms()) {
            pq.offer(room.getRoomPrice().getOffWeekDaysMinFee());
        }

        return pq.poll();
    }

    private int getDiscountPrice(Accommodation accommodation) {
        return getDiscountInfo(accommodation)
            .map(couponRoom -> couponRoom.couponRooms().get(0).price())
            .orElse(getLowestPrice(accommodation));
    }

    private String getCouponName(Accommodation accommodation) {
        return getDiscountInfo(accommodation)
            .map(couponRoom -> couponRoom.couponRooms().get(0).name())
            .orElse("쿠폰이 없습니다.");
    }

    private Optional<CouponRoomDetailResponse> getDiscountInfo(Accommodation accommodation) {
        Optional<List<CouponRoomDetailResponse>> flatResponse =
            couponService.getSortedFlatCouponInAccommodation(accommodation);
        Optional<List<CouponRoomDetailResponse>> percentageResponse =
            couponService.getSortedPercentageCouponInAccommodation(accommodation);

        if (flatResponse.isEmpty() || percentageResponse.isEmpty()) {
            return Optional.empty();
        }

        return flatResponse.map(flat ->
            flat.get(0).couponRooms().get(0).price()).orElse(getLowestPrice(accommodation)) <=
            percentageResponse.map(percent ->
                percent.get(0).couponRooms().get(0).price()).orElse(getLowestPrice(accommodation)) ?
            flatResponse.map(flat -> flat.get(0))
            : percentageResponse.map(percent -> percent.get(0));

    }

    @Transactional(readOnly = true)
    public AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId, LocalDate startDate, LocalDate endDate
    ) {
        Accommodation accommodation =
            accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new AccommodationException(ACCOMMODATION_NOT_FOUND));

        return null;
    }

}
