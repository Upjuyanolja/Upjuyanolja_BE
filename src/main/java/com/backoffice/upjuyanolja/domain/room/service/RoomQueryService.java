package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationQueryService;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomsInfoResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomOptionNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomPriceNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomStockNotFoundException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomImageRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomOptionRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomPriceRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 객실 조회 Service Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 * @author HyunA (vikim1210@naver.com)
 */
@Service
@RequiredArgsConstructor
public class RoomQueryService implements RoomQueryUseCase {

    /**
     * 숙소 Repository Interface
     */
    private final AccommodationQueryService accommodationQueryService;

    /**
     * 숙소 소유권 Repository Interface
     */
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;

    /**
     * 객실 Repository Interface
     */
    private final RoomRepository roomRepository;

    /**
     * 객실 가격 Repository Interface
     */
    private final RoomPriceRepository roomPriceRepository;

    /**
     * 객실 재고 Repository Interface
     */
    private final RoomStockRepository roomStockRepository;

    /**
     * 객실 이미지 Repository Interface
     */
    private final RoomImageRepository roomImageRepository;

    /**
     * 객실 옵션 Repository Interface
     */
    private final RoomOptionRepository roomOptionRepository;

    /**
     * 회원 조회 Service Class
     */
    private final MemberGetService memberGetService;

    /**
     * 쿠폰 Service Class
     */
    private final CouponService couponService;

    /**
     * 객실 목록 조회 메서드
     *
     * @param memberId        객실 목록을 조회할 업주 회원 식별자
     * @param accommodationId 객실 목록을 조회하고자 하는 숙소 식별자
     * @param pageable        객실 페이지네이션 조회를 위한 Pageable 객체
     * @return 객실 정보 목록 페이지
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    @Transactional(readOnly = true)
    public RoomPageResponse getRooms(
        long memberId,
        long accommodationId,
        Pageable pageable
    ) {
        // 1. 회원, 숙소 조회
        Member member = memberGetService.getMemberById(memberId);
        Accommodation accommodation = accommodationQueryService.getAccommodationById(
            accommodationId
        );

        // 2. 소유권 확인
        checkOwnership(member, accommodation);

        // 3. 객실, 객실 쿠폰, 객실 옵션, 객실 이미지 조회
        List<RoomsInfoResponse> rooms = new ArrayList<>();
        Page<Room> roomPage = roomRepository.findAllByAccommodationId(accommodationId, pageable);
        for (Room room : roomPage.get().toList()) {
            List<CouponDetailResponse> couponDetails = new ArrayList<>();
            List<Coupon> coupons = couponService.getCouponInRoom(room);
            int roomPrice = findRoomPriceByRoom(room).getOffWeekDaysMinFee();
            couponDetails.addAll(couponService
                .getSortedDiscountTypeCouponResponseInRoom(
                    room, roomPrice,
                    coupons, DiscountType.FLAT)
            );
            couponDetails.addAll(couponService
                .getSortedDiscountTypeCouponResponseInRoom(
                    room, roomPrice,
                    coupons, DiscountType.RATE)
            );
            RoomOption roomOption = findRoomOptionByRoom(room);
            List<RoomImage> roomImage = findRoomImageByRoom(room);
            rooms.add(RoomsInfoResponse.of(
                    room, roomOption, roomImage,
                    couponDetails, roomPrice
                )
            );
        }

        return RoomPageResponse.builder()
            .pageNum(roomPage.getNumber())
            .pageSize(roomPage.getSize())
            .totalPages(roomPage.getTotalPages())
            .totalElements(roomPage.getTotalElements())
            .isLast(roomPage.isLast())
            .rooms(rooms)
            .build();
    }

    /**
     * 객실 상세 조회 메서드
     *
     * @param memberId 객실 상세 정보를 조회할 업주 회원 식별자
     * @param roomId   상세 정보를 조회하고자 하는 객실 식별자
     * @return 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    @Transactional(readOnly = true)
    public RoomInfoResponse getRoom(long memberId, long roomId) {
        // 1. 회원, 객실 조회
        Member member = memberGetService.getMemberById(memberId);
        Room room = findRoomById(roomId);

        // 2. 소유권 확인
        checkOwnership(member, room.getAccommodation());

        // 3. 객실 옵션, 객실 가격, 객실 이미지 조회
        RoomOption option = findRoomOptionByRoom(room);
        int roomPrice = findRoomPriceByRoom(room).getOffWeekDaysMinFee();
        List<RoomImage> roomImage = findRoomImageByRoom(room);

        return RoomInfoResponse.of(room, option, roomImage, roomPrice);
    }

    /**
     * 숙소의 객실 정보 조회 메서드
     *
     * @param accommodation 객실 정보 목록을 조회할 숙소 Entity
     * @return 객실 정보 목록
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Transactional(readOnly = true)
    public List<RoomInfoResponse> getRoomsInfo(Accommodation accommodation) {
        List<RoomInfoResponse> roomInfoResponses = new ArrayList<>();
        for (Room room : findByAccommodationId(accommodation.getId())) {
            RoomOption roomOption = findRoomOptionByRoom(room);
            int roomPrice = findRoomPriceByRoom(room).getOffWeekDaysMinFee();
            List<RoomImage> roomImages = findRoomImageByRoom(room);
            roomInfoResponses.add(RoomInfoResponse.of(room, roomOption, roomImages, roomPrice));
        }
        return roomInfoResponses;
    }

    /**
     * 숙소 식별자로 속한 객실 Entity 리스트 조회 메서드
     *
     * @param accommodationId 객실 목록을 조회할 숙소 식별자
     * @return 객실 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Transactional(readOnly = true)
    public List<Room> findByAccommodationId(long accommodationId) {
        return roomRepository.findAllByAccommodationId(accommodationId);
    }

    /**
     * 객실 식별자로 객실 Entity를 조회하는 메서드
     *
     * @param roomId 조회하려는 객실 식별자
     * @return 객실 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Transactional(readOnly = true)
    public Room findRoomById(long roomId) {
        return roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
    }

    /**
     * 객실 Entity로 객실 옵션을 조회하는 메서드
     *
     * @param room 객실 옵션을 조회하려는 객실 Entity
     * @return 객실 옵션 Entity
     * @author HyunA (vikim1210@naver.com)
     */
    @Transactional(readOnly = true)
    public RoomOption findRoomOptionByRoom(Room room) {
        return roomOptionRepository.findByRoom(room)
            .orElseThrow(RoomOptionNotFoundException::new);
    }

    /**
     * 객실 Entity로 객실 가격을 조회하는 메서드
     *
     * @param room 객실 가격을 조회하려는 객실 Entity
     * @return 객실 가격 Entity
     * @author HyunA (vikim1210@naver.com)
     */
    @Transactional(readOnly = true)
    public RoomPrice findRoomPriceByRoom(Room room) {
        return roomPriceRepository.findByRoom(room)
            .orElseThrow(RoomPriceNotFoundException::new);
    }

    /**
     * 객실 Entity로 객실 이미지 목록을 조회하는 메서드
     *
     * @param room 객실 이미지 목록을 조회하려는 객실 Entity
     * @return 객실 이미지 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Transactional(readOnly = true)
    public List<RoomImage> findRoomImageByRoom(Room room) {
        return roomImageRepository.findByRoom(room);
    }

    /**
     * 객실 이미지 URL 문자열 리스트 조회 메서드
     *
     * @param room 객실 이미지 URL을 조회할 객실 Entity
     * @return 객실 이미지 URL 문자열 리스트
     * @author HyunA (vikim1210@naver.com)
     */
    @Transactional(readOnly = true)
    public List<String> getRoomImageUrlByRoom(Room room) {
        return roomImageRepository.findByRoom(room).stream()
            .map(image -> image.getUrl())
            .toList();
    }

    /**
     * 시작일과 종료일 사이의 객실 재고 필터링 조회 메서드
     *
     * @param room      객실 재고를 조회할 객실 Entity
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 시작일과 종료일 사이의 객실 재고 Entity 리스트
     * @author HyunA (vikim1210@naver.com)
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Transactional(readOnly = true)
    public List<RoomStock> getFilteredRoomStocksByDate(
        Room room, LocalDate startDate, LocalDate endDate
    ) {
        List<RoomStock> roomStocks = roomStockRepository.findAllByRoom(room);
        if (roomStocks.isEmpty()) {
            throw new RoomStockNotFoundException();
        }

        return roomStocks
            .stream()
            .filter(
                stock ->
                    !(stock.getDate().isBefore(startDate)) &&
                        !(stock.getDate().isAfter(endDate))
            )
            .toList();
    }

    /**
     * 객실 리스트 중 가장 최소 가격을 가지는 객실에 대한 객실, 최소가 조회 메서드
     *
     * @param rooms 객실 Entity 리스트
     * @return 객실, 최소가 Map
     * @author HyunA (vikim1210@naver.com)
     */
    @Transactional(readOnly = true)
    public Map<Room, Integer> getMinRoomPriceWithRoom(List<Room> rooms) {
        Map<Room, Integer> roomMinPriceMap = new HashMap<>();

        for (Room room : rooms) {
            int roomPrice = findRoomPriceByRoom(room).getOffWeekDaysMinFee();
            roomMinPriceMap.put(room, roomPrice);
        }

        return roomMinPriceMap;
    }

    /**
     * 숙소 소유권 체크 메서드
     *
     * @param member        숙소 소유권을 확인할 업주 회원 Entity
     * @param accommodation 소유권을 확인할 숙소 Entity
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Transactional(readOnly = true)
    public void checkOwnership(Member member, Accommodation accommodation) {
        if (!accommodationOwnershipRepository
            .existsAccommodationOwnershipByMemberAndAccommodation(member, accommodation)) {
            throw new NotOwnerException();
        }
    }
}
