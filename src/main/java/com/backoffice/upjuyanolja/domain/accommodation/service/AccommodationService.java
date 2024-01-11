package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationImageRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongCategoryException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationImageRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomResponse;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.service.RoomService;
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
    private final AccommodationImageRepository accommodationImageRepository;
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;
    private final CategoryRepository categoryRepository;
    private final CouponService couponService;
    private final RoomService roomService;
    private final MemberGetService memberGetService;

    @Transactional(readOnly = true)
    public AccommodationPageResponse findAccommodations(
        String category, String type, boolean onlyHasCoupon, String keyword, Pageable pageable
    ) {
        List<Accommodation> accommodations = accommodationRepository
            .findByCategoryWithTypeAndName(category, type, keyword);

        return AccommodationPageResponse.from(
            new PageImpl<>(
                accommodations.stream()
                    .filter(
                        accommodation -> !onlyHasCoupon || this.checkCouponAvailability(
                            accommodation))
                    .map(accommodation -> AccommodationSummaryResponse.of(
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
            .anyMatch(room -> !room.getCouponIssuances().isEmpty());
    }

    private int getLowestPrice(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId).orElseThrow(
            () -> new AccommodationNotFoundException()
        );
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> i));

        for (Room room : accommodation.getRooms()) {
            pq.offer(room.getPrice().getOffWeekDaysMinFee());
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
        List<CouponRoomResponse> rateResponse =
            couponService.getSortedRateCouponInAccommodation(accommodationId);

        if (!flatResponses.isEmpty() && !rateResponse.isEmpty()) {
            int flatPrice = flatResponses.get(0).price();
            int percentPrice = rateResponse.get(0).price();

            CouponRoomResponse bestCoupon =
                flatPrice <= percentPrice ? flatResponses.get(0) : rateResponse.get(0);

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

        return AccommodationDetailResponse.of(accommodation,
            getMainCouponName(accommodationId),
            filterRoom.stream()
                .map(room -> RoomResponse.of(
                        room, couponService.getSortedTotalCouponInRoom(room).get(0).price(),
                        checkSoldOut(filterRoom, room),
                        couponService.getSortedTotalCouponInRoom(room)
                    )
                )
                .toList()
        );
    }

    private Accommodation getAccommodationById(long accommodationId) {
        return accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);
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

    public AccommodationInfoResponse createAccommodation(
        long memberId,
        AccommodationRegisterRequest request
    ) {
        Member member = memberGetService.getMemberById(memberId);
        Accommodation accommodation = saveAccommodation(request);
        saveOwnership(member, accommodation);
        saveRooms(accommodation, request.rooms());
        accommodation = getAccommodationById(accommodation.getId());

        return AccommodationInfoResponse.of(accommodation);
    }

    private Accommodation saveAccommodation(AccommodationRegisterRequest request) {
        Category category = getCategoryByName(request.category());
        Accommodation accommodation = accommodationRepository
            .save(AccommodationRegisterRequest.toEntity(request, category));
        List<AccommodationImage> images = AccommodationImageRequest
            .toEntity(accommodation, request.images());
        accommodationImageRepository.saveAll(images);

        return accommodation;
    }

    private Category getCategoryByName(String name){
        return categoryRepository.findCategoryByName(name)
            .orElseThrow(()-> new WrongCategoryException());
    }

    private void saveOwnership(Member member, Accommodation accommodation) {
        accommodationOwnershipRepository.save(AccommodationOwnership.builder()
            .accommodation(accommodation)
            .member(member)
            .build());
    }

    private void saveRooms(Accommodation accommodation, List<RoomRegisterRequest> requests) {
        requests.forEach(request -> roomService.saveRoom(accommodation, request));
    }
}
