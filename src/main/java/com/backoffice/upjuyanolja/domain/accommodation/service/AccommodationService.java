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
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationImageRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.service.RoomService;
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
    private final AccommodationImageRepository accommodationImageRepository;
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;
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
            .anyMatch(room -> !room.getCouponIssuances().isEmpty());
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
        List<CouponRoomDetailResponse> percentageResponse =
            couponService.getSortedPercentageCouponInAccommodation(accommodationId);

        if (!flatResponse.isEmpty() && !percentageResponse.isEmpty()) {
            int flatPrice = flatResponse.get(0).couponRooms().get(0).price();
            int percentPrice = percentageResponse.get(0).couponRooms().get(0).price();

            CouponRoomDetailResponse bestCoupon =
                flatPrice <= percentPrice ? flatResponse.get(0) : percentageResponse.get(0);

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

        return null;
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
        Accommodation accommodation = accommodationRepository
            .save(AccommodationRegisterRequest.toEntity(request));
        List<AccommodationImage> images = AccommodationImageRequest
            .toEntity(accommodation, request.images());
        accommodationImageRepository.saveAll(images);

        return accommodation;
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

    private Accommodation getAccommodationById(long accommodationId) {
        return accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);
    }
}
