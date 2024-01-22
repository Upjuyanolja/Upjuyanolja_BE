package com.backoffice.upjuyanolja.domain.coupon.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.coupon.controller.CouponBackofficeController;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponAddInfos;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponAddRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponAddRooms;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponMakeRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.request.backoffice.CouponRoomsRequest;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponInfo;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageRooms;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponRoomsResponse;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.global.security.AuthenticationConfig;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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

            doNothing().when(couponBackofficeService).createCoupon(
                any(CouponMakeRequest.class), any(Member.class));

            // when & Then
            mockMvc.perform(post("/api/coupons/backoffice/buy")
                    .content(objectMapper.writeValueAsString(mockCouponMakeRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").isString())
                .andDo(print());
        }
    }


    @Nested
    @DisplayName("쿠폰 관리 테스트")
    class Context_couponManage {

        @DisplayName("쿠폰 관리 화면에 필요한 데이터의 응답을 테스트한다.")
        @Test
        public void couponManageViewResponse_test() throws Exception {
            // given
            when(securityUtil.getCurrentMemberId()).thenReturn(1L);
            when(memberGetService.getMemberById(1L)).thenReturn(mockMember);

            List<Long> roomIdSet = List.of(1L, 2L, 3L);
            List<String> roomNameSet = List.of("스탠다드", "디럭스", "스위트");
            List<Room> rooms = createRooms(mockAccommodation, roomIdSet, roomNameSet);

            List<Long> couponIds1 = List.of(1L, 2L, 3L, 4L);
            List<Long> couponIds2 = List.of(5L, 6L, 7L, 8L);
            List<Long> couponIds3 = List.of(9L, 10L, 11L, 12L);
            List<Coupon> coupons1 = createCoupons(couponIds1, rooms.get(0));
            List<Coupon> coupons2 = createCoupons(couponIds2, rooms.get(1));
            List<Coupon> coupons3 = createCoupons(couponIds3, rooms.get(2));

            List<Integer> priceList = List.of(100000, 100000, 10000);
            List<CouponInfo> couponInfos1 = createCouponInfos(coupons1, priceList);
            List<CouponInfo> couponInfos2 = createCouponInfos(coupons2, priceList);
            List<CouponInfo> couponInfos3 = createCouponInfos(coupons3, priceList);

            List<CouponManageRooms> manageRooms = List.of(
                createManageRoom(roomIdSet.get(0), roomNameSet.get(0), priceList.get(0), couponInfos1),
                createManageRoom(roomIdSet.get(1), roomNameSet.get(1), priceList.get(1), couponInfos2),
                createManageRoom(roomIdSet.get(2), roomNameSet.get(2), priceList.get(2), couponInfos3)
            );

            CouponManageResponse couponManageResponse = CouponManageResponse.builder()
                .accommodationId(1L)
                .accommodationName("대박 호텔")
                .expiry(coupons1.get(0).getEndDate())
                .rooms(manageRooms)
                .build();

            given(couponBackofficeService.manageCoupon(any(Long.TYPE)))
                .willReturn(couponManageResponse);

            // when & Then
            mockMvc.perform(get("/api/coupons/backoffice/manage/1"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data.accommodationId").isNumber())
                .andExpect(jsonPath("$.data.accommodationName").isString())
                .andExpect(jsonPath("$.data.expiry").exists())
                .andExpect(jsonPath("$.data.rooms").isArray())
                .andExpect(jsonPath("$.data.rooms[0].roomId").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].roomName").isString())
                .andExpect(jsonPath("$.data.rooms[0].roomPrice").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].coupons").isArray())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].couponId").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].status").isString())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].couponName").isString())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].appliedPrice").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].discountType").isString())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].discount").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].dayLimit").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].quantity").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].coupons[0].couponType").isString())
                .andDo(print());

            verify(couponBackofficeService, times(1)).manageCoupon(any(Long.TYPE));
        }


        @DisplayName("쿠폰 추가 구매 테스트")
        @Test
        public void addonCouponTest() throws Exception {
            // given
            when(securityUtil.getCurrentMemberId()).thenReturn(1L);
            when(memberGetService.getMemberById(1L)).thenReturn(mockMember);

            List<CouponAddInfos> coupons1 = List.of(
                new CouponAddInfos(1L, CouponStatus.ENABLE, DiscountType.FLAT, 50000, 20, 50, CouponType.ALL_DAYS, 1000),
                new CouponAddInfos(2L, CouponStatus.ENABLE, DiscountType.RATE, 10, 20, 50, CouponType.ALL_DAYS, 1000)
            );
            List<CouponAddInfos> coupons2 = List.of(
                new CouponAddInfos(3L, CouponStatus.ENABLE, DiscountType.FLAT, 10000, 20, 50, CouponType.ALL_DAYS, 1000),
                new CouponAddInfos(4L, CouponStatus.ENABLE, DiscountType.RATE, 20, 20, 50, CouponType.ALL_DAYS, 1000)
            );
            List<CouponAddInfos> coupons3 = List.of(
                new CouponAddInfos(5L, CouponStatus.ENABLE, DiscountType.FLAT, 30000, 20, 50, CouponType.ALL_DAYS, 1000),
                new CouponAddInfos(6L, CouponStatus.ENABLE, DiscountType.RATE, 50, 20, 50, CouponType.ALL_DAYS, 1000)
            );
            List<CouponAddRooms> rooms = List.of(
                new CouponAddRooms(1L, coupons1),
                new CouponAddRooms(2L, coupons2),
                new CouponAddRooms(3L, coupons3)
            );

            CouponAddRequest couponAddRequest = new CouponAddRequest(1L, 150000, LocalDate.of(2024,02,25), rooms);

            ResultActions resultActions = mockMvc.perform(
                patch("/api/coupons/backoffice/manage/buy")
                    .content(objectMapper.writeValueAsString(couponAddRequest))
                    .contentType(MediaType.APPLICATION_JSON))
//            String requestBody = resultActions.andReturn().getRequest().getContentAsString();

            // when & Then
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("쿠폰 추가 구매에 성공하였습니다."))
                .andDo(print());
        }

    }

    private CouponManageRooms createManageRoom(
        Long roomId,
        String roomName,
        int roomPrice,
        List<CouponInfo> couponInfos
    ) {
        return CouponManageRooms.builder()
            .roomId(roomId)
            .roomName(roomName)
            .roomPrice(roomPrice)
            .coupons(couponInfos)
            .build();
    }

    private List<CouponInfo> createCouponInfos(List<Coupon> coupons, List<Integer> priceList) {
        return List.of(
            CouponInfo.from(coupons.get(0),  priceList.get(0)),
            CouponInfo.from(coupons.get(1),  priceList.get(1)),
            CouponInfo.from(coupons.get(2),  priceList.get(2))
        );
    }
    private List<Room> createRooms(
        Accommodation accommodation, List<Long> roomIds, List<String> roomNames
    ) {
        List<Room> rooms = List.of(
            createRoom(roomIds.get(0), roomNames.get(0), accommodation),
            createRoom(roomIds.get(1), roomNames.get(1), accommodation),
            createRoom(roomIds.get(2), roomNames.get(2), accommodation)
        );
        return rooms;
    }

    private Room createRoom(
        Long roomId,
        String roomName,
        Accommodation accommodation
    ) {
        return Room.builder()
            .id(roomId)
            .accommodation(accommodation)
            .name(roomName)
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
            .option(RoomOption.builder()
                .airCondition(true)
                .tv(true)
                .internet(true)
                .build())
            .images(new ArrayList<>())
            .build();
    }

    private List<Coupon> createCoupons(List<Long> couponIds, Room room) {
        List<Coupon> coupons = List.of(
            createCoupon(
                couponIds.get(0), room, DiscountType.FLAT, CouponStatus.ENABLE, 5000, 20),
            createCoupon(
                couponIds.get(1), room, DiscountType.RATE, CouponStatus.ENABLE, 10, 20),
            createCoupon(
                couponIds.get(2), room, DiscountType.FLAT, CouponStatus.SOLD_OUT, 1000, 0),
            createCoupon(
                couponIds.get(3), room, DiscountType.RATE, CouponStatus.DELETED, 30, 0)
        );
        return coupons;
    }

    private Coupon createCoupon(
        long couponId, Room room, DiscountType discountType, CouponStatus status, int discount,
        int stock
    ) {
        return Coupon.builder()
            .id(couponId)
            .room(room)
            .couponType(CouponType.ALL_DAYS)
            .discountType(discountType)
            .couponStatus(status)
            .discount(discount)
            .endDate(LocalDate.now().plusMonths(1))
            .dayLimit(-1)
            .stock(stock)
            .build();
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
