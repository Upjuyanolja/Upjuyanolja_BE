package com.backoffice.upjuyanolja.domain.coupon.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.coupon.controller.CouponBackofficeController;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponRoomsRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponRoomsResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointType;
import com.backoffice.upjuyanolja.global.security.AuthenticationConfig;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(value = CouponBackofficeController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class,
        AuthenticationConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CouponBackofficeControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private CouponBackofficeService couponBackofficeService;

    @MockBean
    private SecurityUtil securityUtil;

    Member mockMember;

    Point mockPoint;

    @BeforeEach
    public void initTest() {
        mockMember = createMember(1L);
    }

    @Nested
    @DisplayName("쿠폰 만들기 테스트")
    class Context_makeCouponViewResponse {

        @Test
        @DisplayName(("숙소 id로 요청을 하면 쿠폰 만들기 화면에 필요한 데이터를 응답한다."))
        public void couponMakeViewResponse_test() throws Exception {
            // given
            Long accommodationId = 1L;
            given(couponBackofficeService.getRoomsByAccommodation(accommodationId))
                .willReturn(makeCouponViewResponse());

            // when & then
            mockMvc.perform(get("/api/coupons/backoffice/buy/{accommodationId}", accommodationId))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data.accommodationId").isNumber())
                .andExpect(jsonPath("$.data.accommodationName").isString())
                .andExpect(jsonPath("$.data.rooms").isArray())
                .andExpect(jsonPath("$.data.rooms[0].roomId").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].roomName").isString())
                .andExpect(jsonPath("$.data.rooms[0].roomPrice").isNumber())
                .andDo(print());
        }

        @DisplayName("쿠폰 만들기")
        @Test
        public void couponMakeRequest_test() throws Exception {
            // given
            List<CouponRoomsRequest> mockCouponRoomsRequests = createCouponRooms();
            CouponMakeRequest mockCouponMakeRequest = createCouponMakeRequest(
                mockCouponRoomsRequests, 1L);
            Long mockMemberId = mockMember.getId();
            mockPoint = createPoint(1L, mockMember, 50000);

            given(couponBackofficeService.createCoupon(any(CouponMakeRequest.class), any(Member.class)))
                .willReturn(new Object());

            // when & Then
            mockMvc.perform(post("/api/coupons/backoffice/buy")
                    .content(objectMapper.writeValueAsString(mockCouponMakeRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").isString())
                .andDo(print());
        }
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

    private CouponMakeRequest createCouponMakeRequest(
        List<CouponRoomsRequest> roomsRequests, Long accommodationId
    ) {
        return CouponMakeRequest.builder()
            .accommodationId(accommodationId)
            .totalPoints(30000)
            .rooms(roomsRequests)
            .build();
    }

    private List<CouponRoomsRequest> createCouponRooms() {
        return Arrays.asList(
            new CouponRoomsRequest(1L, DiscountType.FLAT, 1000, 10, 10000),
            new CouponRoomsRequest(1L, DiscountType.RATE, 10, 10, 10000),
            new CouponRoomsRequest(1L, DiscountType.RATE, 5000, 10, 10000)
        );
    }

    private Member createMember(Long id) {
        return Member.builder()
            .id(id)
            .email("test@mail.com")
            .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
            .name("test")
            .phone("010-1234-1234")
            .imageUrl(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
            .authority(Authority.ROLE_USER)
            .build();
    }

    private Point createPoint(Long pointId, Member member, int balance) {
        return Point.builder()
            .id(pointId)
            .member(member)
            .pointBalance(balance)
            .pointType(PointType.USE)
            .build();
    }
}
