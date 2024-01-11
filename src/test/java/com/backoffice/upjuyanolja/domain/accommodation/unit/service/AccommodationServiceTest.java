package com.backoffice.upjuyanolja.domain.accommodation.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationImageRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationOptionRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationImageRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationService;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomImageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomOptionResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.service.RoomService;
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
public class AccommodationServiceTest {

    @InjectMocks
    private AccommodationService accommodationService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationImageRepository accommodationImageRepository;

    @Mock
    private AccommodationOwnershipRepository accommodationOwnershipRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RoomService roomService;

    @Nested
    @DisplayName("createAccommodation()은")
    class Context_createAccommodation {

        @Test
        @DisplayName("객실을 등록할 수 있다.")
        void _willSuccess() {
            // given
            AccommodationRegisterRequest accommodationRegisterRequest = AccommodationRegisterRequest.builder()
                .name("그랜드 하얏트 제주")
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .category("TOURIST_HOTEL")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
                .images(List.of(AccommodationImageRequest.builder()
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .option(AccommodationOptionRequest.builder()
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
                .rooms(List.of(RoomRegisterRequest.builder()
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
                    .build()))
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
                .id(1L)
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
            AccommodationImage accommodationImage = AccommodationImage.builder()
                .id(1L)
                .accommodation(accommodation)
                .url("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
                .build();
            AccommodationOwnership accommodationOwnership = AccommodationOwnership.builder()
                .id(1L)
                .accommodation(accommodation)
                .member(member)
                .build();
            RoomInfoResponse roomInfoResponse = RoomInfoResponse.builder()
                .id(1L)
                .name("65m² 킹룸")
                .status("SELLING")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .price(100000)
                .images(List.of(RoomImageResponse.builder()
                    .id(1L)
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .option(RoomOptionResponse.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .build();
            Room room = Room.builder()
                .id(1L)
                .accommodation(accommodation)
                .name("65m² 킹룸")
                .standard(2)
                .capacity(3)
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
            Accommodation savedAccommodation = Accommodation.builder()
                .id(1L)
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
                .images(List.of(accommodationImage))
                .rooms(List.of(room))
                .build();

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(categoryRepository.findCategoryByName(any(String.class))).willReturn(Optional.of(category));
            given(accommodationRepository.save(any(Accommodation.class))).willReturn(accommodation);
            given(accommodationImageRepository.saveAll(any(Iterable.class))).willReturn(
                List.of(accommodationImage));
            given(accommodationOwnershipRepository.save(
                any(AccommodationOwnership.class))).willReturn(accommodationOwnership);
            given(roomService.saveRoom(any(Accommodation.class),
                any(RoomRegisterRequest.class))).willReturn(roomInfoResponse);
            given(accommodationRepository.findById(any(Long.TYPE)))
                .willReturn(Optional.of(savedAccommodation));

            // when
            AccommodationInfoResponse result = accommodationService.createAccommodation(1L,
                accommodationRegisterRequest);

            // then
            assertThat(result.accommodationId()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("그랜드 하얏트 제주");
            assertThat(result.category()).isEqualTo("TOURIST_HOTEL");
            assertThat(result.address()).isEqualTo("제주특별자치도 제주시 노형동 925 ");
            assertThat(result.description()).isEqualTo(
                "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.");
            assertThat(result.images()).isNotEmpty();
            assertThat(result.option()).isNotNull();
            assertThat(result.rooms()).isNotEmpty();
        }
    }
}
