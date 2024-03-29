package com.backoffice.upjuyanolja.domain.accommodation.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.accommodation.controller.AccommodationBackofficeController;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationImageRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationOptionRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationImageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationNameResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOptionResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationCommandService;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationQueryService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomImageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomOptionResponse;
import com.backoffice.upjuyanolja.global.security.AuthenticationConfig;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(value = AccommodationBackofficeController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class,
        AuthenticationConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AccommodationBackofficeControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private AccommodationCommandService accommodationCommandService;

    @MockBean
    private AccommodationQueryService accommodationQueryService;

    @MockBean
    private SecurityUtil securityUtil;

    @Nested
    @DisplayName("registerAccommodation()은")
    class Context_registerAccommodation {

        @Test
        @DisplayName("숙소를 등록할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            AccommodationRegisterRequest request = AccommodationRegisterRequest.builder()
                .name("그랜드 하얏트 제주")
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .zipCode("63082")
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .zipCode("63082")
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
            AccommodationInfoResponse accommodationInfoResponse = AccommodationInfoResponse.builder()
                .accommodationId(1L)
                .name("그랜드 하얏트 제주")
                .category("TOURIST_HOTEL")
                .address("제주특별자치도 제주시 노형동 925 ")
                .description(
                    "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
                .images(List.of(AccommodationImageResponse.builder()
                    .id(1L)
                    .url("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
                    .build()))
                .option(AccommodationOptionResponse.builder()
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
                .rooms(List.of(RoomInfoResponse.builder()
                    .id(1L)
                    .name("65m² 킹룸")
                    .defaultCapacity(2)
                    .maxCapacity(3)
                    .checkInTime("15:00")
                    .checkOutTime("11:00")
                    .price(100000)
                    .amount(858)
                    .status("SELLING")
                    .option(RoomOptionResponse.builder()
                        .airCondition(true)
                        .tv(true)
                        .internet(true)
                        .build())
                    .images(List.of(RoomImageResponse.builder()
                        .id(1L)
                        .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                        .build()))
                    .build()))
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(accommodationCommandService
                .createAccommodation(any(Long.TYPE), any(AccommodationRegisterRequest.class)))
                .willReturn(accommodationInfoResponse);

            // when then
            mockMvc.perform(post("/backoffice-api/accommodations")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accommodationId").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.category").isString())
                .andExpect(jsonPath("$.address").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.images[0].id").isNumber())
                .andExpect(jsonPath("$.images[0].url").isString())
                .andExpect(jsonPath("$.option").isMap())
                .andExpect(jsonPath("$.option.cooking").isBoolean())
                .andExpect(jsonPath("$.option.parking").isBoolean())
                .andExpect(jsonPath("$.option.pickup").isBoolean())
                .andExpect(jsonPath("$.option.barbecue").isBoolean())
                .andExpect(jsonPath("$.option.fitness").isBoolean())
                .andExpect(jsonPath("$.option.karaoke").isBoolean())
                .andExpect(jsonPath("$.option.sauna").isBoolean())
                .andExpect(jsonPath("$.option.sports").isBoolean())
                .andExpect(jsonPath("$.option.seminar").isBoolean())
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.rooms[0].id").isNumber())
                .andExpect(jsonPath("$.rooms[0].name").isString())
                .andExpect(jsonPath("$.rooms[0].defaultCapacity").isNumber())
                .andExpect(jsonPath("$.rooms[0].maxCapacity").isNumber())
                .andExpect(jsonPath("$.rooms[0].checkInTime").isString())
                .andExpect(jsonPath("$.rooms[0].checkOutTime").isString())
                .andExpect(jsonPath("$.rooms[0].price").isNumber())
                .andExpect(jsonPath("$.rooms[0].amount").isNumber())
                .andExpect(jsonPath("$.rooms[0].status").isString())
                .andExpect(jsonPath("$.rooms[0].images").isArray())
                .andExpect(jsonPath("$.rooms[0].images[0].id").isNumber())
                .andExpect(jsonPath("$.rooms[0].images[0].url").isString())
                .andExpect(jsonPath("$.rooms[0].option").isMap())
                .andExpect(jsonPath("$.rooms[0].option.airCondition").isBoolean())
                .andExpect(jsonPath("$.rooms[0].option.tv").isBoolean())
                .andExpect(jsonPath("$.rooms[0].option.internet").isBoolean())
                .andDo(print());

            verify(accommodationCommandService, times(1))
                .createAccommodation(any(Long.TYPE), any(AccommodationRegisterRequest.class));
        }
    }

    @Nested
    @DisplayName("getAccommodationOwnership()은")
    class Context_getAccommodationOwnership {

        @Test
        @DisplayName("보유 숙소 목록을 조회할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            AccommodationNameResponse accommodationNameResponse = AccommodationNameResponse.builder()
                .id(1L)
                .name("그랜드 하얏트 제주")
                .build();
            AccommodationOwnershipResponse accommodationOwnershipResponse = AccommodationOwnershipResponse
                .builder()
                .accommodations(List.of(accommodationNameResponse))
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(accommodationQueryService.getAccommodationOwnership(any(Long.TYPE)))
                .willReturn(accommodationOwnershipResponse);

            // when then
            mockMvc.perform(get("/backoffice-api/accommodations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accommodations").isArray())
                .andExpect(jsonPath("$.accommodations[0].id").isNumber())
                .andExpect(jsonPath("$.accommodations[0].name").isString())
                .andDo(print());

            verify(accommodationQueryService, times(1)).getAccommodationOwnership(any(Long.TYPE));
        }
    }
}
