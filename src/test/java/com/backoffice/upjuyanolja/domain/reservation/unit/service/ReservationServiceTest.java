package com.backoffice.upjuyanolja.domain.reservation.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.payment.entity.PayMethod;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetCanceledResponse;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservedResponse;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationRoom;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.exception.NoSuchReservationRoomException;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRepository;
import com.backoffice.upjuyanolja.domain.reservation.service.ReservationService;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 단위 테스트")
class ReservationServiceTest {

  @InjectMocks
  ReservationService reservationService;

  @Mock
  ReservationRepository reservationRepository;

  static Member mockMember;

  static List<Reservation> mockReservations;

  @BeforeEach
  public void initTest() {
    mockMember = Member.builder()
        .id(1L)
        .email("test@mail.com")
        .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
        .name("test")
        .phone("010-1234-1234")
        .imageUrl(
            "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
        .authority(Authority.ROLE_USER)
        .build();

    mockReservations = createReservations();
  }

  private List<Reservation> createReservations() {
    List<Reservation> reservations = new ArrayList<>();

    reservations.add(createReservation(
        LocalDate.now(), LocalDate.now().plusDays(1),
        0, false, ReservationStatus.RESERVED
    ));

    reservations.add(createReservation(
        LocalDate.now(), LocalDate.now().plusDays(1),
        1000, true, ReservationStatus.RESERVED
    ));

    reservations.add(createReservation(
        LocalDate.now(), LocalDate.now().plusDays(1),
        0, false, ReservationStatus.SERVICED
    ));

    reservations.add(createReservation(
        LocalDate.now(), LocalDate.now().plusDays(1),
        0, false, ReservationStatus.CANCELLED
    ));

    return reservations;
  }

  private Reservation createReservation(
      LocalDate startDate,
      LocalDate endDate,
      int discount,
      boolean isCouponUsed,
      ReservationStatus status
  ) {
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
        .standard(2)
        .capacity(3)
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

    ReservationRoom reservationRoom = ReservationRoom.builder()
        .id(1L)
        .room(room)
        .startDate(startDate)
        .endDate(endDate)
        .price(room.getPrice().getOffWeekDaysMinFee())
        .build();

    Payment payment = Payment.builder()
        .id(1L)
        .member(mockMember)
        .payMethod(PayMethod.KAKAO_PAY)
        .roomPrice(room.getPrice().getOffWeekDaysMinFee())
        .discountAmount(discount)
        .totalAmount(room.getPrice().getOffWeekDaysMinFee() - discount)
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
  @DisplayName("예약 내역 조회 서비스")
  class SearchReservation {

    @Test
    @DisplayName("getReserved()를 호출 시 GetReservedResponse 반환")
    void returnGetReservedResponse_call_getReserved() {
      // given
      int pageNumber = 0;
      int pageSize = 4;
      Sort sort = Sort.by(Direction.ASC, "id");
      Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize, sort);

      Collection<ReservationStatus> statuses = Arrays.asList(ReservationStatus.RESERVED,
          ReservationStatus.SERVICED);
      when(reservationRepository.findAllByMemberAndStatusIn(
          eq(mockMember),
          eq(statuses),
          eq(pageable)
      )).thenReturn(new PageImpl<>(mockReservations, pageable, mockReservations.size()));

//      verify(reservationRepository).findAllByMemberAndStatusIn(eq(mockMember),
//          eq(statuses),
//          eq(pageable));

      // when
      // then
      assertEquals(GetReservedResponse.class,
          reservationService.getReserved(mockMember, pageable).getClass());
    }

    @Test
    @DisplayName("getCanceled()를 호출 시 GetCanceledResponse 반환")
    void returnGetCanceledResponse_call_getCanceled() {
      // given
      int pageNumber = 0;
      int pageSize = 4;
      Sort sort = Sort.by(Direction.ASC, "id");
      Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize, sort);

      Collection<ReservationStatus> statuses = List.of(ReservationStatus.CANCELLED);
      when(reservationRepository.findAllByMemberAndStatusIn(
          eq(mockMember),
          eq(statuses),
          eq(pageable)
      )).thenReturn(new PageImpl<>(mockReservations, pageable, mockReservations.size()));

      // when
      // then
      assertEquals(GetCanceledResponse.class,
          reservationService.getCanceled(mockMember, pageable).getClass());
    }

    @Test
    @DisplayName("getReserved()를 호출 시 숙소 정보를 찾을 수 없는 경우 NoSuchReservationRoomException")
    void NoSuchReservationRoomException_noRoom_getReserved() {
      // given
      int pageNumber = 0;
      int pageSize = 5;
      Sort sort = Sort.by(Direction.ASC, "id");
      Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize, sort);

      Payment payment = Payment.builder()
          .id(1L)
          .member(mockMember)
          .payMethod(PayMethod.KAKAO_PAY)
          .roomPrice(10000)
          .discountAmount(1000)
          .totalAmount(9000)
          .build();

      Reservation wrongReservation = Reservation.builder()
          .member(mockMember)
          .reservationRoom(null)
          .visitorName("홍길동")
          .visitorPhone("010-1234-5678")
          .payment(payment)
          .isCouponUsed(false)
          .status(ReservationStatus.RESERVED)
          .build();

      mockReservations.add(wrongReservation);

      Collection<ReservationStatus> statuses = Arrays.asList(ReservationStatus.RESERVED,
          ReservationStatus.SERVICED);
      when(reservationRepository.findAllByMemberAndStatusIn(
          eq(mockMember),
          eq(statuses),
          eq(pageable)
      )).thenReturn(new PageImpl<>(mockReservations, pageable, mockReservations.size()));

      // when
      // then
      Throwable exception = assertThrows(NoSuchReservationRoomException.class, () -> {
        reservationService.getReserved(mockMember, pageable);
      });
      assertEquals("예약 숙소 정보를 찾을 수 없습니다.", exception.getMessage());
    }
  }

  @Test
  @DisplayName("getCanceled()를 호출 시 숙소 정보를 찾을 수 없는 경우 NoSuchReservationRoomException")
  void NoSuchReservationRoomException_noRoom_getCanceled() {
    // given
    int pageNumber = 0;
    int pageSize = 5;
    Sort sort = Sort.by(Direction.ASC, "id");
    Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize, sort);

    Payment payment = Payment.builder()
        .id(1L)
        .member(mockMember)
        .payMethod(PayMethod.KAKAO_PAY)
        .roomPrice(10000)
        .discountAmount(1000)
        .totalAmount(9000)
        .build();

    Reservation wrongReservation = Reservation.builder()
        .member(mockMember)
        .reservationRoom(null)
        .visitorName("홍길동")
        .visitorPhone("010-1234-5678")
        .payment(payment)
        .isCouponUsed(false)
        .status(ReservationStatus.CANCELLED)
        .build();

    mockReservations.add(wrongReservation);

    Collection<ReservationStatus> statuses = Arrays.asList(ReservationStatus.CANCELLED);
    when(reservationRepository.findAllByMemberAndStatusIn(
        eq(mockMember),
        eq(statuses),
        eq(pageable)
    )).thenReturn(new PageImpl<>(mockReservations, pageable, mockReservations.size()));

    // when
    // then
    Throwable exception = assertThrows(NoSuchReservationRoomException.class, () -> {
      reservationService.getCanceled(mockMember, pageable);
    });
    assertEquals("예약 숙소 정보를 찾을 수 없습니다.", exception.getMessage());
  }
}