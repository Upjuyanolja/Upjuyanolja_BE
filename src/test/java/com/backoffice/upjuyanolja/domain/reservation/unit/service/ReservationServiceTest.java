package com.backoffice.upjuyanolja.domain.reservation.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponRedeem;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRedeemRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.payment.entity.PayMethod;
import com.backoffice.upjuyanolja.domain.payment.entity.Payment;
import com.backoffice.upjuyanolja.domain.payment.repository.PaymentRepository;
import com.backoffice.upjuyanolja.domain.reservation.dto.request.CreateReservationRequest;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetCanceledResponse;
import com.backoffice.upjuyanolja.domain.reservation.dto.response.GetReservedResponse;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationRoom;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.exception.InvalidCouponException;
import com.backoffice.upjuyanolja.domain.reservation.exception.InvalidReservationInfoException;
import com.backoffice.upjuyanolja.domain.reservation.exception.NoSuchReservationException;
import com.backoffice.upjuyanolja.domain.reservation.exception.NoSuchReservationRoomException;
import com.backoffice.upjuyanolja.domain.reservation.exception.PaymentFailureException;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRepository;
import com.backoffice.upjuyanolja.domain.reservation.service.ReservationService;
import com.backoffice.upjuyanolja.domain.reservation.service.ReservationStockService;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.service.RoomQueryService;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
    ReservationStockService stockService;

    @Mock
    RoomCommandUseCase roomCommandUseCase;

    @Mock
    RoomQueryService roomQueryService;

    @Mock
    RoomRepository roomRepository;

    @Mock
    CouponRepository couponRepository;

    @Mock
    CouponRedeemRepository couponRedeemRepository;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    PaymentRepository paymentRepository;

    static Member mockMember;
    static Accommodation mockAccommodation;
    static Room mockRoom;

    static RoomPrice mockRoomPrice;

    static Coupon mockCoupon;

    static List<Reservation> mockReservations;
    static List<Payment> mockPayments = new ArrayList<>();

    @BeforeEach
    public void initTest() {
        mockMember = createMember(1L);
        mockAccommodation = createAccommodation();
        mockRoom = createRoom(1L, RoomStatus.SELLING);
        mockRoomPrice = createRoomPrice(1L, mockRoom);
        mockCoupon = createCoupon(1L, mockRoom, CouponStatus.ENABLE, 1);
        mockReservations = createReservations(mockRoomPrice);
        for (Reservation reservation : mockReservations) {
            mockPayments.add(createPayment(reservation, mockRoomPrice));
        }
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

    private static Accommodation createAccommodation() {
        Category category = Category.builder()
            .id(5L)
            .name("TOURIST_HOTEL")
            .build();

        Accommodation accommodation = Accommodation.builder()
            .id(1L)
            .name("그랜드 하얏트 제주")
            .address("제주특별자치도 제주시 노형동 925")
            .detailAddress("")
            .zipCode("63082")
            .category(category)
            .description(
                "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
            .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
            .build();
        return accommodation;
    }

    private static Room createRoom(Long id, RoomStatus status) {
        Room room = Room.builder()
            .id(id)
            .accommodation(mockAccommodation)
            .name("65m² 킹룸")
            .defaultCapacity(2)
            .maxCapacity(3)
            .checkInTime(LocalTime.of(15, 0, 0))
            .checkOutTime(LocalTime.of(11, 0, 0))
            .amount(858)
            .status(status)
            .build();
        return room;
    }

    private RoomPrice createRoomPrice(long id, Room room) {
        RoomPrice roomPrice = RoomPrice.builder()
            .id(id)
            .room(room)
            .offWeekDaysMinFee(100000)
            .offWeekendMinFee(100000)
            .peakWeekDaysMinFee(100000)
            .peakWeekendMinFee(100000)
            .build();

        return roomPrice;
    }

    private Reservation createReservation(
        LocalDate startDate,
        LocalDate endDate,
        RoomPrice roomPrice,
        boolean isCouponUsed,
        ReservationStatus status
    ) {
        ReservationRoom reservationRoom = ReservationRoom.builder()
            .id(1L)
            .room(mockRoom)
            .startDate(startDate)
            .endDate(endDate)
            .price(roomPrice.getOffWeekDaysMinFee())
            .build();

        Reservation reservation = Reservation.builder()
            .id(1L)
            .member(mockMember)
            .reservationRoom(reservationRoom)
            .visitorName("홍길동")
            .visitorPhone("010-1234-5678")
            .isCouponUsed(isCouponUsed)
            .status(status)
            .build();

        return reservation;
    }

    private List<Reservation> createReservations(RoomPrice roomPrice) {
        List<Reservation> reservations = new ArrayList<>();

        reservations.add(createReservation(
            LocalDate.now(), LocalDate.now().plusDays(1), roomPrice,
            false, ReservationStatus.RESERVED
        ));

        reservations.add(createReservation(
            LocalDate.now(), LocalDate.now().plusDays(1), roomPrice,
            true, ReservationStatus.RESERVED
        ));

        reservations.add(createReservation(
            LocalDate.now(), LocalDate.now().plusDays(1), roomPrice,
            false, ReservationStatus.SERVICED
        ));

        reservations.add(createReservation(
            LocalDate.now(), LocalDate.now().plusDays(1), roomPrice,
            false, ReservationStatus.CANCELLED
        ));

        return reservations;
    }

    private Payment createPayment(Reservation reservation, RoomPrice roomPrice) {
        return Payment.builder()
            .member(mockMember)
            .reservation(reservation)
            .payMethod(PayMethod.KAKAO_PAY)
            .roomPrice(roomPrice.getOffWeekDaysMinFee())
            .discountAmount(0)
            .totalAmount(roomPrice.getOffWeekDaysMinFee())
            .build();
    }


    private RoomStock createRoomStock(Room room, int count) {
        return RoomStock.builder()
            .id(1L)
            .room(room)
            .count(count)
            .date(LocalDate.now())
            .build();
    }

    private static Coupon createCoupon(Long id, Room room, CouponStatus status, int stock) {
        return Coupon.builder()
            .id(id)
            .room(room)
            .couponType(CouponType.ALL_DAYS)
            .discountType(DiscountType.FLAT)
            .couponStatus(status)
            .discount(10000)
            .endDate(LocalDate.now().plusMonths(1))
            .dayLimit(10)
            .stock(stock)
            .build();
    }

    @Nested
    @DisplayName("예약 생성 서비스")
    class CrateReservation {

        /*
         * [정상 객실] ID: 1L, RoomStatus.SELLING
         * [정상 쿠폰] ID: 1L, CouponStatus.ENABLE
         * 객실 가격: 100000
         * 객실+쿠폰 결제 금액: 90000
         * */
        static final Long defaultRoomId = 1L;
        static final Long defaultCouponId = 1L;
        static int defaultTotalPrice = 9000;

        private CreateReservationRequest createRequest(Long roomId, Long couponId, int totalPrice) {
            return CreateReservationRequest.builder()
                .roomId(roomId)
                .visitorName("홍길동")
                .visitorPhone("010-1234-5678")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .couponId(couponId)
                .totalPrice(totalPrice)
                .payMethod(PayMethod.KAKAO_PAY)
                .build();
        }

        @Test
        @DisplayName("RoomId가 유효한 경우 InvalidReservationInfoException")
        void RoomNotFoundException_invalidRoomId() {
            // given
            CreateReservationRequest request = createRequest(5L, defaultCouponId,
                defaultTotalPrice);

            when(roomRepository.findById(eq(request.getRoomId()))).thenThrow(
                new InvalidReservationInfoException());

            // when
            // then
            Throwable exception = assertThrows(InvalidReservationInfoException.class, () -> {
                reservationService.create(mockMember, request);
            });

            assertEquals("예약 정보가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("RoomStatus가 판매중이 아닌경우 InvalidReservationInfoException")
        void InvalidReservationInfoException_invalidRoomStatus() {
            // given
            Long stopSellingRoomID = 2L;
            Room stopSellingRoom = createRoom(stopSellingRoomID, RoomStatus.STOP_SELLING);
            CreateReservationRequest request = createRequest(stopSellingRoomID, defaultCouponId,
                defaultTotalPrice);

            when(roomRepository.findById(eq(request.getRoomId()))).thenReturn(
                Optional.ofNullable(stopSellingRoom));

            // when
            // then
            Throwable exception = assertThrows(InvalidReservationInfoException.class, () -> {
                reservationService.create(mockMember, request);
            });

            assertEquals("예약 정보가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("객실 재고가 없는 경우 InvalidReservationInfoException")
        void InvalidReservationInfoException_noRoomStock() {
            // given
            RoomStock roomStock = createRoomStock(mockRoom, 0);
            List<RoomStock> roomStockList = new ArrayList<>(Arrays.asList(roomStock));
            CreateReservationRequest request = createRequest(defaultRoomId, defaultCouponId,
                defaultTotalPrice);

            when(roomRepository.findById(any(Long.class))).thenReturn(
                Optional.ofNullable(mockRoom));
            when(roomQueryService.getFilteredRoomStocksByDate(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(roomStockList);

            // when
            // then
            Throwable exception = assertThrows(InvalidReservationInfoException.class, () -> {
                reservationService.create(mockMember, request);
            });

            assertEquals("예약 정보가 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("RoomId와 CouponId가 연관 되지 않은 경우 InvalidCouponException")
        void InvalidCouponException_invalidCouponId() {
            // given
            Long notMatchedRoomID = 2L;
            Room notMatchedRoom = createRoom(notMatchedRoomID, RoomStatus.SELLING);
            RoomStock roomStock = createRoomStock(notMatchedRoom, 1);
            List<RoomStock> roomStockList = new ArrayList<>(Arrays.asList(roomStock));

            CreateReservationRequest request = createRequest(notMatchedRoomID, defaultCouponId,
                defaultTotalPrice);

            when(roomRepository.findById(any(Long.class))).thenReturn(
                Optional.ofNullable(notMatchedRoom));
            when(roomQueryService.getFilteredRoomStocksByDate(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(roomStockList);
            when(couponRepository.findByIdAndRoom(eq(request.getCouponId()),
                eq(notMatchedRoom))).thenThrow(
                new InvalidCouponException());

            // when
            // then
            Throwable exception = assertThrows(InvalidCouponException.class, () -> {
                reservationService.create(mockMember, request);
            });

            assertEquals("쿠폰이 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("CouponStatus가 발급 중이 아닌 경우 InvalidCouponException")
        void InvalidCouponException_invalidCouponStatus() {
            // given
            Long disabledCouponId = 2L;
            Coupon disabledCoupon = createCoupon(disabledCouponId, mockRoom, CouponStatus.DISABLE,
                1);
            RoomStock roomStock = createRoomStock(mockRoom, 1);
            List<RoomStock> roomStockList = new ArrayList<>(Arrays.asList(roomStock));

            CreateReservationRequest request = createRequest(defaultRoomId, disabledCouponId,
                defaultTotalPrice);

            when(roomRepository.findById(any(Long.class))).thenReturn(
                Optional.ofNullable(mockRoom));
            when(roomQueryService.getFilteredRoomStocksByDate(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(roomStockList);
            when(couponRepository.findByIdAndRoom(eq(request.getCouponId()),
                eq(mockRoom))).thenReturn(Optional.ofNullable(disabledCoupon));

            // when
            // then
            Throwable exception = assertThrows(InvalidCouponException.class, () -> {
                reservationService.create(mockMember, request);
            });

            assertEquals("쿠폰이 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("쿠폰 재고가 없는 경우 InvalidCouponException")
        void InvalidCouponException_noCouponStock() {
            Long noStockCouponId = 2L;
            Coupon noStockCoupon = createCoupon(noStockCouponId, mockRoom, CouponStatus.ENABLE, 0);
            RoomStock roomStock = createRoomStock(mockRoom, 1);
            List<RoomStock> roomStockList = new ArrayList<>(Arrays.asList(roomStock));

            CreateReservationRequest request = createRequest(defaultRoomId, noStockCouponId,
                defaultTotalPrice);

            when(roomRepository.findById(any(Long.class))).thenReturn(
                Optional.ofNullable(mockRoom));
            when(roomQueryService.getFilteredRoomStocksByDate(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(roomStockList);
            when(couponRepository.findByIdAndRoom(eq(request.getCouponId()),
                eq(mockRoom))).thenReturn(Optional.ofNullable(noStockCoupon));

            // when
            // then
            Throwable exception = assertThrows(InvalidCouponException.class, () -> {
                reservationService.create(mockMember, request);
            });

            assertEquals("쿠폰이 유효하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("입력 받은 결제 금액과 계산된 결제 금액이 다른 경우 PaymentFailureException")
        void PaymentFailureException_notMatchTotalPrice() {
            int invalidTotalPrice = 5000;
            RoomStock roomStock = createRoomStock(mockRoom, 1);
            List<RoomStock> roomStockList = new ArrayList<>(Arrays.asList(roomStock));

            CreateReservationRequest request = createRequest(defaultRoomId, defaultCouponId,
                invalidTotalPrice);

            ReservationRoom reservationRoom = ReservationRoom.builder()
                .id(1L)
                .room(mockRoom)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .price(mockRoomPrice.getOffWeekDaysMinFee())
                .build();

            when(roomRepository.findById(any(Long.class))).thenReturn(
                Optional.ofNullable(mockRoom));
            when(roomQueryService.getFilteredRoomStocksByDate(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(roomStockList);
            when(roomQueryService.findRoomPriceByRoom(mockRoom)).thenReturn(mockRoomPrice);
            when(couponRepository.findByIdAndRoom(any(Long.class),
                any(Room.class))).thenReturn(Optional.ofNullable(mockCoupon));

            // when
            // then
            Throwable exception = assertThrows(PaymentFailureException.class, () -> {
                reservationService.create(mockMember, request);
            });

            assertEquals("결제에 실패 했습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("예약 취소 서비스")
    class CanCelReservation {

        static final Long defaultReservationId = 1L;

        @Test
        @DisplayName("reservationId가 유효한 경우 NoSuchReservationException")
        void NoSuchReservationException_invalidReservationId() {
            // given
            Long invalidReservationId = 10L;

            when(reservationRepository.findByIdAndMember(eq(invalidReservationId),
                any(Member.class))).thenThrow(
                new NoSuchReservationException());

            // when
            // then
            Throwable exception = assertThrows(NoSuchReservationException.class, () -> {
                reservationService.cancel(mockMember, invalidReservationId);
            });

            assertEquals("예약 정보를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("reservationId가 로그인한 회원의 예약이 아닌 경우 NoSuchReservationException")
        void NoSuchReservationException_notMachReservationAndMember() {
            // given
            Long invalidReservationId = 10L;

            when(reservationRepository.findByIdAndMember(eq(invalidReservationId),
                any(Member.class))).thenThrow(
                new NoSuchReservationException());

            // when
            // then
            Throwable exception = assertThrows(NoSuchReservationException.class, () -> {
                reservationService.cancel(mockMember, invalidReservationId);
            });

            assertEquals("예약 정보를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("쿠폰 사용 정보가 없는 경우 NoSuchReservationException")
        void NoSuchReservationException_noSuchCouponRedeem() {
            // given
            RoomStock roomStock = createRoomStock(mockRoom, 1);
            List<RoomStock> roomStockList = new ArrayList<>(Arrays.asList(roomStock));
            Reservation reservation = createReservation(
                LocalDate.now(), LocalDate.now(), mockRoomPrice,
                true, ReservationStatus.RESERVED
            );

            when(reservationRepository.findByIdAndMember(any(Long.class), any(Member.class)))
                .thenReturn(Optional.ofNullable(reservation));
            when(couponRedeemRepository.findByReservation(eq(reservation))).thenThrow(
                new NoSuchReservationException());

            // when
            // then
            Throwable exception = assertThrows(NoSuchReservationException.class, () -> {
                reservationService.cancel(mockMember, defaultReservationId);
            });
            assertEquals("예약 정보를 찾을 수 없습니다.", exception.getMessage());

        }

        @Test
        @DisplayName("객실 재고 정보가 없는 경우 NoSuchReservationRoomException")
        void NoSuchReservationRoomException_noSuchRoomStock() {
            // given
            Coupon coupon = createCoupon(1L, mockRoom, CouponStatus.ENABLE, 1);

            Reservation reservation = createReservation(
                LocalDate.now(), LocalDate.now(), mockRoomPrice,
                true, ReservationStatus.RESERVED
            );

            CouponRedeem couponRedeem = CouponRedeem.builder()
                .reservation(reservation)
                .coupon(coupon)
                .build();

            when(reservationRepository.findByIdAndMember(any(Long.class),
                any(Member.class))).thenReturn(
                Optional.ofNullable(reservation));
            when(couponRedeemRepository.findByReservation(any(Reservation.class)))
                .thenReturn(Optional.ofNullable(couponRedeem));
            doNothing().when(couponRedeemRepository).delete(any(CouponRedeem.class));
            when(roomQueryService.getFilteredRoomStocksByDate(
                any(Room.class), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(new ArrayList<>());

            // when
            // then
            Throwable exception = assertThrows(NoSuchReservationRoomException.class, () -> {
                reservationService.cancel(mockMember, defaultReservationId);
            });
            assertEquals("예약 숙소 정보를 찾을 수 없습니다.", exception.getMessage());

        }
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
            for (int i = 0; i < mockReservations.size(); i++) {
                when(paymentRepository.findByReservation(
                    eq(mockReservations.get(i))
                )).thenReturn(Optional.ofNullable(mockPayments.get(i)));
            }

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
            for (int i = 0; i < mockReservations.size(); i++) {
                when(paymentRepository.findByReservation(
                    eq(mockReservations.get(i))
                )).thenReturn(Optional.ofNullable(mockPayments.get(i)));
            }

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

            Reservation wrongReservation = Reservation.builder()
                .member(mockMember)
                .reservationRoom(null)
                .visitorName("홍길동")
                .visitorPhone("010-1234-5678")
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
            for (int i = 0; i < mockReservations.size(); i++) {
                when(paymentRepository.findByReservation(
                    eq(mockReservations.get(i))
                )).thenReturn(Optional.ofNullable(mockPayments.get(i)));
            }

            // when
            // then
            Throwable exception = assertThrows(NoSuchReservationRoomException.class, () -> {
                reservationService.getReserved(mockMember, pageable);
            });
            assertEquals("예약 숙소 정보를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("getCanceled()를 호출 시 숙소 정보를 찾을 수 없는 경우 NoSuchReservationRoomException")
        void NoSuchReservationRoomException_noRoom_getCanceled() {
            // given
            int pageNumber = 0;
            int pageSize = 5;
            Sort sort = Sort.by(Direction.ASC, "id");
            Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize, sort);

            Reservation wrongReservation = Reservation.builder()
                .member(mockMember)
                .reservationRoom(null)
                .visitorName("홍길동")
                .visitorPhone("010-1234-5678")
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
            for (int i = 0; i < mockReservations.size(); i++) {
                when(paymentRepository.findByReservation(
                    eq(mockReservations.get(i))
                )).thenReturn(Optional.ofNullable(mockPayments.get(i)));
            }

            // when
            // then
            Throwable exception = assertThrows(NoSuchReservationRoomException.class, () -> {
                reservationService.getCanceled(mockMember, pageable);
            });
            assertEquals("예약 숙소 정보를 찾을 수 없습니다.", exception.getMessage());
        }
    }
}
