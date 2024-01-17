package com.backoffice.upjuyanolja.domain.accommodation.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationImageRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationOptionRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationImageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationNameResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOptionResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOwnershipResponse;
import com.backoffice.upjuyanolja.domain.accommodation.service.AccommodationCommandService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomImageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomOptionResponse;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.backoffice.upjuyanolja.global.util.RestDocsSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class AccommodationControllerDocsTest extends RestDocsSupport {

    @MockBean
    private AccommodationCommandService accommodationCommandService;

    @MockBean
    private SecurityUtil securityUtil;

    private final ConstraintDescriptions accommodationRegisterDescriptions = new ConstraintDescriptions(
        AccommodationRegisterRequest.class);
    private final ConstraintDescriptions accommodationImageRequestDescriptions = new ConstraintDescriptions(
        AccommodationImageRequest.class);
    private final ConstraintDescriptions accommodationOptionRequestDescriptions = new ConstraintDescriptions(
        AccommodationOptionRequest.class);
    private final ConstraintDescriptions roomRegisterRequestDescriptions = new ConstraintDescriptions(
        RoomRegisterRequest.class);
    private final ConstraintDescriptions roomImageRequestDescriptions = new ConstraintDescriptions(
        RoomImageRequest.class);
    private final ConstraintDescriptions roomOptionRequestDescriptions = new ConstraintDescriptions(
        RoomOptionRequest.class);

    @Test
    @DisplayName("객실을 등록할 수 있다.")
    void registerAccommodation() throws Exception {
        // given
        AccommodationRegisterRequest request = AccommodationRegisterRequest.builder()
            .name("그랜드 하얏트 제주")
            .address("제주특별자치도 제주시 노형동 925")
            .detailAddress("")
            .zipCode("63082")
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
        mockMvc.perform(post("/api/accommodations")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(restDoc.document(
                requestFields(
                    fieldWithPath("name").description("숙소 이름")
                        .attributes(key("constraints")
                            .value(accommodationRegisterDescriptions.descriptionsForProperty(
                                "name"))),
                    fieldWithPath("address").description("숙소 주소")
                        .attributes(key("constraints")
                            .value(accommodationRegisterDescriptions.descriptionsForProperty(
                                "address"))),
                    fieldWithPath("detailAddress").description("숙소 상세 주소")
                        .attributes(key("constraints")
                            .value(accommodationRegisterDescriptions.descriptionsForProperty(
                                "detailAddress"))),
                    fieldWithPath("zipCode").description("숙소 우편번호")
                        .attributes(key("constraints")
                            .value(accommodationRegisterDescriptions.descriptionsForProperty(
                                "zipCode"))),
                    fieldWithPath("description").description("숙소 설명")
                        .attributes(key("constraints")
                            .value(accommodationRegisterDescriptions.descriptionsForProperty(
                                "description"))),
                    fieldWithPath("category").description("숙소 유형")
                        .attributes(key("constraints")
                            .value(accommodationRegisterDescriptions.descriptionsForProperty(
                                "category"))),
                    fieldWithPath("thumbnail").description("숙소 대표 이미지")
                        .attributes(key("constraints")
                            .value(accommodationRegisterDescriptions.descriptionsForProperty(
                                "thumbnail"))),
                    fieldWithPath("images").description("숙소 이미지"),
                    fieldWithPath("images[].url").description("숙소 이미지 URL")
                        .attributes(key("constraints")
                            .value(accommodationImageRequestDescriptions.descriptionsForProperty(
                                "url"))),
                    fieldWithPath("option").description("숙소 옵션"),
                    fieldWithPath("option.cooking").description("객실 내 취사 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "cooking"))),
                    fieldWithPath("option.parking").description("주차 시설 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "parking"))),
                    fieldWithPath("option.pickup").description("픽업 서비스 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "pickup"))),
                    fieldWithPath("option.barbecue").description("바베큐장 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "barbecue"))),
                    fieldWithPath("option.fitness").description("피트니스 센터 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "fitness"))),
                    fieldWithPath("option.karaoke").description("노래방 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "karaoke"))),
                    fieldWithPath("option.sauna").description("사우나실 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "sauna"))),
                    fieldWithPath("option.sports").description("스포츠 시설 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "sports"))),
                    fieldWithPath("option.seminar").description("세미나실 여부")
                        .attributes(key("constraints")
                            .value(accommodationOptionRequestDescriptions.descriptionsForProperty(
                                "seminar"))),
                    fieldWithPath("rooms").description("객실 배열"),
                    fieldWithPath("rooms[].name").description("객실 이름")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "name"))),
                    fieldWithPath("rooms[].price").description("객실 가격")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "price"))),
                    fieldWithPath("rooms[].defaultCapacity").description("객실 기본 인원")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "defaultCapacity"))),
                    fieldWithPath("rooms[].maxCapacity").description("객실 최대 인원")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "maxCapacity"))),
                    fieldWithPath("rooms[].checkInTime").description("객실 체크인 시간")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "checkInTime"))),
                    fieldWithPath("rooms[].checkOutTime").description("객실 체크아웃 시간")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "checkOutTime"))),
                    fieldWithPath("rooms[].amount").description("객실 개수")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "amount"))),
                    fieldWithPath("rooms[].images").description("객실 이미지 배열"),
                    fieldWithPath("rooms[].images[].url").description("객실 이미지 URL")
                        .attributes(key("constraints")
                            .value(roomImageRequestDescriptions.descriptionsForProperty(
                                "url"))),
                    fieldWithPath("rooms[].option").description("객실 옵션"),
                    fieldWithPath("rooms[].option.airCondition").description("객실 에어컨 여부")
                        .attributes(key("constraints")
                            .value(roomOptionRequestDescriptions.descriptionsForProperty(
                                "airCondition"))),
                    fieldWithPath("rooms[].option.tv").description("객실 TV 여부")
                        .attributes(key("constraints")
                            .value(roomOptionRequestDescriptions.descriptionsForProperty(
                                "tv"))),
                    fieldWithPath("rooms[].option.internet").description("객실 인터넷 여부")
                        .attributes(key("constraints")
                            .value(roomOptionRequestDescriptions.descriptionsForProperty(
                                "internet")))
                ),
                responseFields(successResponseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT)
                        .description("응답 데이터"),
                    fieldWithPath("data.accommodationId").type(JsonFieldType.NUMBER)
                        .description("숙소 식별자"),
                    fieldWithPath("data.name").type(JsonFieldType.STRING)
                        .description("숙소 이름"),
                    fieldWithPath("data.category").type(JsonFieldType.STRING)
                        .description("숙소 유형"),
                    fieldWithPath("data.address").type(JsonFieldType.STRING)
                        .description("숙소 주소"),
                    fieldWithPath("data.description").type(JsonFieldType.STRING)
                        .description("숙소 설명"),
                    fieldWithPath("data.images").type(JsonFieldType.ARRAY)
                        .description("숙소 이미지 배열"),
                    fieldWithPath("data.images[].id").type(JsonFieldType.NUMBER)
                        .description("숙소 이미지 식별자"),
                    fieldWithPath("data.images[].url").type(JsonFieldType.STRING)
                        .description("숙소 이미지 URL"),
                    fieldWithPath("data.option").type(JsonFieldType.OBJECT)
                        .description("숙소 옵션"),
                    fieldWithPath("data.option.cooking").type(JsonFieldType.BOOLEAN)
                        .description("객실 내 취사 여부"),
                    fieldWithPath("data.option.parking").type(JsonFieldType.BOOLEAN)
                        .description("주차 시설 여부"),
                    fieldWithPath("data.option.pickup").type(JsonFieldType.BOOLEAN)
                        .description("픽업 여부"),
                    fieldWithPath("data.option.barbecue").type(JsonFieldType.BOOLEAN)
                        .description("바베큐 여부"),
                    fieldWithPath("data.option.fitness").type(JsonFieldType.BOOLEAN)
                        .description("피트니스 센터 여부"),
                    fieldWithPath("data.option.karaoke").type(JsonFieldType.BOOLEAN)
                        .description("노래방 여부"),
                    fieldWithPath("data.option.sauna").type(JsonFieldType.BOOLEAN)
                        .description("사우나 여부"),
                    fieldWithPath("data.option.sports").type(JsonFieldType.BOOLEAN)
                        .description("스포츠 시설 여부"),
                    fieldWithPath("data.option.seminar").type(JsonFieldType.BOOLEAN)
                        .description("세미나실 여부"),
                    fieldWithPath("data.rooms").type(JsonFieldType.ARRAY)
                        .description("객실 배열"),
                    fieldWithPath("data.rooms[].id").type(JsonFieldType.NUMBER)
                        .description("객실 식별자"),
                    fieldWithPath("data.rooms[].name").type(JsonFieldType.STRING)
                        .description("객실 이름"),
                    fieldWithPath("data.rooms[].defaultCapacity").type(JsonFieldType.NUMBER)
                        .description("객실 기본 인원"),
                    fieldWithPath("data.rooms[].maxCapacity").type(JsonFieldType.NUMBER)
                        .description("객실 최대 인원"),
                    fieldWithPath("data.rooms[].checkInTime").type(JsonFieldType.STRING)
                        .description("객실 체크인 시간"),
                    fieldWithPath("data.rooms[].checkOutTime").type(JsonFieldType.STRING)
                        .description("객실 체크아웃 시간"),
                    fieldWithPath("data.rooms[].price").type(JsonFieldType.NUMBER)
                        .description("객실 가격"),
                    fieldWithPath("data.rooms[].amount").type(JsonFieldType.NUMBER)
                        .description("객실 개수"),
                    fieldWithPath("data.rooms[].status").type(JsonFieldType.STRING)
                        .description("객실 상태"),
                    fieldWithPath("data.rooms[].images").type(JsonFieldType.ARRAY)
                        .description("객실 이미지 배열"),
                    fieldWithPath("data.rooms[].images[].id").type(JsonFieldType.NUMBER)
                        .description("객실 이미지 식별자"),
                    fieldWithPath("data.rooms[].images[].url").type(JsonFieldType.STRING)
                        .description("객실 이미지 URL"),
                    fieldWithPath("data.rooms[].option").type(JsonFieldType.OBJECT)
                        .description("객실 옵션"),
                    fieldWithPath("data.rooms[].option.airCondition").type(JsonFieldType.BOOLEAN)
                        .description("에어컨 여부"),
                    fieldWithPath("data.rooms[].option.tv").type(JsonFieldType.BOOLEAN)
                        .description("TV 여부"),
                    fieldWithPath("data.rooms[].option.internet").type(JsonFieldType.BOOLEAN)
                        .description("인터넷 여부")
                )
            ));

        verify(accommodationCommandService, times(1))
            .createAccommodation(any(Long.TYPE), any(AccommodationRegisterRequest.class));
    }

    @Test
    @DisplayName("보유 숙소 목록을 조회할 수 있다.")
    void getAccommodationOwnership() throws Exception {
        // given
        AccommodationNameResponse accommodationNameResponse1 = AccommodationNameResponse.builder()
            .id(1L)
            .name("그랜드 하얏트 제주")
            .build();
        AccommodationNameResponse accommodationNameResponse2 = AccommodationNameResponse.builder()
            .id(2L)
            .name("신라 호텔")
            .build();
        AccommodationOwnershipResponse accommodationOwnershipResponse = AccommodationOwnershipResponse
            .builder()
            .accommodations(List.of(accommodationNameResponse1, accommodationNameResponse2))
            .build();

        given(securityUtil.getCurrentMemberId()).willReturn(1L);
        given(accommodationCommandService.getAccommodationOwnership(any(Long.TYPE)))
            .willReturn(accommodationOwnershipResponse);

        // when then
        mockMvc.perform(get("/api/accommodations/backoffice"))
            .andDo(restDoc.document(
                responseFields(successResponseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT)
                        .description("응답 데이터"),
                    fieldWithPath("data.accommodations").type(JsonFieldType.ARRAY)
                        .description("보유 숙소 배열"),
                    fieldWithPath("data.accommodations[].id").type(JsonFieldType.NUMBER)
                        .description("숙소 식별자"),
                    fieldWithPath("data.accommodations[].name").type(JsonFieldType.STRING)
                        .description("숙소 이름")
                )
            ));

        verify(accommodationCommandService, times(1)).getAccommodationOwnership(any(Long.TYPE));
    }
}
