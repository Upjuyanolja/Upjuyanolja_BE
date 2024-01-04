package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
        String category, boolean onlyHasCoupon, String keyword, Pageable pageable
    ) {
        List<Accommodation> accommodations = accommodationRepository
            .findAllByCategoryAndName(category, keyword);

        return AccommodationPageResponse.from(
            new PageImpl<>(
                accommodations.stream()
                    .filter(
                        accommodation -> !onlyHasCoupon || this.checkCouponAvailability(
                            accommodation))
                    .map(accommodation -> AccommodationSummaryResponse.from(
                        accommodation, getLowestPrice(accommodation.getId()),
                        getDiscountPrice(accommodation.getId()),
                        getCouponName(accommodation.getId())
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

    private int getLowestPrice(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId).orElseThrow(
            () -> new AccommodationNotFoundException()
        );
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> i));

        for (Room room : accommodation.getRooms()) {
            pq.offer(room.getRoomPrice().getOffWeekDaysMinFee());
        }

        return pq.poll();
    }

    private int getDiscountPrice(Long accommodationId) {
        return getDiscountInfo(accommodationId)
            .map(couponRoom -> couponRoom.price())
            .orElse(getLowestPrice(accommodationId));
    }

    private String getCouponName(Long accommodationId) {
        return getDiscountInfo(accommodationId)
            .map(couponRoom -> couponRoom.name())
            .orElse("");
    }

    private String getMainCouponName(Long accommodationId) {
        List<CouponRoomResponse> flatResponses =
            couponService.getSortedFlatCouponInAccommodation(accommodationId);
        List<CouponRoomResponse> percentageResponses =
            couponService.getSortedPercentageCouponInAccommodation(accommodationId);

        if (flatResponses.isEmpty() && percentageResponses.isEmpty()) {
            return "";
        }

        if (!flatResponses.isEmpty() && !percentageResponses.isEmpty()) {
            return flatResponses.get(0).name() + " or " + percentageResponses.get(0).name();
        }

        return flatResponses.isEmpty() ? percentageResponses.get(0).name()
            : flatResponses.get(0).name();
    }

    private Optional<CouponRoomResponse> getDiscountInfo(Long accommodationId) {
        List<CouponRoomResponse> flatResponses =
            couponService.getSortedFlatCouponInAccommodation(accommodationId);
        List<CouponRoomResponse> percentageResponses =
            couponService.getSortedPercentageCouponInAccommodation(accommodationId);

        if (!flatResponses.isEmpty() && !percentageResponses.isEmpty()) {
            int flatPrice = flatResponses.get(0).price();
            int percentPrice = percentageResponses.get(0).price();

            CouponRoomResponse bestCoupon =
                flatPrice <= percentPrice ? flatResponses.get(0) : percentageResponses.get(0);

            return Optional.of(bestCoupon);

        } else {
            return Optional.empty();
        }

    }

    @Transactional(readOnly = true)
    public AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId, LocalDate startDate, LocalDate endDate
    ) {
        Accommodation accommodation =
            accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException());

        List<Room> filterRoom = filterRoomsByDate(accommodation.getRooms(), startDate, endDate);

        return AccommodationDetailResponse.from(accommodation,
            getMainCouponName(accommodationId),
            filterRoom.stream()
                .map(room -> RoomResponse.from(
                        room, couponService.getSortedTotalCouponInRoom(room).get(0).price(),
                        checkSoldOut(filterRoom, room),
                        couponService.getSortedTotalCouponInRoom(room)
                    )
                )
                .toList()
        );
    }

    private List<Room> filterRoomsByDate(
        List<Room> rooms, LocalDate startDate, LocalDate endDate
    ) {
        List<Room> filterRoom = new ArrayList<>();

        for (Room room : rooms) {
            List<RoomStock> filteredStocks = room.getRoomStocks().stream()
                .filter(
                    stock ->
                        !(stock.getApplyDate().isBefore(startDate)) &&
                            (
                                !(stock.getStopDate().isBefore(endDate)) ||
                                    Objects.isNull(stock.getStopDate())
                            )
                            && stock.getCount() != 0
                )
                .toList();
            if (!filteredStocks.isEmpty()) {
                filterRoom.add(room);
            }
        }
        return filterRoom;
    }

    private boolean checkSoldOut(List<Room> rooms, Room room) {
        return rooms.contains(room);
    }

}
