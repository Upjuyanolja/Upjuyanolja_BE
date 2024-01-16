package com.backoffice.upjuyanolja.domain.coupon.docs;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomsResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.global.util.RestDocsSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;


class CouponBackofficeControllerDocsTest extends RestDocsSupport {

    @MockBean
    private CouponBackofficeService couponBackofficeService;

    @Test
    @DisplayName("업주님의 숙소와 쿠폰을 등록할 객실 목록을 조회할 수 있다.")
    public void roomsResponseByAccommodation() throws Exception {
        // given
        Long accommodationId = 1L;
        given(couponBackofficeService.getRoomsByAccommodation(accommodationId))
            .willReturn(makeCouponViewResponse());

        // when
        ResultActions result = mockMvc.perform(
            get("/api/coupons/backoffice/buy/{accommodationId}", accommodationId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
            .andDo(restDoc.document(
                responseFields(successResponseCommon()).and(
                    subsectionWithPath("data").type(JsonFieldType.OBJECT)
                        .description("응답 데이터"),
                    subsectionWithPath("data.accommodationId").type(JsonFieldType.NUMBER)
                        .description("숙소 식별자"),
                    subsectionWithPath("data.accommodationName").type(JsonFieldType.STRING)
                        .description("숙소 이름"),
                    subsectionWithPath("data.rooms").type(JsonFieldType.ARRAY)
                        .description("보유 객실 배열"),
                    subsectionWithPath("data.rooms[].roomId").type(JsonFieldType.NUMBER)
                        .description("객실 식별자"),
                    subsectionWithPath("data.rooms[].roomName").type(JsonFieldType.STRING)
                        .description("객실 이름"),
                    subsectionWithPath("data.rooms[].roomPrice").type(JsonFieldType.NUMBER)
                        .description("객실 가격")
                )
            ));

        verify(couponBackofficeService, times(1)).getRoomsByAccommodation(accommodationId);
    }

    private CouponMakeViewResponse makeCouponViewResponse() {
        List<CouponRoomsResponse> roomResponses = new ArrayList<>();
        roomResponses.add(CouponRoomsResponse.of(1L, "스탠다드", 100000));
        roomResponses.add(CouponRoomsResponse.of(2L, "디럭스", 200000));
        roomResponses.add(CouponRoomsResponse.of(3L, "스위트", 300000));

        return new CouponMakeViewResponse(
            1L,
            "대박 호텔",
            roomResponses);
    }
}
