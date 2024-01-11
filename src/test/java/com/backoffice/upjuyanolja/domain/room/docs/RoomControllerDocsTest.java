package com.backoffice.upjuyanolja.domain.room.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;

import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomImageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomOptionResponse;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.backoffice.upjuyanolja.global.util.RestDocsSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;

public class RoomControllerDocsTest extends RestDocsSupport {

    @MockBean
    private RoomCommandUseCase roomCommandUseCase;

    @MockBean
    private SecurityUtil securityUtil;

    private final ConstraintDescriptions roomRegisterRequestDescriptions = new ConstraintDescriptions(
        RoomRegisterRequest.class);
    private final ConstraintDescriptions roomImageRequestDescriptions = new ConstraintDescriptions(
        RoomImageRequest.class);
    private final ConstraintDescriptions roomOptionRequestDescriptions = new ConstraintDescriptions(
        RoomOptionRequest.class);

    @Test
    @DisplayName("객실을 추가 등록할 수 있다.")
    void registerRoom() throws Exception {
        // given
        RoomRegisterRequest request = RoomRegisterRequest.builder()
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
        RoomInfoResponse roomInfoResponse = RoomInfoResponse.builder()
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
            .build();

        given(securityUtil.getCurrentMemberId()).willReturn(1L);
        given(roomCommandUseCase
            .registerRoom(any(Long.TYPE), any(Long.TYPE), any(RoomRegisterRequest.class)))
            .willReturn(roomInfoResponse);

        // when then
        mockMvc.perform(post("/api/rooms/{accommodationId}", 1L)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(restDoc.document(
                requestFields(
                    fieldWithPath("name").description("객실 이름")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "name"))),
                    fieldWithPath("price").description("객실 가격")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "price"))),
                    fieldWithPath("defaultCapacity").description("객실 기본 인원")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "defaultCapacity"))),
                    fieldWithPath("maxCapacity").description("객실 최대 인원")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "maxCapacity"))),
                    fieldWithPath("checkInTime").description("객실 체크인 시간")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "checkInTime"))),
                    fieldWithPath("checkOutTime").description("객실 체크아웃 시간")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "checkOutTime"))),
                    fieldWithPath("amount").description("객실 개수")
                        .attributes(key("constraints")
                            .value(roomRegisterRequestDescriptions.descriptionsForProperty(
                                "amount"))),
                    fieldWithPath("images").description("객실 이미지 배열"),
                    fieldWithPath("images[].url").description("객실 이미지 URL")
                        .attributes(key("constraints")
                            .value(roomImageRequestDescriptions.descriptionsForProperty(
                                "url"))),
                    fieldWithPath("option").description("객실 옵션"),
                    fieldWithPath("option.airCondition").description("객실 에어컨 여부")
                        .attributes(key("constraints")
                            .value(roomOptionRequestDescriptions.descriptionsForProperty(
                                "airCondition"))),
                    fieldWithPath("option.tv").description("객실 TV 여부")
                        .attributes(key("constraints")
                            .value(roomOptionRequestDescriptions.descriptionsForProperty(
                                "tv"))),
                    fieldWithPath("option.internet").description("객실 인터넷 여부")
                        .attributes(key("constraints")
                            .value(roomOptionRequestDescriptions.descriptionsForProperty(
                                "internet")))
                ),
                responseFields(successResponseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT)
                        .description("응답 데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                        .description("객실 식별자"),
                    fieldWithPath("data.name").type(JsonFieldType.STRING)
                        .description("객실 이름"),
                    fieldWithPath("data.defaultCapacity").type(JsonFieldType.NUMBER)
                        .description("객실 기본 인원"),
                    fieldWithPath("data.maxCapacity").type(JsonFieldType.NUMBER)
                        .description("객실 최대 인원"),
                    fieldWithPath("data.checkInTime").type(JsonFieldType.STRING)
                        .description("객실 체크인 시간"),
                    fieldWithPath("data.checkOutTime").type(JsonFieldType.STRING)
                        .description("객실 체크아웃 시간"),
                    fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                        .description("객실 가격"),
                    fieldWithPath("data.amount").type(JsonFieldType.NUMBER)
                        .description("객실 개수"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING)
                        .description("객실 상태"),
                    fieldWithPath("data.images").type(JsonFieldType.ARRAY)
                        .description("객실 이미지 배열"),
                    fieldWithPath("data.images[].id").type(JsonFieldType.NUMBER)
                        .description("객실 이미지 식별자"),
                    fieldWithPath("data.images[].url").type(JsonFieldType.STRING)
                        .description("객실 이미지 URL"),
                    fieldWithPath("data.option").type(JsonFieldType.OBJECT)
                        .description("객실 옵션"),
                    fieldWithPath("data.option.airCondition").type(
                            JsonFieldType.BOOLEAN)
                        .description("에어컨 여부"),
                    fieldWithPath("data.option.tv").type(JsonFieldType.BOOLEAN)
                        .description("TV 여부"),
                    fieldWithPath("data.option.internet").type(JsonFieldType.BOOLEAN)
                        .description("인터넷 여부")
                )
            ));

        verify(roomCommandUseCase, times(1))
            .registerRoom(any(Long.TYPE), any(Long.TYPE), any(RoomRegisterRequest.class));
    }
}
