package com.backoffice.upjuyanolja.domain.reservation.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.payment.entity.PayMethod;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.domain.reservation.controller.ReservationController;
import com.backoffice.upjuyanolja.domain.reservation.dto.request.CreateReservationRequest;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetCanceledResponse;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservedResponse;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationRoom;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.service.ReservationService;
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
import java.util.List;
import org.apache.catalina.security.SecurityConfig;
import org.hamcrest.Matchers;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@WebMvcTest(value = ReservationController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class,
        AuthenticationConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ReservationControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private SecurityUtil securityUtil;

    @MockBean
    private MemberGetService memberGetService;

    @MockBean
    private ReservationService reservationService;

    static Member mockMember;
    static Room mockRoom;

    @BeforeEach
    public void initTest() {
        mockMember = createMember(1L);
        mockRoom = createRoom();
    }

    private static Member createMember(Long id) {
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

    private static Room createRoom() {
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
                .zipCode("63082")
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

        Room room = Room.builder()
            .id(1L)
            .accommodation(accommodation)
            .name("65m² 킹룸")
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
        return room;
    }

    private static Coupon createCoupon() {
        return Coupon.builder()
            .room(mockRoom)
            .couponType(CouponType.ALL_DAYS)
            .discountType(DiscountType.FLAT)
            .couponStatus(CouponStatus.ENABLE)
            .discount(1000)
            .endDate(LocalDate.now().plusMonths(1))
            .dayLimit(10)
            .stock(5)
            .build();
    }

    private Reservation createReservation(
        LocalDate startDate,
        LocalDate endDate,
        int discount,
        boolean isCouponUsed,
        ReservationStatus status
    ) {
        ReservationRoom reservationRoom = ReservationRoom.builder()
            .id(1L)
            .room(mockRoom)
            .startDate(startDate)
            .endDate(endDate)
            .price(mockRoom.getPrice().getOffWeekDaysMinFee())
            .build();

        Payment payment = Payment.builder()
            .id(1L)
            .member(mockMember)
            .payMethod(PayMethod.KAKAO_PAY)
            .roomPrice(mockRoom.getPrice().getOffWeekDaysMinFee())
            .discountAmount(discount)
            .totalAmount(mockRoom.getPrice().getOffWeekDaysMinFee() - discount)
            .build();

        Reservation reservation = Reservation.builder()
            .id(1L)
            .member(mockMember)
            .reservationRoom(reservationRoom)
            .visitorName("홍길동")
            .visitorPhone("010-1234-5678")
            .payment(payment)
            .isCouponUsed(isCouponUsed)
            .status(status)
            .build();

        return reservation;
    }

    @Nested
    @DisplayName("예약 하기")
    class CreateReservation {

        private static CreateReservationRequest createRequest(Long couponId) {
            if (couponId == null) {
                return CreateReservationRequest.builder()
                    .roomId(1L)
                    .visitorName("홍길동")
                    .visitorPhone("010-1234-5678")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(1))
                    .totalPrice(mockRoom.getPrice().getOffWeekDaysMinFee())
                    .payMethod(PayMethod.KAKAO_PAY)
                    .build();
            }

            return CreateReservationRequest.builder()
                .roomId(1L)
                .visitorName("홍길동")
                .visitorPhone("010-1234-5678")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .couponId(couponId)
                .totalPrice(mockRoom.getPrice().getOffWeekDaysMinFee())
                .payMethod(PayMethod.KAKAO_PAY)
                .build();
        }

        @Test
        @DisplayName("쿠폰 미사용 예약 생성")
        void createReservation_couponIdNull() throws Exception {
            // given
            CreateReservationRequest request = createRequest(null);

            // when
            // then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("예약이 완료되었습니다."))
                .andDo(print());
        }

        @Test
        @DisplayName("쿠폰 사용 예약 생성")
        void createReservation_couponId() throws Exception {
            // given
            CreateReservationRequest request = createRequest(1L);

            // when
            // then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("예약이 완료되었습니다."))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("예약 취소 하기")
    class CancelReservation {

        @Test
        @DisplayName("예약 취소")
        void createReservation_couponIdNull() throws Exception {
            // given
            Long revervationId = 1L;

            // when
            // then
            mockMvc.perform(delete("/api/reservations/{reservationId}", revervationId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("성공적으로 예약을 취소했습니다."))
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("예약/예약 취소 내역 조회")
    class SearchReserved {

        @Test
        @DisplayName("예약 내역 조회")
        void getReserved() throws Exception {
            // given
            Pageable pageable = PageRequest.of(0, 3);

            List<Reservation> reservations = new ArrayList<>();
            reservations.add(createReservation(
                LocalDate.now(), LocalDate.now().plusDays(1),
                0, false, ReservationStatus.RESERVED
            ));
            reservations.add(createReservation(
                LocalDate.now(), LocalDate.now().plusDays(1),
                0, false, ReservationStatus.SERVICED
            ));

            Page<Reservation> mockPage = new PageImpl<>(reservations, pageable,
                reservations.size());
            GetReservedResponse mockResponse = new GetReservedResponse(mockPage);

            when(securityUtil.getCurrentMemberId()).thenReturn(1L);
            when(memberGetService.getMemberById(1L)).thenReturn(mockMember);
            when(reservationService.getReserved(any(Member.class), eq(pageable)))
                .thenReturn(mockResponse);

            // when
            // then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations")
                    .param("page", String.valueOf(pageable.getPageNumber()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("예약 조회에 성공하였습니다."))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.pageNum")
                        .value(pageable.getPageNumber()))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.pageSize").value(pageable.getPageSize()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPages").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isLast").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reservations").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reservations[0].id").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].isCouponUsed")
                        .isBoolean())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].roomPrice").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].totalAmount").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].accommodationId")
                        .isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].accommodationName")
                        .isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].roomId").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].roomName").isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].checkInTime").isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].checkOutTime")
                        .isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].defaultCapacity")
                        .isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].maxCapacity").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].startDate").isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].endDate").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reservations[0].status",
                    Matchers.isOneOf("RESERVED", "SERVICED")))
                .andDo(print());
        }

        @Test
        @DisplayName("예약 취소 내역 조회")
        void getCanceled() throws Exception {
            // given
            Pageable pageable = PageRequest.of(0, 3);

            List<Reservation> reservations = new ArrayList<>();
            reservations.add(createReservation(
                LocalDate.now(), LocalDate.now().plusDays(1),
                0, false, ReservationStatus.CANCELLED
            ));

            Page<Reservation> mockPage = new PageImpl<>(reservations, pageable,
                reservations.size());
            GetCanceledResponse mockResponse = new GetCanceledResponse(mockPage);

            when(securityUtil.getCurrentMemberId()).thenReturn(1L);
            when(memberGetService.getMemberById(1L)).thenReturn(mockMember);
            when(reservationService.getCanceled(any(Member.class), eq(pageable)))
                .thenReturn(mockResponse);

            // when
            // then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations")
                    .queryParam("status", "CANCELLED")
                    .param("page", String.valueOf(pageable.getPageNumber()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("예약 취소 조회에 성공하였습니다."))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.pageNum")
                        .value(pageable.getPageNumber()))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.pageSize").value(pageable.getPageSize()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPages").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isLast").isBoolean())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reservations").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reservations[0].id").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].isCouponUsed")
                        .isBoolean())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].roomPrice").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].totalAmount").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].accommodationId")
                        .isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].accommodationName")
                        .isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].roomId").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].roomName").isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].checkInTime").isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].checkOutTime")
                        .isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].defaultCapacity")
                        .isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].maxCapacity").isNumber())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].startDate").isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].endDate").isString())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.reservations[0].status")
                        .value("CANCELLED"))
                .andDo(print());
        }
    }
}
