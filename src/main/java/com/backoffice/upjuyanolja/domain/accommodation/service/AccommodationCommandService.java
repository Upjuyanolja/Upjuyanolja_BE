package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationImageRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationNameResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationCommandUseCase;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase.AccommodationSaveRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.service.RoomService;
import com.backoffice.upjuyanolja.domain.room.service.RoomCommandService;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
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
@Transactional
@RequiredArgsConstructor
public class AccommodationCommandService implements AccommodationCommandUseCase {

    private final AccommodationQueryUseCase accommodationQueryUseCase;
    private final AccommodationRepository accommodationRepository;
    private final CouponService couponService;
    private final RoomCommandUseCase roomCommandUseCase;
    private final MemberGetService memberGetService;

    @Override
    public AccommodationInfoResponse createAccommodation(
        long memberId,
        AccommodationRegisterRequest request
    ) {
        Member member = memberGetService.getMemberById(memberId);
        Accommodation accommodation = saveAccommodation(request);
        accommodationQueryUseCase.saveOwnership(member, accommodation);
        request.rooms().forEach(
            roomRegisterRequest -> roomCommandUseCase.saveRoom(accommodation, roomRegisterRequest));
        return AccommodationInfoResponse.of(
            accommodationQueryUseCase.getAccommodationById(accommodation.getId()));
    }

    @Override
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

    @Override
    public AccommodationOwnershipResponse getAccommodationOwnership(long memberId) {
        Member member = memberGetService.getMemberById(memberId);
        List<AccommodationOwnership> ownerships = accommodationQueryUseCase
            .getOwnershipByMember(member);
        List<AccommodationNameResponse> accommodations = new ArrayList<>();
        ownerships.forEach(ownership -> accommodations.add(
            AccommodationNameResponse.of(ownership.getAccommodation())));
        return AccommodationOwnershipResponse.builder()
            .accommodations(accommodations)
            .build();
    }

    private boolean checkCouponAvailability(Accommodation accommodation) {
        return accommodation.getRooms().stream()
            .anyMatch(room -> !room.getCouponIssuances().isEmpty());
    }

    private int getLowestPrice(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId).orElseThrow(
            AccommodationNotFoundException::new
        );
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> i));

        for (Room room : accommodation.getRooms()) {
            pq.offer(room.getPrice().getOffWeekDaysMinFee());
        }

        return pq.poll();
    }

    private int getDiscountPrice(Long accommodationId) {
        return getDiscountInfo(accommodationId)
            .map(couponIssuance -> couponIssuance.price())
            .orElse(getLowestPrice(accommodationId));
    }

    private String getCouponName(Long accommodationId) {
        return getDiscountInfo(accommodationId)
            .map(couponIssuance -> couponIssuance.name())
            .orElse("");
    }

    private Optional<CouponDetailResponse> getDiscountInfo(Long accommodationId) {
        List<CouponDetailResponse> responses = new ArrayList<>();
        responses.addAll(couponService.getSortedFlatCouponInAccommodation(accommodationId));
        responses.addAll(couponService.getSortedRateCouponInAccommodation(accommodationId));

        return responses.stream()
            .min(Comparator.comparingInt(CouponDetailResponse::price));
    }

    private String getMainCouponName(Long accommodationId) {
        Optional<CouponDetailResponse> flatResponse =
            couponService.getSortedFlatCouponInAccommodation(accommodationId).stream()
                .findFirst();

        Optional<CouponDetailResponse> rateResponse =
            couponService.getSortedRateCouponInAccommodation(accommodationId).stream()
                .findFirst();

        if (flatResponse.isEmpty() && rateResponse.isEmpty()) {
            return "";
        }

        if (!flatResponse.isEmpty() && !rateResponse.isEmpty()) {
            return flatResponse.get().name()+" or "+rateResponse.get().name();
        }

        return flatResponse.orElse(rateResponse.get()).name();

    }

    @Override
    public AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId, LocalDate startDate, LocalDate endDate
    ) {
        Accommodation accommodation =
            accommodationRepository.findById(accommodationId)
                .orElseThrow(AccommodationNotFoundException::new);

        List<Room> filterRooms = getFilteredRoomsByDate(
            accommodation.getRooms(), startDate, endDate
        );

        return AccommodationDetailResponse.of(accommodation,
            getMainCouponName(accommodationId),
            accommodation.getRooms().stream()
                .map(room -> RoomResponse.of(
                        room, getDiscountPrice(room),
                        !checkSoldOut(filterRooms, room),
                        getMinFilteredRoomStock(room, startDate, endDate),
                        couponService.getSortedTotalCouponInRoom(room)
                    )
                )
                .toList()
        );
    }

    private int getDiscountPrice(Room room) {
        return couponService.getSortedTotalCouponInRoom(room)
            .stream()
            .findFirst()
            .map(coupon -> coupon.price())
            .orElse(room.getPrice().getOffWeekDaysMinFee());
    }

    private List<Room> getFilteredRoomsByDate(
        List<Room> rooms, LocalDate startDate, LocalDate endDate
    ) {
        List<Room> filterRoom = new ArrayList<>();

        for (Room room : rooms) {
            List<RoomStock> filteredStocks = getFilteredRoomStocksByDate(room, startDate, endDate);
            if (!filteredStocks.isEmpty()) {
                filterRoom.add(room);
            }
        }
        return filterRoom;
    }

    private List<RoomStock> getFilteredRoomStocksByDate(
        Room room, LocalDate startDate, LocalDate endDate
    ) {
        return roomService.findStockByRoom(room).stream()
            .filter(
                stock ->
                    !(stock.getDate().isBefore(startDate)) &&
                        !(stock.getDate().isBefore(endDate)) &&
                        stock.getCount() != 0
            )
            .toList();
    }

    private int getMinFilteredRoomStock(
        Room room, LocalDate startDate, LocalDate endDate
    ) {
        return getFilteredRoomStocksByDate(room, startDate, endDate).stream()
            .mapToInt(RoomStock::getCount)
            .min()
            .orElse(0);
    }

    private boolean checkSoldOut(List<Room> rooms, Room room) {
        return rooms.contains(room);
    }

    private Accommodation saveAccommodation(AccommodationRegisterRequest request) {
        Category category = accommodationQueryUseCase.getCategoryByName(request.category());
        Accommodation accommodation = accommodationQueryUseCase.saveAccommodation(
            AccommodationSaveRequest.builder()
                .name(request.name())
                .address(Address.builder()
                    .address(request.address())
                    .detailAddress(request.detailAddress())
                    .build())
                .description(request.description())
                .category(category)
                .thumbnail(request.thumbnail())
                .build());
        accommodationQueryUseCase.saveAllImages(AccommodationImageRequest
            .toEntity(accommodation, request.images()));

        return accommodation;
    }
}
