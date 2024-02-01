package com.backoffice.upjuyanolja.domain.room.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomPageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.domain.room.service.RoomQueryService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class RoomQueryServiceTest {

    @InjectMocks
    private RoomQueryService roomQueryService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationOwnershipRepository accommodationOwnershipRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomStockRepository roomStockRepository;

    @Mock
    private CouponService couponService;

    @Nested
    @DisplayName("getRooms()은")
    class Context_getRooms {

        @Test
        @DisplayName("객실 목록을 조회할 수 있다.")
        void _willSuccess() {
            // given
            RoomPageRequest roomPageRequest = RoomPageRequest.builder()
                .pageNum(0)
                .pageSize(10)
                .build();

            Member member = Member.builder()
                .id(1L)
                .email("test@mail.com")
                .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
                .name("test")
                .phone("010-1234-1234")
                .imageUrl(
                    "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
                .authority(Authority.ROLE_USER)
                .build();

            Category category = Category.builder()
                .id(5L)
                .name("TOURIST_HOTEL")
                .build();

            Accommodation accommodation = Accommodation.builder()
                .name("그랜드 하얏트 제주")
                .address(Address.builder()
                    .address("제주특별자치도 제주시 노형동 925")
                    .detailAddress("")
                    .build())
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
                .option(AccommodationOption.builder()
                    .cooking(false)
                    .parking(true)
                    .pickup(false)
                    .barbecue(false)
                    .fitness(true)
                    .karaoke(false)
                    .sauna(false)
                    .sports(true)
                    .seminar(true)
                    .build())
                .images(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();

            Room room = Room.builder()
                .id(1L)
                .accommodation(accommodation)
                .name("65m² 킹룸")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime(LocalTime.of(15, 0, 0))
                .checkOutTime(LocalTime.of(11, 0, 0))
                .price(RoomPrice.builder()
                    .offWeekDaysMinFee(100000)
                    .offWeekendMinFee(100000)
                    .peakWeekDaysMinFee(100000)
                    .peakWeekendMinFee(100000)
                    .build())
                .amount(858)
                .status(RoomStatus.SELLING)
                .option(RoomOption.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .images(new ArrayList<>())
                .build();

            RoomImage roomImage = RoomImage.builder()
                .id(1L)
                .room(room)
                .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                .build();

            Room savedRoom = Room.builder()
                .id(1L)
                .accommodation(accommodation)
                .name("65m² 킹룸")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime(LocalTime.of(15, 0, 0))
                .checkOutTime(LocalTime.of(11, 0, 0))
                .price(RoomPrice.builder()
                    .offWeekDaysMinFee(100000)
                    .offWeekendMinFee(100000)
                    .peakWeekDaysMinFee(100000)
                    .peakWeekendMinFee(100000)
                    .build())
                .amount(858)
                .status(RoomStatus.SELLING)
                .option(RoomOption.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .images(List.of(roomImage))
                .build();
            List<Coupon> coupons = List.of(
                Coupon.builder()
                    .id(1L)
                    .room(savedRoom)
                    .couponType(CouponType.ALL_DAYS)
                    .discountType(DiscountType.FLAT)
                    .couponStatus(CouponStatus.ENABLE)
                    .discount(10000)
                    .endDate(LocalDate.MAX)
                    .dayLimit(10)
                    .build(),
                Coupon.builder()
                    .id(2L)
                    .room(savedRoom)
                    .couponType(CouponType.ALL_DAYS)
                    .discountType(DiscountType.RATE)
                    .couponStatus(CouponStatus.ENABLE)
                    .discount(20)
                    .endDate(LocalDate.MAX)
                    .dayLimit(10)
                    .build(),
                Coupon.builder()
                    .id(3L)
                    .room(savedRoom)
                    .couponType(CouponType.ALL_DAYS)
                    .discountType(DiscountType.FLAT)
                    .couponStatus(CouponStatus.ENABLE)
                    .discount(15000)
                    .endDate(LocalDate.MAX)
                    .dayLimit(10)
                    .build()
            );
            List<CouponDetailResponse> flatCoupons = List.of(
                CouponDetailResponse.builder()
                    .id(1L)
                    .name("10000원 할인")
                    .price(90000)
                    .build(),
                CouponDetailResponse.builder()
                    .id(3L)
                    .name("15000원 할인")
                    .price(95000)
                    .build()
            );
            List<CouponDetailResponse> rateCoupons = List.of(
                CouponDetailResponse.builder()
                    .id(2L)
                    .name("20% 할인")
                    .price(80000)
                    .build()
            );

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(accommodationRepository.findById(any(Long.TYPE)))
                .willReturn(Optional.of(accommodation));
            given(accommodationOwnershipRepository
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class)))
                .willReturn(true);
            given(roomRepository.findAllByAccommodation(any(Long.TYPE), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(savedRoom)));
            given(couponService.getCouponInRoom(any(Room.class))).willReturn(coupons);
            given(couponService.getSortedDiscountTypeCouponResponseInRoom(
                any(Room.class),
                any(List.class),
                eq(DiscountType.FLAT)
            )).willReturn(flatCoupons);
            given(couponService.getSortedDiscountTypeCouponResponseInRoom(
                any(Room.class),
                any(List.class),
                eq(DiscountType.RATE)
            )).willReturn(rateCoupons);

            // when
            RoomPageResponse result = roomQueryService.getRooms(1L, 1L, roomPageRequest.of());

            // then
            assertThat(result.rooms().get(0).id()).isEqualTo(1L);
            assertThat(result.rooms().get(0).name()).isEqualTo("65m² 킹룸");
            assertThat(result.rooms().get(0).status()).isEqualTo("SELLING");
            assertThat(result.rooms().get(0).defaultCapacity()).isEqualTo(2);
            assertThat(result.rooms().get(0).maxCapacity()).isEqualTo(3);
            assertThat(result.rooms().get(0).checkInTime()).isEqualTo("15:00");
            assertThat(result.rooms().get(0).checkOutTime()).isEqualTo("11:00");
            assertThat(result.rooms().get(0).basePrice()).isEqualTo(100000);
            assertThat(result.rooms().get(0).discountPrice()).isEqualTo(80000);
            assertThat(result.rooms().get(0).images()).isNotEmpty();
            assertThat(result.rooms().get(0).images().get(0).id()).isEqualTo(1L);
            assertThat(result.rooms().get(0).images().get(0).url()).isEqualTo(
                "http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg");
            assertThat(result.rooms().get(0).option()).isNotNull();
            assertThat(result.rooms().get(0).option().airCondition()).isEqualTo(true);
            assertThat(result.rooms().get(0).option().tv()).isEqualTo(true);
            assertThat(result.rooms().get(0).option().internet()).isEqualTo(true);
            assertThat(result.rooms().get(0).coupons()).isNotEmpty();

            verify(memberGetService, times(1)).getMemberById(any(Long.TYPE));
            verify(accommodationRepository, times(1)).findById(any(Long.TYPE));
            verify(accommodationOwnershipRepository, times(1))
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class)
                );
            verify(roomRepository, times(1))
                .findAllByAccommodation(any(Long.TYPE), any(Pageable.class));
            verify(couponService, times(1)).getCouponInRoom(any(Room.class));
            verify(couponService, times(1)).getSortedDiscountTypeCouponResponseInRoom(
                any(Room.class),
                any(List.class),
                eq(DiscountType.FLAT)
            );
            verify(couponService, times(1)).getSortedDiscountTypeCouponResponseInRoom(
                any(Room.class),
                any(List.class),
                eq(DiscountType.RATE)
            );
        }
    }

    @Nested
    @DisplayName("getRoom()은")
    class Context_getRoom {

        @Test
        @DisplayName("객실을 조회할 수 있다.")
        void _willSuccess() {
            // given
            Member member = Member.builder()
                .id(1L)
                .email("test@mail.com")
                .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
                .name("test")
                .phone("010-1234-1234")
                .imageUrl(
                    "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
                .authority(Authority.ROLE_USER)
                .build();

            Category category = Category.builder()
                .id(5L)
                .name("TOURIST_HOTEL")
                .build();

            Accommodation accommodation = Accommodation.builder()
                .name("그랜드 하얏트 제주")
                .address(Address.builder()
                    .address("제주특별자치도 제주시 노형동 925")
                    .detailAddress("")
                    .build())
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
                .option(AccommodationOption.builder()
                    .cooking(false)
                    .parking(true)
                    .pickup(false)
                    .barbecue(false)
                    .fitness(true)
                    .karaoke(false)
                    .sauna(false)
                    .sports(true)
                    .seminar(true)
                    .build())
                .images(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();

            RoomImage roomImage1 = RoomImage.builder()
                .id(1L)
                .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                .build();

            Room room = Room.builder()
                .id(1L)
                .accommodation(accommodation)
                .name("65m² 킹룸")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime(LocalTime.of(15, 0, 0))
                .checkOutTime(LocalTime.of(11, 0, 0))
                .price(RoomPrice.builder()
                    .offWeekDaysMinFee(100000)
                    .offWeekendMinFee(100000)
                    .peakWeekDaysMinFee(100000)
                    .peakWeekendMinFee(100000)
                    .build())
                .amount(858)
                .status(RoomStatus.SELLING)
                .option(RoomOption.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .images(List.of(roomImage1))
                .build();

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(roomRepository.findById(any(Long.TYPE))).willReturn(Optional.of(room));
            given(accommodationOwnershipRepository
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class)))
                .willReturn(true);

            // when
            RoomInfoResponse result = roomQueryService.getRoom(1L, 1L);

            // then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("65m² 킹룸");
            assertThat(result.status()).isEqualTo("SELLING");
            assertThat(result.defaultCapacity()).isEqualTo(2);
            assertThat(result.maxCapacity()).isEqualTo(3);
            assertThat(result.checkInTime()).isEqualTo("15:00");
            assertThat(result.checkOutTime()).isEqualTo("11:00");
            assertThat(result.price()).isEqualTo(100000);
            assertThat(result.images()).isNotEmpty();
            assertThat(result.images().get(0).id()).isEqualTo(1L);
            assertThat(result.images().get(0).url()).isEqualTo(
                "http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg");
            assertThat(result.option()).isNotNull();
            assertThat(result.option().airCondition()).isEqualTo(true);
            assertThat(result.option().tv()).isEqualTo(true);
            assertThat(result.option().internet()).isEqualTo(true);

            verify(memberGetService, times(1)).getMemberById(any(Long.TYPE));
            verify(roomRepository, times(1)).findById(any(Long.TYPE));
            verify(accommodationOwnershipRepository, times(1))
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class));
        }
    }
}
