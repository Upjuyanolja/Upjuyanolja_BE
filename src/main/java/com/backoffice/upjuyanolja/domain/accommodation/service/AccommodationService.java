package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Room;
import com.backoffice.upjuyanolja.domain.accommodation.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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
                        accommodation, checkSoldOut(accommodation),
                        getLowestPrice(accommodation), getDiscountPrice(accommodation),
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

    private boolean checkSoldOut(Accommodation accommodation) {
        return accommodation.getRooms().stream()
            .allMatch(room -> room.getCount() == 0);
    }

    private int getLowestPrice(Accommodation accommodation) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> i));

        for (Room room : accommodation.getRooms()) {
            RoomPrice roomPrice = room.getRoomPrice();
            int minPrice = Math.min(
                Math.min(roomPrice.getOffWeekDaysMinFee(), roomPrice.getOffWeekendMinFee()),
                Math.min(roomPrice.getPeakWeekDaysMinFee(), roomPrice.getPeakWeekendMinFee())
            );
            pq.offer(minPrice);
        }

        return pq.poll();
    }

    private int getDiscountPrice(Accommodation accommodation) {
        return 0;
    }

    private String getCouponName(Accommodation accommodation) {
        return null;
    }

    public AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId, LocalDate startDate, LocalDate endDate
    ) {
        return null;
    }
}
