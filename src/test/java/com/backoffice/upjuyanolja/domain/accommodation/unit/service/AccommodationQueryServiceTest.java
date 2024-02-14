package com.backoffice.upjuyanolja.domain.accommodation.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationQueryService;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
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
public class AccommodationQueryServiceTest {

    @InjectMocks
    private AccommodationQueryService accommodationQueryService;

    @Mock
    private MemberGetService memberGetService;

    @Mock
    private AccommodationOwnershipRepository accommodationOwnershipRepository;

    @Nested
    @DisplayName("getAccommodationOwnership()은")
    class Context_getAccommodationOwnership {

        @Test
        @DisplayName("보유 숙소 목록을 조회할 수 있다.")
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
                .authority(Authority.ROLE_ADMIN)
                .build();
            Category category = Category.builder()
                .id(5L)
                .name("TOURIST_HOTEL")
                .build();
            Accommodation accommodation = Accommodation.builder()
                .id(1L)
                .name("그랜드 하얏트 제주")
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .category(category)
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
                .build();
            AccommodationOwnership accommodationOwnership = AccommodationOwnership.builder()
                .id(1L)
                .accommodation(accommodation)
                .member(member)
                .build();

            given(memberGetService.getMemberById(any(Long.TYPE))).willReturn(member);
            given(accommodationOwnershipRepository.findAllByMember(any(Member.class)))
                .willReturn(List.of(accommodationOwnership));

            // when
            AccommodationOwnershipResponse result = accommodationQueryService
                .getAccommodationOwnership(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.accommodations()).isNotEmpty();
            assertThat(result.accommodations().size()).isEqualTo(1);
            assertThat(result.accommodations().get(0).id()).isEqualTo(1L);
            assertThat(result.accommodations().get(0).name()).isEqualTo("그랜드 하얏트 제주");
        }
    }
}
