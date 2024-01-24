package com.backoffice.upjuyanolja.domain.room.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageAddRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageDeleteRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomPageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.exception.DuplicateRoomNameException;
import com.backoffice.upjuyanolja.domain.room.service.RoomCommandService;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import jakarta.persistence.EntityManager;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
public class RoomCommandServiceTest {

    @InjectMocks
    private RoomCommandService roomCommandService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private AccommodationQueryUseCase accommodationQueryUseCase;

    @Mock
    private RoomQueryUseCase roomQueryUseCase;

    @Mock
    private EntityManager em;

    @Nested
    @DisplayName("registerRoom()은")
    class Context_registerRoom {

        @Test
        @DisplayName("객실을 추가 등록할 수 있다.")
        void _willSuccess() {
            // given
            RoomRegisterRequest roomRegisterRequest = RoomRegisterRequest.builder()
                .name("65m² 킹룸")
                .price(100000)
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .amount(858)
                .images(List.of(RoomImageRequest.builder()
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .option(RoomOptionRequest.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
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

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(accommodationQueryUseCase.getAccommodationById(any(Long.TYPE)))
                .willReturn(accommodation);
            given(accommodationQueryUseCase.existsOwnershipByMemberAndAccommodation(
                any(Member.class),
                any(Accommodation.class)))
                .willReturn(true);
            given(roomQueryUseCase
                .existsRoomByNameAndAccommodation(any(String.class), any(Accommodation.class)))
                .willReturn(false);
            given(roomQueryUseCase.saveRoom(any(Accommodation.class), any(Room.class)))
                .willReturn(room);
            given(roomQueryUseCase.saveRoomImages(any(List.class))).willReturn(List.of(roomImage));
            doNothing().when(em).refresh(any(Room.class));

            // when
            RoomInfoResponse result = roomCommandService.registerRoom(1L, 1L, roomRegisterRequest);

            // then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("65m² 킹룸");
            assertThat(result.status()).isEqualTo("SELLING");
            assertThat(result.defaultCapacity()).isEqualTo(2);
            assertThat(result.maxCapacity()).isEqualTo(3);
            assertThat(result.checkInTime()).isEqualTo("15:00");
            assertThat(result.checkOutTime()).isEqualTo("11:00");
            assertThat(result.price()).isEqualTo(100000);
            assertThat(result.images()).isNotNull();
            assertThat(result.option()).isNotNull();
            assertThat(result.option().airCondition()).isEqualTo(true);
            assertThat(result.option().tv()).isEqualTo(true);
            assertThat(result.option().internet()).isEqualTo(true);

            verify(memberGetService, times(1)).getMemberById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1)).getAccommodationById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1)).existsOwnershipByMemberAndAccommodation(
                any(Member.class),
                any(Accommodation.class));
            verify(roomQueryUseCase, times(1)).existsRoomByNameAndAccommodation(any(String.class),
                any(Accommodation.class));
            verify(roomQueryUseCase, times(1)).saveRoom(any(Accommodation.class), any(Room.class));
            verify(roomQueryUseCase, times(1)).saveRoomImages(any(List.class));
        }

        @Test
        @DisplayName("숙소 업주가 아니면 객실을 추가 등록할 수 없다.")
        void notOwner_willFail() {
            // given
            RoomRegisterRequest roomRegisterRequest = RoomRegisterRequest.builder()
                .name("65m² 킹룸")
                .price(100000)
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .amount(858)
                .images(List.of(RoomImageRequest.builder()
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .option(RoomOptionRequest.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
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

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(accommodationQueryUseCase.getAccommodationById(any(Long.TYPE)))
                .willReturn(accommodation);
            given(accommodationQueryUseCase.existsOwnershipByMemberAndAccommodation(
                any(Member.class),
                any(Accommodation.class)))
                .willReturn(false);

            // when
            Throwable exception = assertThrows(NotOwnerException.class, () -> {
                roomCommandService.registerRoom(1L, 1L, roomRegisterRequest);
            });

            // then
            assertEquals(exception.getMessage(), "숙소의 업주가 아닙니다.");

            verify(memberGetService, times(1)).getMemberById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1)).getAccommodationById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1)).existsOwnershipByMemberAndAccommodation(
                any(Member.class),
                any(Accommodation.class));
            verify(roomQueryUseCase, never()).existsRoomByNameAndAccommodation(any(String.class),
                any(Accommodation.class));
            verify(roomQueryUseCase, never()).saveRoom(any(Accommodation.class), any(Room.class));
            verify(roomQueryUseCase, never()).saveRoomImages(any(List.class));
            verify(roomQueryUseCase, never()).findRoomById(any(Long.TYPE));
        }
    }

    @Nested
    @DisplayName("saveRoom()은")
    class Context_saveRoom {

        @Test
        @DisplayName("객실을 등록할 수 있다.")
        void _willSuccess() {
            // given
            RoomRegisterRequest roomRegisterRequest = RoomRegisterRequest.builder()
                .name("65m² 킹룸")
                .price(100000)
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .amount(858)
                .images(List.of(RoomImageRequest.builder()
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .option(RoomOptionRequest.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
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

            given(roomQueryUseCase.existsRoomByName(any(String.class))).willReturn(false);
            given(roomQueryUseCase.saveRoom(any(Accommodation.class), any(Room.class))).willReturn(
                room);
            given(roomQueryUseCase.saveRoomImages(any(List.class))).willReturn(List.of(roomImage));
            given(roomQueryUseCase.findRoomById(any(Long.TYPE))).willReturn(savedRoom);

            // when
            RoomInfoResponse result = roomCommandService.saveRoom(accommodation,
                roomRegisterRequest);

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

            verify(roomQueryUseCase, times(1)).existsRoomByName(any(String.class));
            verify(roomQueryUseCase, times(1)).saveRoom(any(Accommodation.class), any(Room.class));
            verify(roomQueryUseCase, times(1)).saveRoomImages(any(List.class));
            verify(roomQueryUseCase, times(1)).findRoomById(any(Long.TYPE));
        }

        @Test
        @DisplayName("중복된 객실 이름을 가진 객실은 등록할 수 없다.")
        void duplicatedRoomName_willFail() {
            // given
            RoomRegisterRequest roomRegisterRequest = RoomRegisterRequest.builder()
                .name("65m² 킹룸")
                .price(100000)
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .amount(858)
                .images(List.of(RoomImageRequest.builder()
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .option(RoomOptionRequest.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
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

            given(roomQueryUseCase
                .existsRoomByNameAndAccommodation(any(String.class), any(Accommodation.class)))
                .willReturn(true);

            // when
            Throwable exception = assertThrows(DuplicateRoomNameException.class, () -> {
                roomCommandService.saveRoom(accommodation, roomRegisterRequest);
            });

            // then
            assertEquals(exception.getMessage(), "중복된 객실 이름입니다.");

            verify(roomQueryUseCase, times(1))
                .existsRoomByNameAndAccommodation(any(String.class), any(Accommodation.class));
        }
    }

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

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(accommodationQueryUseCase.getAccommodationById(any(Long.TYPE)))
                .willReturn(accommodation);
            given(accommodationQueryUseCase.existsOwnershipByMemberAndAccommodation(
                any(Member.class),
                any(Accommodation.class)))
                .willReturn(true);
            given(roomQueryUseCase.findAllByAccommodationId(any(Long.TYPE), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(savedRoom)));

            // when
            RoomPageResponse result = roomCommandService.getRooms(1L, 1L, roomPageRequest.of());

            // then
            assertThat(result.rooms().get(0).id()).isEqualTo(1L);
            assertThat(result.rooms().get(0).name()).isEqualTo("65m² 킹룸");
            assertThat(result.rooms().get(0).status()).isEqualTo("SELLING");
            assertThat(result.rooms().get(0).defaultCapacity()).isEqualTo(2);
            assertThat(result.rooms().get(0).maxCapacity()).isEqualTo(3);
            assertThat(result.rooms().get(0).checkInTime()).isEqualTo("15:00");
            assertThat(result.rooms().get(0).checkOutTime()).isEqualTo("11:00");
            assertThat(result.rooms().get(0).price()).isEqualTo(100000);
            assertThat(result.rooms().get(0).images()).isNotEmpty();
            assertThat(result.rooms().get(0).images().get(0).id()).isEqualTo(1L);
            assertThat(result.rooms().get(0).images().get(0).url()).isEqualTo(
                "http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg");
            assertThat(result.rooms().get(0).option()).isNotNull();
            assertThat(result.rooms().get(0).option().airCondition()).isEqualTo(true);
            assertThat(result.rooms().get(0).option().tv()).isEqualTo(true);
            assertThat(result.rooms().get(0).option().internet()).isEqualTo(true);

            verify(memberGetService, times(1)).getMemberById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1)).getAccommodationById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1))
                .existsOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class)
                );
            verify(roomQueryUseCase, times(1))
                .findAllByAccommodationId(any(Long.TYPE), any(Pageable.class));
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
            given(roomQueryUseCase.findRoomById(any(Long.TYPE))).willReturn(room);
            given(accommodationQueryUseCase.existsOwnershipByMemberAndAccommodation(
                any(Member.class),
                any(Accommodation.class)))
                .willReturn(true);
            given(roomQueryUseCase.findRoomById(any(Long.TYPE))).willReturn(room);

            // when
            RoomInfoResponse result = roomCommandService.getRoom(1L, 1L);

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
            verify(roomQueryUseCase, times(1)).findRoomById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1))
                .existsOwnershipByMemberAndAccommodation(any(Member.class),
                    any(Accommodation.class));
        }
    }

    @Nested
    @DisplayName("modifyRoom()은")
    class Context_modifyRoom {

        @Test
        @DisplayName("객실을 수정할 수 있다.")
        void _willSuccess() {
            // given
            RoomUpdateRequest roomUpdateRequest = RoomUpdateRequest.builder()
                .name("65m² 킹룸")
                .price(200000)
                .status("STOP_SELLING")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .amount(858)
                .addImages(List.of(RoomImageAddRequest.builder()
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .deleteImages(List.of(RoomImageDeleteRequest.builder()
                    .id(1L)
                    .build()))
                .option(RoomOptionRequest.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
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

            RoomImage roomImage2 = RoomImage.builder()
                .id(2L)
                .room(room)
                .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_2.jpg")
                .build();
            RoomImage roomImage3 = RoomImage.builder()
                .id(3L)
                .room(room)
                .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_3.jpg")
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
                    .offWeekDaysMinFee(200000)
                    .offWeekendMinFee(200000)
                    .peakWeekDaysMinFee(200000)
                    .peakWeekendMinFee(200000)
                    .build())
                .amount(858)
                .status(RoomStatus.STOP_SELLING)
                .option(RoomOption.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .images(List.of(roomImage2, roomImage3))
                .build();

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(roomQueryUseCase.findRoomById(any(Long.TYPE))).willReturn(room);
            given(accommodationQueryUseCase.existsOwnershipByMemberAndAccommodation(
                any(Member.class),
                any(Accommodation.class)))
                .willReturn(true);
            given(roomQueryUseCase.saveRoomImages(any(List.class)))
                .willReturn(List.of(roomImage2, roomImage3));
            given(roomQueryUseCase.findRoomImage(any(Long.TYPE)))
                .willReturn(roomImage1);
            doNothing().when(roomQueryUseCase).deleteRoomImages(any(List.class));
            doNothing().when(em).refresh(any(Room.class));

            // when
            RoomInfoResponse result = roomCommandService.modifyRoom(1L, 1L, roomUpdateRequest);

            // then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("65m² 킹룸");
            assertThat(result.status()).isEqualTo("STOP_SELLING");
            assertThat(result.defaultCapacity()).isEqualTo(2);
            assertThat(result.maxCapacity()).isEqualTo(3);
            assertThat(result.checkInTime()).isEqualTo("15:00");
            assertThat(result.checkOutTime()).isEqualTo("11:00");
            assertThat(result.price()).isEqualTo(200000);
            assertThat(result.images()).isNotNull();
            assertThat(result.option()).isNotNull();
            assertThat(result.option().airCondition()).isEqualTo(true);
            assertThat(result.option().tv()).isEqualTo(true);
            assertThat(result.option().internet()).isEqualTo(true);

            verify(memberGetService, times(1)).getMemberById(any(Long.TYPE));
            verify(roomQueryUseCase, times(1)).findRoomById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1))
                .existsOwnershipByMemberAndAccommodation(any(Member.class),
                    any(Accommodation.class));
            verify(roomQueryUseCase, times(1)).saveRoomImages(any(List.class));
            verify(roomQueryUseCase, times(1)).findRoomImage(any(Long.TYPE));
            verify(roomQueryUseCase, times(1)).deleteRoomImages(any(List.class));
        }
    }

    @Nested
    @DisplayName("deleteRoom()은")
    class Context_deleteRoom {

        @Test
        @DisplayName("객실을 삭제할 수 있다.")
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
            given(roomQueryUseCase.findRoomById(any(Long.TYPE))).willReturn(room);
            given(accommodationQueryUseCase.existsOwnershipByMemberAndAccommodation(
                any(Member.class),
                any(Accommodation.class)))
                .willReturn(true);

            // when
            RoomInfoResponse result = roomCommandService.deleteRoom(1L, 1L);

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
            verify(roomQueryUseCase, times(1)).findRoomById(any(Long.TYPE));
            verify(accommodationQueryUseCase, times(1))
                .existsOwnershipByMemberAndAccommodation(any(Member.class),
                    any(Accommodation.class));
        }
    }
}
