package com.backoffice.upjuyanolja.domain.reservation.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.payment.entity.PayMethod;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
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
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.backoffice.upjuyanolja.global.util.RestDocsSupport;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
public class ReservationControllerDocsTest extends RestDocsSupport {

  @MockBean
  private SecurityUtil securityUtil;

  @MockBean
  private MemberGetService memberGetService;

  @MockBean
  private ReservationService reservationService;

  static Member mockMember;
  static Room mockRoom;

  private final ConstraintDescriptions createReservationRequestDescriptions = new ConstraintDescriptions(
      CreateReservationRequest.class);

  @BeforeEach
  public void initTest() {
    mockMember = createMember();
    mockRoom = createRoom();
  }

  private static Member createMember() {
    return Member.builder()
        .id(1L)
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
  void createReservation() throws Exception {
    //given
    CreateReservationRequest request = createRequest(null);
    when(securityUtil.getCurrentMemberId()).thenReturn(1L);
    when(memberGetService.getMemberById(1L)).thenReturn(mockMember);
    doNothing().when(reservationService).create(any(Member.class), eq(request));

    //when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON));

    //then
    result.andExpect(status().isCreated())
        .andDo(restDoc.document(
            requestFields(
                fieldWithPath("roomId").description("객실 id")
                    .attributes(key("constraints")
                        .value(createReservationRequestDescriptions.descriptionsForProperty(
                            "roomId"))),
                fieldWithPath("visitorName").description("방문자 성함")
                    .attributes(key("constraints")
                        .value(createReservationRequestDescriptions.descriptionsForProperty(
                            "visitorName"))),
                fieldWithPath("visitorPhone").description("방문자 전화번호")
                    .attributes(key("constraints")
                        .value(createReservationRequestDescriptions.descriptionsForProperty(
                            "visitorPhone"))),
                fieldWithPath("startDate").description("입실일")
                    .attributes(key("constraints")
                        .value(createReservationRequestDescriptions.descriptionsForProperty(
                            "startDate"))),
                fieldWithPath("endDate").description("퇴실일")
                    .attributes(key("constraints")
                        .value(createReservationRequestDescriptions.descriptionsForProperty(
                            "endDate"))),
                fieldWithPath("couponId").description("쿠폰 id")
                    .attributes(key("constraints")
                        .value(createReservationRequestDescriptions.descriptionsForProperty(
                            "couponId"))),
                fieldWithPath("totalPrice").description("결제 금액")
                    .attributes(key("constraints")
                        .value(createReservationRequestDescriptions.descriptionsForProperty(
                            "totalPrice"))),
                fieldWithPath("payMethod").description("결제수단")
                    .attributes(key("constraints")
                        .value(createReservationRequestDescriptions.descriptionsForProperty(
                            "payMethod")))
            ),
            responseFields(
                fieldWithPath("message").description("응답 메세지"),
                subsectionWithPath("data").description("응답 데이터")
            )
        ));
  }

  @Test
  void cancelReservation() throws Exception {
    // given
    Long revervationId = 1L;
    when(securityUtil.getCurrentMemberId()).thenReturn(1L);
    when(memberGetService.getMemberById(1L)).thenReturn(mockMember);
    doNothing().when(reservationService).cancel(any(Member.class), any(Long.class));

    // when
    // then
    mockMvc.perform(delete("/api/reservations/{reservationId}", revervationId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("성공적으로 예약을 취소했습니다."))
        .andDo(print());

    //when
    ResultActions result = mockMvc.perform(
        delete("/api/reservations/{reservationId}", revervationId)
            .contentType(MediaType.APPLICATION_JSON));

    //then
    result.andExpect(status().isNoContent())
        .andDo(restDoc.document(
            responseFields(
                fieldWithPath("message").description("응답 메세지"),
                subsectionWithPath("data").description("응답 데이터")
            )
        ));
  }

  @Test
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

    Page<Reservation> mockPage = new PageImpl<>(reservations, pageable, 4);
    GetReservedResponse mockResponse = new GetReservedResponse(mockPage);

    when(securityUtil.getCurrentMemberId()).thenReturn(1L);
    when(memberGetService.getMemberById(1L)).thenReturn(mockMember);
    when(reservationService.getReserved(any(Member.class), eq(pageable)))
        .thenReturn(mockResponse);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations")
        .queryParam("page", String.valueOf(pageable.getPageNumber()))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    result.andExpect(status().isOk())
        .andDo(restDoc.document(
            queryParameters(
                parameterWithName("page").description("불러올 페이지 순서")),
            responseFields(
                fieldWithPath("message").description("응답 메세지"),
                subsectionWithPath("data").description("응답 데이터"),
                subsectionWithPath("data.pageNum").description("현재 페이지"),
                subsectionWithPath("data.pageSize").description("각 페이지 사이즈"),
                subsectionWithPath("data.totalPages").description("전체 페이지 수"),
                subsectionWithPath("data.totalElements").description("전체 요소 수"),
                subsectionWithPath("data.isLast").description("마지막 페이지 여부"),
                subsectionWithPath("data.reservations").description("예약 리스트"),
                subsectionWithPath("data.reservations[].id").description("예약 ID"),
                subsectionWithPath("data.reservations[].date").description("예약 완료 날짜"),
                subsectionWithPath("data.reservations[].isCouponUsed").description("쿠폰 사용 여부"),
                subsectionWithPath("data.reservations[].roomPrice").description("객실 가격"),
                subsectionWithPath("data.reservations[].totalAmount").description("결제 금액"),
                subsectionWithPath("data.reservations[].accommodationId").description("숙소 ID"),
                subsectionWithPath("data.reservations[].accommodationName").description("숙소 이름"),
                subsectionWithPath("data.reservations[].roomId").description("객실 ID"),
                subsectionWithPath("data.reservations[].roomName").description("겍실 이름"),
                subsectionWithPath("data.reservations[].checkInTime").description("Check-in 시간"),
                subsectionWithPath("data.reservations[].checkOutTime").description("Check-out 시간"),
                subsectionWithPath("data.reservations[].defaultCapacity").description("기본 인원"),
                subsectionWithPath("data.reservations[].maxCapacity").description("최대 인원"),
                subsectionWithPath("data.reservations[].startDate").description("예약 시작일"),
                subsectionWithPath("data.reservations[].endDate").description("예약 종료일"),
                subsectionWithPath("data.reservations[].status").description("예약 상태")
            )
        ));
  }

  @Test
  void getCanceled() throws Exception {
    // given
    Pageable pageable = PageRequest.of(0, 3);

    List<Reservation> reservations = new ArrayList<>();
    reservations.add(createReservation(
        LocalDate.now(), LocalDate.now().plusDays(1),
        0, false, ReservationStatus.CANCELLED
    ));

    Page<Reservation> mockPage = new PageImpl<>(reservations, pageable, 4);
    GetCanceledResponse mockResponse = new GetCanceledResponse(mockPage);

    when(securityUtil.getCurrentMemberId()).thenReturn(1L);
    when(memberGetService.getMemberById(1L)).thenReturn(mockMember);
    when(reservationService.getCanceled(any(Member.class), eq(pageable)))
        .thenReturn(mockResponse);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/cancel")
        .queryParam("page", String.valueOf(pageable.getPageNumber()))
        .contentType(MediaType.APPLICATION_JSON));

    // then
    result.andExpect(status().isOk())
        .andDo(restDoc.document(
            queryParameters(
                parameterWithName("page").description("불러올 페이지 순서")),
            responseFields(
                fieldWithPath("message").description("응답 메세지"),
                subsectionWithPath("data").description("응답 데이터"),
                subsectionWithPath("data.pageNum").description("현재 페이지"),
                subsectionWithPath("data.pageSize").description("각 페이지 사이즈"),
                subsectionWithPath("data.totalPages").description("전체 페이지 수"),
                subsectionWithPath("data.totalElements").description("전체 요소 수"),
                subsectionWithPath("data.isLast").description("마지막 페이지 여부"),
                subsectionWithPath("data.reservations").description("예약 리스트"),
                subsectionWithPath("data.reservations[].id").description("예약 ID"),
                subsectionWithPath("data.reservations[].date").description("예약 완료 날짜"),
                subsectionWithPath("data.reservations[].isCouponUsed").description("쿠폰 사용 여부"),
                subsectionWithPath("data.reservations[].roomPrice").description("객실 가격"),
                subsectionWithPath("data.reservations[].totalAmount").description("결제 금액"),
                subsectionWithPath("data.reservations[].accommodationId").description("숙소 ID"),
                subsectionWithPath("data.reservations[].accommodationName").description("숙소 이름"),
                subsectionWithPath("data.reservations[].roomId").description("객실 ID"),
                subsectionWithPath("data.reservations[].roomName").description("겍실 이름"),
                subsectionWithPath("data.reservations[].checkInTime").description("Check-in 시간"),
                subsectionWithPath("data.reservations[].checkOutTime").description("Check-out 시간"),
                subsectionWithPath("data.reservations[].defaultCapacity").description("기본 인원"),
                subsectionWithPath("data.reservations[].maxCapacity").description("최대 인원"),
                subsectionWithPath("data.reservations[].startDate").description("예약 시작일"),
                subsectionWithPath("data.reservations[].endDate").description("예약 종료일"),
                subsectionWithPath("data.reservations[].status").description("예약 상태")
            )
        ));
  }
}
