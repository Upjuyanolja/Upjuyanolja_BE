package com.backoffice.upjuyanolja.domain.coupon.unit.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponRoomsRequest;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService 단위 테스트")
class CouponBackofficeServiceTest {

    @InjectMocks
    CouponBackofficeService couponBackofficeService;

    @Mock
    MemberGetService memberGetService;

    @Mock
    CouponRepository couponRepository;

    @Mock
    PointRepository pointRepository;

    Member mockMember;
    Accommodation mockAccommodation;
    AccommodationOwnership mockAccommodationOwnership;
    Point mockPoint;

    @BeforeEach
    public void initTest() {
        mockMember = createMember(1L);
        mockAccommodation = createAccommodation(1L);
        mockAccommodationOwnership = createAccommodationOwnership();
    }

    @DisplayName("로그인한 업주의 id와 동록된 숙소의 id의 다르면 예외가 발생하는지 검증한다.")
    @Test
    public void givenDifferentMemberIdAndAccommodationId_thenReturnFalse_test() throws Exception {
        // given
        List<CouponRoomsRequest> mockRequests = List.of(
            new CouponRoomsRequest(1L, DiscountType.FLAT, 5000, 10, 10000),
            new CouponRoomsRequest(2L, DiscountType.RATE, 5, 20, 10000),
            new CouponRoomsRequest(3L, DiscountType.FLAT, 1000, 50, 10000)
        );
        CouponMakeRequest mockCouponMakeRequest = CouponMakeRequest.builder()
            .accommodationId(1L)
            .totalPoints(30000)
            .rooms(mockRequests)
            .build();

        // when & then
        when(couponRepository.existsAccommodationIdByMemberId(1L, 2L))
            .thenThrow(AccommodationNotFoundException.class);

        assertThrows(
            AccommodationNotFoundException.class,
            () -> couponRepository.existsAccommodationIdByMemberId(1L, 2L)
        );

        verify(couponRepository).existsAccommodationIdByMemberId(1L, 2L);
    }

    private Member createMember(Long id) {
        return Member.builder()
            .id(id)
            .email("test1@tester.com")
            .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
            .name("tester")
            .phone("010-1234-1234")
            .imageUrl(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
            .authority(Authority.ROLE_ADMIN)
            .build();
    }

    private Accommodation createAccommodation(long accommodationId) {
        Category category = Category.builder()
            .id(5L)
            .name("TOURIST_HOTEL")
            .build();

        Accommodation accommodation = Accommodation.builder()
            .id(accommodationId)
            .name("그랜드 하얏트 제주")
            .address("제주특별자치도 제주시 노형동 925")
            .detailAddress("")
            .zipCode("63082")
            .category(category)
            .description(
                "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
            .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
            .build();
        return accommodation;
    }

    private AccommodationOwnership createAccommodationOwnership() {
        return AccommodationOwnership.builder()
            .id(1L)
            .accommodation(mockAccommodation)
            .member(mockMember)
            .build();
    }

    private Point createPoint(Long pointId, Member member, long balance) {
        return Point.builder()
            .id(pointId)
            .member(member)
            .totalPointBalance(balance)
            .build();
    }

    private List<Room> createRooms(
        Accommodation accommodation, List<Long> roomIds, List<String> roomNames
    ) {
        List<Room> rooms = List.of(
            createRoom(roomIds.get(0), roomNames.get(0), accommodation),
            createRoom(roomIds.get(1), roomNames.get(1), accommodation),
            createRoom(roomIds.get(2), roomNames.get(2), accommodation)
        );
        return rooms;
    }

    private Room createRoom(
        Long roomId,
        String roomName,
        Accommodation accommodation
    ) {
        return Room.builder()
            .id(roomId)
            .accommodation(accommodation)
            .name(roomName)
            .defaultCapacity(2)
            .maxCapacity(3)
            .checkInTime(LocalTime.of(15, 0, 0))
            .checkOutTime(LocalTime.of(11, 0, 0))
            .amount(858)
            .status(RoomStatus.SELLING)
            .build();
    }
}
