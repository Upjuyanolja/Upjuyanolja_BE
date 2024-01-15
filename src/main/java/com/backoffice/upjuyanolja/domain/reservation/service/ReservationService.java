package com.backoffice.upjuyanolja.domain.reservation.service;

import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponRedeem;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRedeemRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
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
import com.backoffice.upjuyanolja.domain.reservation.exception.PaymentFailureException;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRepository;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRoomRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final RoomRepository roomRepository;
  private final RoomStockRepository roomStockRepository;
  private final CouponRepository couponRepository;

  private final CouponRedeemRepository couponRedeemRepository;
  private final PaymentRepository paymentRepository;
  private final ReservationRoomRepository reservationRoomRepository;
  private final ReservationRepository reservationRepository;

  private final ReservationStockService stockService;

  @Transactional
  public void create(Member currentMember, CreateReservationRequest request) {
    int discountAmount = 0;
    Coupon coupon = null;

    /*
     * 객실 재고 수정 및 예약 객실 생성
     * */
    Room room = roomRepository.findById(request.getRoomId())
        .orElseThrow(RoomNotFoundException::new);

    decreaseRoomStock(room, request.getStartDate(), request.getEndDate());

    /*
     * 쿠폰 재고 수정
     * request.getCouponId() = null 인 경우 스킵
     * */
    if (request.getCouponId() != null) {
      // room - coupon 유효성 검사
      coupon = couponRepository.findByIdAndRoom(request.getCouponId(), room)
          .orElseThrow(InvalidCouponException::new);

      // 쿠폰 재고 수정
      decreaseCouponStock(coupon);

      // 할인 금액 계산
      discountAmount = DiscountType.getPaymentPrice(coupon.getDiscountType(),
          room.getPrice().getOffWeekDaysMinFee(), coupon.getDiscount());
    }

    /*
     * 예약 및 결제 저장
     * */
    Reservation reservation = saveReservation(currentMember, request, room, discountAmount);

    // 쿠폰 사용 시 쿠폰 사용 내역 저장
    if (reservation.getIsCouponUsed()) {
      couponRedeemRepository.save(CouponRedeem.builder()
          .coupon(coupon)
          .reservation(reservation)
          .build());
    }
  }

  private void decreaseRoomStock(Room room, LocalDate startDate, LocalDate endDate) {
    if (room.getStatus() != RoomStatus.SELLING) {
      throw new InvalidReservationInfoException();
    }

    // 객실 재고 검증 및 get
    List<RoomStock> roomStocks = getRoomStock(room, startDate, endDate);

    // 객실 락 획득 및 재고 수정
    for (RoomStock roomStock : roomStocks) {
      stockService.decreaseRoomStock(roomStock.getId(), roomStock); //lock
    }
  }

  // 기간 내의 모든 재고가 1 이상 이어야 함
  private List<RoomStock> getRoomStock(Room room, LocalDate startDate, LocalDate endDate) {
    int daysCount = Period.between(startDate, endDate).getDays() + 1;

    List<RoomStock> roomStocks = roomStockRepository.findByRoomAndDateBetween(room,
        startDate, endDate);

    if (roomStocks.size() != daysCount &&
        !roomStocks.stream().allMatch(r -> r.getCount() >= 1)) {
      throw new InvalidReservationInfoException();
    }

    roomStocks.sort(Comparator.comparing(RoomStock::getDate));
    return roomStocks;
  }

  private void decreaseCouponStock(Coupon coupon) {
    if (coupon.getCouponStatus() != CouponStatus.ACTIVE && coupon.getCount() < 1) {
      throw new InvalidCouponException();
    }

    stockService.decreaseCouponStock(coupon.getId(), coupon); //lock
  }

  private Payment savePayment(Member member, CreateReservationRequest request, int roomPrice,
      int discountAmount) {
    int totalAmount = roomPrice - discountAmount;

    if (totalAmount != request.getTotalPrice()) {
      throw new PaymentFailureException();
    }

    return paymentRepository.save(Payment.builder()
        .member(member)
        .payMethod(request.getPayMethod())
        .roomPrice(roomPrice)
        .discountAmount(discountAmount)
        .totalAmount(totalAmount)
        .build());
  }

  private Reservation saveReservation(
      Member currentMember,
      CreateReservationRequest request,
      Room room,
      int discountAmount
  ) {
    /*
     * 예약 객실 저장
     * */
    ReservationRoom reservationRoom = reservationRoomRepository.save(ReservationRoom.builder()
        .room(room)
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .price(room.getPrice().getOffWeekDaysMinFee())
        .build());

    /*
     * 결제 정보 저장
     * 계산한 결제 금액과 요청 결제 금액이 다르면 결제 오류
     */
    Payment payment = savePayment(currentMember, request, reservationRoom.getPrice(),
        discountAmount);

    /*
     * 예약 저장
     * */
    return reservationRepository.save(Reservation.builder()
        .member(currentMember)
        .reservationRoom(reservationRoom)
        .visitorName(request.getVisitorName())
        .visitorPhone(request.getVisitorPhone())
        .payment(payment)
        .isCouponUsed(request.getCouponId() != null)
        .build());
  }

  @Transactional(readOnly = true)
  public GetReservedResponse getReserved(Member currentMember, Pageable pageable) {
    Page<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
        currentMember,
        Arrays.asList(ReservationStatus.RESERVED, ReservationStatus.SERVICED),
        pageable
    );
    return new GetReservedResponse(reservations);
  }

  @Transactional(readOnly = true)
  public GetCanceledResponse getCanceled(Member currentMember, Pageable pageable) {
    Page<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
        currentMember,
        List.of(ReservationStatus.CANCELLED),
        pageable
    );
    return new GetCanceledResponse(reservations);
  }
}
