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
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageAddRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageDeleteRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.exception.DuplicateRoomNameException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomImageRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomOptionRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.domain.room.service.RoomCommandService;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import com.backoffice.upjuyanolja.global.exception.NotOwnerException;
import jakarta.persistence.EntityManager;
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
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class RoomCommandServiceTest {

    @InjectMocks
    private RoomCommandService roomCommandService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private RoomQueryUseCase roomQueryUseCase;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomOptionRepository roomOptionRepository;

    @Mock
    private RoomImageRepository roomImageRepository;

    @Mock
    private RoomStockRepository roomStockRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationOwnershipRepository accommodationOwnershipRepository;

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
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
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
                .images(new ArrayList<>())
                .build();

            RoomOption roomOption = RoomOption.builder()
                .id(1L)
                .room(room)
                .airCondition(true)
                .tv(true)
                .internet(true)
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
                .images(List.of(roomImage))
                .build();

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(accommodationRepository.findById(any(Long.TYPE)))
                .willReturn(Optional.of(accommodation));
            given(accommodationOwnershipRepository
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class)
                ))
                .willReturn(true);
            given(roomRepository
                .existsRoomByNameAndAccommodation(any(String.class), any(Accommodation.class)))
                .willReturn(false);
            given(roomRepository.save(any(Room.class))).willReturn(room);
            given(roomOptionRepository.save(any(RoomOption.class))).willReturn(roomOption);
            given(roomImageRepository.saveAll(any(List.class))).willReturn(List.of(roomImage));

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
            verify(accommodationRepository, times(1)).findById(any(Long.TYPE));
            verify(accommodationOwnershipRepository, times(1))
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class));
            verify(roomRepository, times(1)).existsRoomByNameAndAccommodation(any(String.class),
                any(Accommodation.class));
            verify(roomRepository, times(1)).save(any(Room.class));
            verify(roomImageRepository, times(1)).saveAll(any(List.class));
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
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
                .images(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(accommodationRepository.findById(any(Long.TYPE)))
                .willReturn(Optional.of(accommodation));
            given(
                accommodationOwnershipRepository.existsAccommodationOwnershipByMemberAndAccommodation(
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
            verify(accommodationRepository, times(1)).findById(any(Long.TYPE));
            verify(accommodationOwnershipRepository, times(1))
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class));
            verify(roomRepository, never()).existsRoomByNameAndAccommodation(any(String.class),
                any(Accommodation.class));
            verify(roomRepository, never()).save(any(Room.class));
            verify(roomImageRepository, never()).saveAll(any(List.class));
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
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
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
                .images(new ArrayList<>())
                .build();

            RoomOption roomOption = RoomOption.builder()
                .id(1L)
                .room(room)
                .airCondition(true)
                .tv(true)
                .internet(true)
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
                .images(List.of(roomImage))
                .build();

            given(roomRepository.existsRoomByNameAndAccommodation(
                any(String.class),
                any(Accommodation.class)))
                .willReturn(false);
            given(roomRepository.save(any(Room.class))).willReturn(room);
            given(roomOptionRepository.save(any(RoomOption.class))).willReturn(roomOption);
            given(roomImageRepository.saveAll(any(List.class))).willReturn(List.of(roomImage));

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
            assertThat(result.images()).isNotNull();
            assertThat(result.option()).isNotNull();
            assertThat(result.option().airCondition()).isEqualTo(true);
            assertThat(result.option().tv()).isEqualTo(true);
            assertThat(result.option().internet()).isEqualTo(true);

            verify(roomRepository, times(1))
                .existsRoomByNameAndAccommodation(any(String.class), any(Accommodation.class));
            verify(roomRepository, times(1)).save(any(Room.class));
            verify(roomImageRepository, times(1)).saveAll(any(List.class));
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
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
                .images(new ArrayList<>())
                .rooms(new ArrayList<>())
                .build();

            given(roomRepository
                .existsRoomByNameAndAccommodation(any(String.class), any(Accommodation.class)))
                .willReturn(true);

            // when
            Throwable exception = assertThrows(DuplicateRoomNameException.class, () -> {
                roomCommandService.saveRoom(accommodation, roomRegisterRequest);
            });

            // then
            assertEquals(exception.getMessage(), "중복된 객실 이름입니다.");

            verify(roomRepository, times(1))
                .existsRoomByNameAndAccommodation(any(String.class), any(Accommodation.class));
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
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
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
                .images(List.of(roomImage1))
                .build();

            RoomOption roomOption = RoomOption.builder()
                .id(1L)
                .room(room)
                .airCondition(true)
                .tv(true)
                .internet(true)
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
                .images(List.of(roomImage2, roomImage3))
                .build();

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(roomRepository.findById(any(Long.TYPE))).willReturn(Optional.of(room));
            given(roomQueryUseCase.findRoomOptionByRoom(any(Room.class))).willReturn(roomOption);
            given(accommodationOwnershipRepository
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class)))
                .willReturn(true);
            given(roomImageRepository.saveAll(any(List.class)))
                .willReturn(List.of(roomImage2, roomImage3));
            given(roomImageRepository.findById(any(Long.TYPE)))
                .willReturn(Optional.of(roomImage1));
            doNothing().when(roomImageRepository).deleteAll(any(List.class));
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
            verify(roomRepository, times(1)).findById(any(Long.TYPE));
            verify(roomQueryUseCase, times(1)).findRoomOptionByRoom(room);
            verify(accommodationOwnershipRepository, times(1))
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class)
                );
            verify(roomImageRepository, times(1)).saveAll(any(List.class));
            verify(roomImageRepository, times(1)).findById(any(Long.TYPE));
            verify(roomImageRepository, times(1)).deleteAll(any(List.class));
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
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
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
                .images(List.of(roomImage1))
                .build();

            RoomOption roomOption = RoomOption.builder()
                .id(1L)
                .room(room)
                .airCondition(true)
                .tv(true)
                .internet(true)
                .build();

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(roomQueryUseCase.findRoomOptionByRoom(any(Room.class))).willReturn(roomOption);
            given(roomRepository.findById(any(Long.TYPE))).willReturn(Optional.of(room));
            given(accommodationOwnershipRepository
                .existsAccommodationOwnershipByMemberAndAccommodation(
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
            verify(roomRepository, times(1)).findById(any(Long.TYPE));
            verify(roomQueryUseCase, times(1)).findRoomOptionByRoom(room);
            verify(accommodationOwnershipRepository, times(1))
                .existsAccommodationOwnershipByMemberAndAccommodation(
                    any(Member.class),
                    any(Accommodation.class));
        }
    }
}
