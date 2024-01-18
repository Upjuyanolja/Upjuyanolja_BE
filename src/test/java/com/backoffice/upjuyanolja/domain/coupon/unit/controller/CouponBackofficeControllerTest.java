package com.backoffice.upjuyanolja.domain.coupon.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.coupon.controller.CouponBackofficeController;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponRoomsRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponRoomsResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
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

    @MockBean
    private MemberGetService memberGetService;

    Member mockMember;

    Point mockPoint;
    Accommodation mockAccommodation;

    @BeforeEach
    public void initTest() {
        mockMember = createMember(1L);
        mockAccommodation = createAccommodation(1L);
    }

    @Nested
    @DisplayName("쿠폰 만들기 테스트")
    class Context_makeCouponViewResponse {

        @Test
        @DisplayName(("숙소 id로 요청을 하면 쿠폰 만들기 화면에 필요한 데이터를 응답한다."))
        public void couponMakeViewResponse_test() throws Exception {
            // given
            when(securityUtil.getCurrentMemberId()).thenReturn(1L);
            when(memberGetService.getMemberById(1L)).thenReturn(mockMember);

            Long accommodationId = 1L;
            CouponMakeViewResponse makeViewResponse = makeCouponViewResponse();

            given(couponBackofficeService.getRoomsByAccommodation(any(Long.TYPE)))
                .willReturn(makeViewResponse);

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

            verify(couponBackofficeService, times(1)).getRoomsByAccommodation(any(Long.TYPE));
        }

        @DisplayName("쿠폰 만들기")
        @Test
        public void couponMakeRequest_test() throws Exception {
            // given
            when(securityUtil.getCurrentMemberId()).thenReturn(1L);
            when(memberGetService.getMemberById(1L)).thenReturn(mockMember);

            List<CouponRoomsRequest> mockCouponRoomsRequests = createCouponRooms();
            CouponMakeRequest mockCouponMakeRequest = createCouponMakeRequest(
                mockCouponRoomsRequests, 1L);
            mockPoint = createPoint(1L, mockMember, 50000);

            doNothing().when(couponBackofficeService).createCoupon(any(CouponMakeRequest.class), any(Member.class));

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
            .build();
    }

    private Accommodation createAccommodation(Long accommodationId) {
        return Accommodation.builder()
            .id(accommodationId)
            .name("그랜드 하얏트 제주")
            .address(Address.builder()
                .address("제주특별자치도 제주시 노형동 925")
                .detailAddress("")
                .zipCode("63082")
                .build())
            .category(createCategory())
            .description(
                "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
            .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
            .option(AccommodationOption.builder()
                .cooking(false).parking(true).pickup(false).barbecue(false).fitness(true)
                .karaoke(false).sauna(false).sports(true).seminar(true)
                .build())
            .images(new ArrayList<>())
            .rooms(new ArrayList<>())
            .build();
    }

    private Category createCategory() {
        return Category.builder()
            .id(5L)
            .name("TOURIST_HOTEL")
            .build();
    }

}
