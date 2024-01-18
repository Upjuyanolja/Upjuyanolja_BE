package com.backoffice.upjuyanolja.domain.coupon.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponRoomsRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponRoomsResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.backoffice.upjuyanolja.global.util.RestDocsSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

@ActiveProfiles("test")
class CouponBackofficeControllerDocsTest extends RestDocsSupport {

    @MockBean
    private SecurityUtil securityUtil;

    @MockBean
    private MemberGetService memberGetService;

    @MockBean
    private CouponBackofficeService couponBackofficeService;

    private final ConstraintDescriptions createCouponRequestDescriptions =
        new ConstraintDescriptions(CouponMakeRequest.class);

    Member mockMember;

    @BeforeEach
    public void initTest() {
        mockMember = createMember();
    }


    @Test
    @DisplayName("업주님의 숙소와 쿠폰을 등록할 객실 목록을 조회할 수 있다.")
    @WithMockUser(roles = "ADMIN")
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

    @DisplayName("쿠폰 만들기 테스트")
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createCoupon() throws Exception {
        // given
        when(securityUtil.getCurrentMemberId()).thenReturn(1L);
        when(memberGetService.getMemberById(1L)).thenReturn(mockMember);

        List<CouponRoomsRequest> mockRequests = List.of(
            new CouponRoomsRequest(1L, DiscountType.FLAT, 5000, 10, 10000),
            new CouponRoomsRequest(2L, DiscountType.RATE, 5, 20, 10000),
            new CouponRoomsRequest(3L, DiscountType.FLAT, 1000, 50, 10000)
        );
        CouponMakeRequest mockCouponMakeRequest = CouponMakeRequest.builder()
            .accommodationId(1L)
            .totalPoints(30000L)
            .rooms(mockRequests)
            .build();

        doNothing().when(couponBackofficeService).createCoupon(any(CouponMakeRequest.class), any(Member.class));

        // when & Then
        mockMvc.perform(post("/api/coupons/backoffice/buy")
                .content(objectMapper.writeValueAsString(mockCouponMakeRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(restDoc.document(
                requestFields(
                    fieldWithPath("accommodationId").description("숙소 식별자")
                        .attributes(key("constraints")
                            .value(createCouponRequestDescriptions.descriptionsForProperty(
                                "accommodationId"))),
                    fieldWithPath("totalPoints").description("쿠폰 구입 합계 포인트")
                        .attributes(key("constraints")
                            .value(createCouponRequestDescriptions.descriptionsForProperty(
                                "totalPoints"))),
                    fieldWithPath("rooms[]").description("객실 정보 배열").attributes(
                        key("constraints")
                            .value(createCouponRequestDescriptions.descriptionsForProperty(
                                "rooms[]"))),
                    fieldWithPath("rooms[].roomId").description("객실 식별자").attributes(
                        key("constraints").value(
                            createCouponRequestDescriptions.descriptionsForProperty(
                                "rooms[].roomId"))),
                    fieldWithPath("rooms[].discountType").description("할인 유형").attributes(
                        key("constraints").value(
                            createCouponRequestDescriptions.descriptionsForProperty(
                                "rooms[].discountType"))),
                    fieldWithPath("rooms[].discount").description("할인 금액 / 힐인율").attributes(
                        key("constraints").value(
                            createCouponRequestDescriptions.descriptionsForProperty(
                                "rooms[].discount"))),
                    fieldWithPath("rooms[].quantity").description("쿠폰 구매 수량").attributes(
                        key("constraints").value(
                            createCouponRequestDescriptions.descriptionsForProperty(
                                "rooms[].quantity"))),
                    fieldWithPath("rooms[].eachPoint").description("쿠폰 별 구입 포인트").attributes(
                        key("constraints").value(
                            createCouponRequestDescriptions.descriptionsForProperty(
                                "rooms[].eachPoint")))
                ),
                responseFields(successResponseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터"))
            ));
    }

    private CouponMakeViewResponse makeCouponViewResponse() {
        List<CouponRoomsResponse> roomResponses = new ArrayList<>();
        roomResponses.add(CouponRoomsResponse.of(1L, "스탠다드", 100000));
        roomResponses.add(CouponRoomsResponse.of(2L, "디럭스", 200000));
        roomResponses.add(CouponRoomsResponse.of(3L, "스위트", 300000));

        return new CouponMakeViewResponse(
            1L,
            "대박 호텔",
            roomResponses
        );
    }

    private Member createMember() {
        return Member.builder()
            .id(1L)
            .email("test2@tester.com")
            .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
            .name("test")
            .phone("010-1234-1234")
            .imageUrl(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
            .authority(Authority.ROLE_USER)
            .build();
    }
}
