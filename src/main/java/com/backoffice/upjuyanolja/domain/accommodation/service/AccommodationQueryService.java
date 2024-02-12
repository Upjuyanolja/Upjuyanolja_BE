package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationNameResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationOptionNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationImageRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOptionRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
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

/**
 * 숙소 조회 Service Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class AccommodationQueryService implements AccommodationQueryUseCase {

    /**
     * 숙소 Repository Interface
     */
    private final AccommodationRepository accommodationRepository;

    /**
     * 숙소 소유권 Repository Interface
     */
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;

    /**
     * 숙소 옵션 Repository Interface
     */
    private final AccommodationOptionRepository accommodationOptionRepository;

    /**
     * 숙소 이미지 Repository Interface
     */
    private final AccommodationImageRepository accommodationImageRepository;

    /**
     * 객실 조회 UseCase Interface
     */
    private final RoomQueryUseCase roomQueryUseCase;

    /**
     * 회원 조회 Service Class
     */
    private final MemberGetService memberGetService;

    /**
     * 쿠폰 Service Class
     */
    private final CouponService couponService;

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

        List<Room> rooms = roomQueryUseCase.findByAccommodationId(accommodationId);

        List<Room> filterRooms = getFilteredRoomsByDate(
            rooms, startDate, endDate
        );

        return AccommodationDetailResponse.of(
            accommodation,
            getMainCouponName(accommodationId),
            getAccommodationOptionByAccommodation(accommodation),
            getAccommodationImageUrlByAccommodation(accommodation),
            rooms.stream()
                .map(room -> {
                        int roomPrice = roomQueryUseCase.findRoomPriceByRoom(room)
                            .getOffWeekDaysMinFee();

                        return RoomResponse.of(
                            room,
                            roomQueryUseCase.findRoomOptionByRoom(room),
                            roomQueryUseCase.findRoomPriceByRoom(room).getOffWeekDaysMinFee(),
                            getDiscountPrice(room, roomPrice),
                            !checkSoldOut(filterRooms, room),
                            getMinFilteredRoomStock(room, startDate, endDate),
                            roomQueryUseCase.getRoomImageUrlByRoom(room),
                            couponService.getSortedTotalCouponResponseInRoom(
                                room, roomPrice
                            )
                        );
                    }
                )
                .toList()
        );
    }

    /**
     * 업주가 보유한 숙소 목록 조회 메서드
     *
     * @param memberId 보유 숙소 목록을 조회할 업주 회원 식별자
     * @return 업주가 보유한 숙소 이름 응답 DTO
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    @Transactional(readOnly = true)
    public AccommodationOwnershipResponse getAccommodationOwnership(long memberId) {
        Member member = memberGetService.getMemberById(memberId);
        List<AccommodationOwnership> ownerships = getOwnershipByMember(member);

        List<AccommodationNameResponse> accommodations = new ArrayList<>();
        ownerships.forEach(ownership -> accommodations.add(
            AccommodationNameResponse.of(ownership.getAccommodation())));

        return AccommodationOwnershipResponse.builder()
            .accommodations(accommodations)
            .build();
    }

    private Accommodation getAccommodationById(long accommodationId) {
        return accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long accommodationId) {
        return accommodationRepository.existsById(accommodationId);
    }

    @Transactional(readOnly = true)
    public Accommodation findAccommodationByRoomId(long roomId) {
        return roomQueryUseCase.findRoomById(roomId).getAccommodation();
    }

    /**
     * 업주 회원의 숙소 소유권 리스트 조회 메서드
     *
     * @param member 업주 회원 Entity
     * @return 숙소 소유권 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private List<AccommodationOwnership> getOwnershipByMember(Member member) {
        return accommodationOwnershipRepository.findAllByMember(member);
    }

    private AccommodationOption getAccommodationOptionByAccommodation(Accommodation accommodation) {
        return accommodationOptionRepository.findByAccommodation(accommodation)
            .orElseThrow(AccommodationOptionNotFoundException::new);
    }

    private List<String> getAccommodationImageUrlByAccommodation(Accommodation accommodation) {
        return accommodationImageRepository.findAllByAccommodation(accommodation).stream()
            .map(image -> image.getUrl())
            .toList();
    }

    private boolean checkCouponAvailability(Accommodation accommodation) {
        return !couponService.findCouponResponseInAccommodation(accommodation.getId()).isEmpty();
    }

    private int getLowestPrice(Long accommodationId) {
        List<Room> rooms = roomQueryUseCase.findByAccommodationId(accommodationId);

        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (Room room : rooms) {
            pq.offer(roomQueryUseCase.findRoomPriceByRoom(room).getOffWeekDaysMinFee());
        }

        return pq.poll();
    }

    private Optional<CouponDetailResponse> getDiscountInfo(Long accommodationId) {
        List<CouponDetailResponse> responses = new ArrayList<>();
        List<Room> rooms = roomQueryUseCase.findByAccommodationId(accommodationId);
        List<Coupon> coupons = new ArrayList<>();

        for (Room room : rooms) {
            coupons = couponService.getCouponInRoom(room);
            responses.addAll(
                couponService.getSortedDiscountTypeCouponResponseInRoom(
                    room, roomQueryUseCase.findRoomPriceByRoom(room).getOffWeekDaysMinFee(),
                    coupons, DiscountType.FLAT));
            responses.addAll(
                couponService.getSortedDiscountTypeCouponResponseInRoom(
                    room, roomQueryUseCase.findRoomPriceByRoom(room).getOffWeekDaysMinFee(),
                    coupons, DiscountType.RATE));
        }

        return responses.stream()
            .min(Comparator.comparingInt(CouponDetailResponse::price));
    }

    private String getMainCouponName(Long accommodationId) {
        List<Room> rooms = roomQueryUseCase.findByAccommodationId(accommodationId);

        String flatName = couponService.getDiscountTypeMainRoomCouponName(
            rooms, DiscountType.FLAT, roomQueryUseCase.getMinRoomPriceWithRoom(rooms)
        );
        String rateName = couponService.getDiscountTypeMainRoomCouponName(
            rooms, DiscountType.RATE, roomQueryUseCase.getMinRoomPriceWithRoom(rooms)
        );

        if (flatName.isEmpty() && rateName.isEmpty()) {
            return "";
        }

        if (flatName.isEmpty() || rateName.isEmpty()) {
            return flatName.isEmpty() ? rateName : flatName;
        } else {
            return flatName + " or " + rateName;
        }

    }

    private int getDiscountPrice(Room room, int roomPrice) {
        return couponService.getSortedTotalCouponResponseInRoom(room, roomPrice).stream()
            .findFirst()
            .map(coupon -> coupon.price())
            .orElse(roomPrice);
    }

    private List<Room> getFilteredRoomsByDate(
        List<Room> rooms, LocalDate startDate, LocalDate endDate
    ) {
        List<Room> filterRoom = new ArrayList<>();

        for (Room room : rooms) {
            List<RoomStock> filteredStocks = roomQueryUseCase.getFilteredRoomStocksByDate(room,
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
        return roomQueryUseCase.getFilteredRoomStocksByDate(room, startDate, endDate).stream()
            .mapToInt(RoomStock::getCount)
            .min()
            .orElse(0);
    }

    private boolean checkSoldOut(List<Room> rooms, Room room) {
        return rooms.contains(room);
    }
}
