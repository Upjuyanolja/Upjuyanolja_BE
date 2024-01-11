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
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.service.RoomService;
import java.time.LocalDate;
import java.util.ArrayList;
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
@Transactional
@RequiredArgsConstructor
public class AccommodationCommandService implements AccommodationCommandUseCase {

    private final AccommodationQueryUseCase accommodationQueryUseCase;
    private final AccommodationRepository accommodationRepository;
    private final CouponService couponService;
    private final RoomService roomService;
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
            roomRegisterRequest -> roomService.saveRoom(accommodation, roomRegisterRequest));
        return AccommodationInfoResponse.of(
            accommodationQueryUseCase.getAccommodationById(accommodation.getId()));
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public AccommodationOwnershipResponse getAccommodationNames(long memberId) {
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
            .map(couponRoom -> couponRoom.couponRooms().get(0).price())
            .orElse(getLowestPrice(accommodationId));
    }

    private String getCouponName(Long accommodationId) {
        return getDiscountInfo(accommodationId)
            .map(couponRoom -> couponRoom.couponRooms().get(0).name())
            .orElse("쿠폰이 없습니다.");
    }

    private Optional<CouponRoomDetailResponse> getDiscountInfo(Long accommodationId) {
        List<CouponRoomDetailResponse> flatResponse =
            couponService.getSortedFlatCouponInAccommodation(accommodationId);
        List<CouponRoomDetailResponse> rateResponse =
            couponService.getSortedRateCouponInAccommodation(accommodationId);

        if (!flatResponse.isEmpty() && !rateResponse.isEmpty()) {
            int flatPrice = flatResponse.get(0).couponRooms().get(0).price();
            int percentPrice = rateResponse.get(0).couponRooms().get(0).price();

            CouponRoomDetailResponse bestCoupon =
                flatPrice <= percentPrice ? flatResponse.get(0) : rateResponse.get(0);

            return Optional.of(bestCoupon);

        } else {
            return Optional.empty();
        }

    }

    @Override
    @Transactional(readOnly = true)
    public AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId, LocalDate startDate, LocalDate endDate
    ) {
        Accommodation accommodation =
            accommodationRepository.findById(accommodationId)
                .orElseThrow(AccommodationNotFoundException::new);

        return null;
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
