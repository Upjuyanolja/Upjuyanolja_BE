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
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
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
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotExistsException;
import com.backoffice.upjuyanolja.domain.room.service.RoomQueryService;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationCommandService implements AccommodationCommandUseCase {

    private final AccommodationQueryUseCase accommodationQueryUseCase;
    private final AccommodationRepository accommodationRepository;
    private final CouponService couponService;
    private final RoomQueryService roomQueryService;
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
        String category, boolean onlyHasCoupon, String keyword, Pageable pageable
    ) {
        Page<Accommodation> accommodations = accommodationRepository
            .searchPageByCategoryWithTypeAndName(category, keyword, pageable);

        return AccommodationPageResponse.from(
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
                            discountInfo.map(response -> response.price()).orElse(lowestPrice),
                            discountInfo.map(response -> response.name()).orElse("")

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
                couponService.getSortedCouponResponseInRoom(room, coupons, DiscountType.FLAT));
            responses.addAll(
                couponService.getSortedCouponResponseInRoom(room, coupons, DiscountType.RATE));
        }

        return responses.stream()
            .min(Comparator.comparingInt(CouponDetailResponse::price));
    }

    private String getMainCouponName(Long accommodationId) {
        List<Room> rooms = roomQueryService.findByAccommodationId(accommodationId);
        List<Coupon> coupons = new ArrayList<>();
        List<CouponDetailResponse> flatResponses = new ArrayList<>();
        List<CouponDetailResponse> rateResponses = new ArrayList<>();

        for (Room room : rooms) {
            coupons = couponService.getCouponInRoom(room);
            List<CouponDetailResponse> flat = couponService.getSortedCouponResponseInRoom(
                room, coupons, DiscountType.FLAT);
            List<CouponDetailResponse> rate = couponService.getSortedCouponResponseInRoom(
                room, coupons, DiscountType.RATE);
            if (!flat.isEmpty()) {
                flatResponses.add(flat.get(0));
            }
            if (!rate.isEmpty()) {
                rateResponses.add(rate.get(0));
            }
        }

        flatResponses.sort(Comparator.comparingInt(CouponDetailResponse::price));
        rateResponses.sort(Comparator.comparingInt(CouponDetailResponse::price));

        if (flatResponses.isEmpty() && rateResponses.isEmpty()) {
            return "";
        }

        if (!flatResponses.isEmpty() && !rateResponses.isEmpty()) {
            return flatResponses.get(0).name() + " or " + rateResponses.get(0).name();
        }

        return flatResponses.isEmpty() ? rateResponses.get(0).name() : flatResponses.get(0).name();

    }

    @Override
    @Transactional(readOnly = true)
    public AccommodationDetailResponse findAccommodationWithRooms(
        Long accommodationId, LocalDate startDate, LocalDate endDate
    ) {
        Accommodation accommodation = findAccommodationById(accommodationId);

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
                        couponService.getSortedTotalCouponResponseInRoom(room)
                    )
                )
                .toList()
        );
    }

    private Accommodation findAccommodationById(Long accommodationId) {
        Accommodation accommodation =
            accommodationRepository.findById(accommodationId)
                .orElseThrow(AccommodationNotFoundException::new);
        return accommodation;
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
                .option(AccommodationOption.builder()
                    .cooking(request.option().cooking())
                    .parking(request.option().parking())
                    .pickup(request.option().pickup())
                    .barbecue(request.option().barbecue())
                    .fitness(request.option().fitness())
                    .karaoke(request.option().karaoke())
                    .sauna(request.option().sauna())
                    .sports(request.option().sports())
                    .seminar(request.option().seminar())
                    .build()
                )
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
