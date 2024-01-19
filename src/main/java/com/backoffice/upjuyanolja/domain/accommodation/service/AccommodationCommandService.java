package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationImageRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationNameResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.ImageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.ImageUrlResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationImageNotExistsException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.FailedSaveImageException;
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
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotExistsException;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationCommandService implements AccommodationCommandUseCase {

    private final AccommodationQueryUseCase accommodationQueryUseCase;
    private final AccommodationRepository accommodationRepository;
    private final CouponService couponService;
    private final RoomCommandUseCase roomCommandUseCase;
    private final MemberGetService memberGetService;
    private final S3UploadService s3UploadService;

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

    @Override
    public ImageResponse saveImages(List<MultipartFile> imageMultipartFiles) {
        List<ImageUrlResponse> imageUrls = new ArrayList<>();

        for (MultipartFile multipartFile : imageMultipartFiles) {
            try {
                if (multipartFile.isEmpty()) {
                    imageUrls.add(ImageUrlResponse.builder()
                        .url(null)
                        .build());
                } else {
                    imageUrls.add(ImageUrlResponse.builder()
                        .url(s3UploadService.saveFile(multipartFile))
                        .build());
                }
            } catch (IOException e) {
                throw new FailedSaveImageException();
            }
        }

        return ImageResponse.builder()
            .urls(imageUrls)
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
            return flatResponse.get().name() + " or " + rateResponse.get().name();
        }

        return flatResponse.orElse(rateResponse.get()).name();

    }

    @Override
    @Transactional(readOnly = true)
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
            List<RoomStock> filteredStocks = roomCommandUseCase.getFilteredRoomStocksByDate(room,
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
        return roomCommandUseCase.getFilteredRoomStocksByDate(room, startDate, endDate).stream()
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
                    .zipCode(request.zipCode())
                    .build())
                .description(request.description())
                .category(category)
                .thumbnail(request.thumbnail())
                .build());

        if (request.images().isEmpty()) {
            throw new AccommodationImageNotExistsException();
        }
        if (request.rooms().isEmpty()) {
            throw new RoomNotExistsException();
        }
        accommodationQueryUseCase.saveAllImages(AccommodationImageRequest
            .toEntity(accommodation, request.images()));

        return accommodation;
    }
}
