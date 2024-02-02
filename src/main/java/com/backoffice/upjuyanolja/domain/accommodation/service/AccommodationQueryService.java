package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationNameResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOptionResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationOptionNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOptionRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.service.RoomCommandService;
import com.backoffice.upjuyanolja.domain.room.service.RoomQueryService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationQueryService implements AccommodationQueryUseCase {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;
    private final AccommodationOptionRepository accommodationOptionRepository;
    private final MemberRepository memberRepository;
    private final CouponService couponService;
    private final RoomCommandService roomCommandService;
    private final RoomQueryService roomQueryService;

    @Override
    @Transactional(readOnly = true)
    public AccommodationPageResponse findAccommodations(
        String category, boolean onlyHasCoupon, String keyword, Pageable pageable
    ) {
        Page<Accommodation> accommodations = accommodationRepository
            .searchPageByCategoryWithTypeAndName(category, keyword, pageable);

        if (accommodations.isEmpty()) {
            return AccommodationPageResponse.builder()
                .accommodations(new ArrayList<>())
                .build();
        }

        return AccommodationPageResponse.of(
            new PageImpl<>(
                accommodations.stream()
                    .filter(
                        accommodation -> !onlyHasCoupon || this.checkCouponAvailability(
                            accommodation))
                    .map(accommodation -> {
                        int lowestPrice = getLowestPrice(accommodation.getId());
                        Optional<CouponDetailResponse> discountInfo = getDiscountInfo(
                            accommodation.getId());
                        return AccommodationSummaryResponse.of(
                            accommodation, lowestPrice,
                            discountInfo.map(CouponDetailResponse::price).orElse(lowestPrice),
                            discountInfo.map(CouponDetailResponse::name).orElse("")

                        );
                    })
                    .toList(),
                pageable,
                accommodations.getTotalElements()
            )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId, LocalDate startDate, LocalDate endDate
    ) {
        Accommodation accommodation = getAccommodationById(
            accommodationId);

        List<Room> filterRooms = getFilteredRoomsByDate(
            accommodation.getRooms(), startDate, endDate
        );

        AccommodationOption accommodationOption =
            getAccommodationOptionByAccommodation(accommodation);

        return AccommodationDetailResponse.of(
            accommodation,
            getMainCouponName(accommodationId),
            AccommodationOptionResponse.of(accommodationOption),
            accommodation.getRooms().stream()
                .map(room -> RoomResponse.of(
                        room,
                        roomCommandService.getRoomOption(room),
                        getDiscountPrice(room),
                        !checkSoldOut(filterRooms, room),
                        getMinFilteredRoomStock(room, startDate, endDate),
                        couponService.getSortedTotalCouponResponseInRoom(room)
                    )
                )
                .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AccommodationOwnershipResponse getAccommodationOwnership(long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
        List<AccommodationOwnership> ownerships = getOwnershipByMember(member);
        List<AccommodationNameResponse> accommodations = new ArrayList<>();
        ownerships.forEach(ownership -> accommodations.add(
            AccommodationNameResponse.of(ownership.getAccommodation())));
        return AccommodationOwnershipResponse.builder()
            .accommodations(accommodations)
            .build();
    }

    @Transactional(readOnly = true)
    public Accommodation getAccommodationById(long accommodationId) {
        return accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long accommodationId) {
        return accommodationRepository.existsById(accommodationId);
    }

    @Transactional(readOnly = true)
    public List<AccommodationOwnership> getOwnershipByMember(Member member) {
        return accommodationOwnershipRepository.findAllByMember(member);
    }

    @Transactional(readOnly = true)
    public AccommodationOption getAccommodationOptionByAccommodation(Accommodation accommodation) {
        return accommodationOptionRepository.findByAccommodation(accommodation)
            .orElseThrow(AccommodationOptionNotFoundException::new);
    }

    private boolean checkCouponAvailability(Accommodation accommodation) {
        return !couponService.findCouponResponseInAccommodation(accommodation.getId()).isEmpty();
    }

    private int getLowestPrice(Long accommodationId) {
        List<Room> rooms = roomQueryService.findByAccommodationId(accommodationId);

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> i));

        for (Room room : rooms) {
            pq.offer(room.getPrice().getOffWeekDaysMinFee());
        }

        return pq.poll();
    }

    private Optional<CouponDetailResponse> getDiscountInfo(Long accommodationId) {
        List<CouponDetailResponse> responses = new ArrayList<>();
        List<Room> rooms = roomQueryService.findByAccommodationId(accommodationId);
        List<Coupon> coupons = new ArrayList<>();

        for (Room room : rooms) {
            coupons = couponService.getCouponInRoom(room);
            responses.addAll(
                couponService.getSortedDiscountTypeCouponResponseInRoom(room, coupons,
                    DiscountType.FLAT));
            responses.addAll(
                couponService.getSortedDiscountTypeCouponResponseInRoom(room, coupons,
                    DiscountType.RATE));
        }

        return responses.stream()
            .min(Comparator.comparingInt(CouponDetailResponse::price));
    }

    private String getMainCouponName(Long accommodationId) {
        List<Room> rooms = roomQueryService.findByAccommodationId(accommodationId);
        String flatName = couponService.getDiscountTypeMainRoomCouponName(rooms, DiscountType.FLAT);
        String rateName = couponService.getDiscountTypeMainRoomCouponName(rooms, DiscountType.RATE);

        if (flatName.isEmpty() && rateName.isEmpty()) {
            return "";
        }

        if (flatName.isEmpty() || rateName.isEmpty()) {
            return flatName.isEmpty() ? rateName : flatName;
        } else {
            return flatName + " or " + rateName;
        }

    }

    @Transactional(readOnly = true)
    public Accommodation findAccommodationByRoomId(long roomId) {
        return roomQueryService.findRoomById(roomId).getAccommodation();
    }


    private int getDiscountPrice(Room room) {
        return couponService.getSortedTotalCouponResponseInRoom(room)
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
            List<RoomStock> filteredStocks = roomQueryService.getFilteredRoomStocksByDate(room,
                startDate, endDate);
            if (!filteredStocks.isEmpty()) {
                filterRoom.add(room);
            }
        }
        return filterRoom;
    }

    private int getMinFilteredRoomStock(
        Room room, LocalDate startDate, LocalDate endDate
    ) {
        return roomQueryService.getFilteredRoomStocksByDate(room, startDate, endDate).stream()
            .mapToInt(RoomStock::getCount)
            .min()
            .orElse(0);
    }

    private boolean checkSoldOut(List<Room> rooms, Room room) {
        return rooms.contains(room);
    }
}
