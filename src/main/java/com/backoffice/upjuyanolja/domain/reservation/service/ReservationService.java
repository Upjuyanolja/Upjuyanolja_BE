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
import com.backoffice.upjuyanolja.domain.reservation.exception.NoSuchReservationException;
import com.backoffice.upjuyanolja.domain.reservation.exception.NoSuchReservationRoomException;
import com.backoffice.upjuyanolja.domain.reservation.exception.PaymentFailureException;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRepository;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRoomRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.service.RoomQueryService;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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
    private final CouponRepository couponRepository;

    private final CouponRedeemRepository couponRedeemRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final ReservationRepository reservationRepository;

    private final RoomCommandUseCase roomCommandUseCase;
    private final RoomQueryService roomQueryService;
    private final ReservationStockService stockService;

    @Transactional
    public void create(Member currentMember, CreateReservationRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(InvalidReservationInfoException::new);
        if (room.getStatus() != RoomStatus.SELLING) {
            throw new InvalidReservationInfoException();
        }

        /*
         * 객실 재고 검증
         * */
        List<RoomStock> roomStocks = getRoomStock(room, request.getStartDate(),
            request.getEndDate());

        /*
         * 쿠폰 유효성 및 재고 검증
         * request.getCouponId() = null 인 경우 스킵
         * */
        Coupon coupon = null;
        if (request.getCouponId() != null) {
            // coupon 유효성 검사
            coupon = couponRepository.findByIdAndRoom(request.getCouponId(), room)
                .orElseThrow(InvalidCouponException::new);
            if (coupon.getCouponStatus() != CouponStatus.ENABLE || coupon.getStock() < 1) {
                throw new InvalidCouponException();
            }
        }

        // 할인 금액 계산
        int totalAmount = getValidTotalAmount(request.getTotalPrice(),
            room.getPrice().getOffWeekDaysMinFee(), coupon);

        /*
         * 객실 재고 차감
         * */
        int index = 0;
        try {
            for (RoomStock roomStock : roomStocks) {
                stockService.decreaseRoomStock(roomStock.getId()); //lock
                index++;
            }
        } catch (Exception e) {
            for (int i = 0; i < index; i++) {
                stockService.increaseRoomStock(roomStocks.get(i).getId());
            }
        }

        /*
         * 쿠폰 재고 차감
         * */
        if (coupon != null) {
            stockService.decreaseCouponStock(coupon.getId()); //lock
        }

        /*
         * 예약 및 결제 저장
         * 예외 발생 시 보상트랜잭션(재고 롤백) 수행
         * */
        try {
            Reservation reservation = saveReservation(currentMember, request, room, totalAmount);

            // 쿠폰 사용 시 쿠폰 사용 내역 저장
            if (coupon != null) {
                couponRedeemRepository.save(CouponRedeem.builder()
                    .coupon(coupon)
                    .reservation(reservation)
                    .build());
            }
        } catch (Exception e) {
            for (RoomStock roomStock : roomStocks) {
                stockService.increaseRoomStock(roomStock.getId()); //lock
            }

            if (coupon != null) {
                stockService.increaseCouponStock(coupon.getId()); //lock
            }
        }
    }

    // 기간 내의 모든 재고가 1 이상 이어야 함
    private List<RoomStock> getRoomStock(Room room, LocalDate startDate, LocalDate endDate) {
        int daysCount = Period.between(startDate, endDate).getDays() + 1;

        List<RoomStock> roomStocks = roomQueryService.getFilteredRoomStocksByDate(room,
            startDate, endDate);

        if (roomStocks.size() != daysCount ||
            !roomStocks.stream().allMatch(r -> r.getCount() >= 1)) {
            throw new InvalidReservationInfoException();
        }

        List<RoomStock> modifiableRoomStocks = new ArrayList<>(roomStocks);
        modifiableRoomStocks.sort(Comparator.comparing(RoomStock::getDate));
        return modifiableRoomStocks;
    }

    private int getValidTotalAmount(int totalAmountRequest, int roomPrice, Coupon coupon) {
        int totalAmount = (coupon == null) ? roomPrice
            : DiscountType.makePaymentPrice(coupon.getDiscountType(), roomPrice,
                coupon.getDiscount());

        if (totalAmountRequest != totalAmount) {
            throw new PaymentFailureException();
        }

        return totalAmount;
    }

    private Reservation saveReservation(
        Member currentMember,
        CreateReservationRequest request,
        Room room,
        int totalAmount
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
            totalAmount);

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
            .status(ReservationStatus.RESERVED)
            .build());
    }

    private Payment savePayment(
        Member member, CreateReservationRequest request, int roomPrice, int totalAmount
    ) {
        return paymentRepository.save(Payment.builder()
            .member(member)
            .payMethod(request.getPayMethod())
            .roomPrice(roomPrice)
            .discountAmount(roomPrice - totalAmount)
            .totalAmount(totalAmount)
            .build());
    }

    @Transactional
    public void cancel(Member currentMember, Long reservationId) {
        Reservation reservation = reservationRepository
            .findByIdAndMember(reservationId, currentMember)
            .orElseThrow(NoSuchReservationException::new);

        /*
         * 예약 상태값 변경 ReservationStatus.CANCELLED
         * */
        reservation.updateStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        /*
         * 쿠폰 사용 내역 제거 & 쿠폰 재고 증가
         * - 쿠폰 미사용 예약 내역 시 스킵
         * */
        if (Boolean.TRUE.equals(reservation.getIsCouponUsed())) {
            Coupon coupon = deleteCouponRedeem(reservation);
            stockService.increaseCouponStock(coupon.getId());
        }

        /*
         * 객실 재고 증가
         * */
        increaseRoomStock(reservation.getReservationRoom());
    }

    private Coupon deleteCouponRedeem(Reservation reservation) {
        CouponRedeem couponRedeem = couponRedeemRepository.findByReservation(reservation)
            .orElseThrow(NoSuchReservationException::new);

        couponRedeemRepository.delete(couponRedeem);

        return couponRedeem.getCoupon();
    }

    private void increaseRoomStock(ReservationRoom reservationRoom) {
        List<RoomStock> roomStocks = getReservationRoomStock(
            reservationRoom.getRoom(),
            reservationRoom.getStartDate(),
            reservationRoom.getEndDate()
        );

        for (RoomStock roomStock : roomStocks) {
            stockService.increaseRoomStock(roomStock.getId()); //lock
        }
    }

    private List<RoomStock> getReservationRoomStock(Room room, LocalDate startDate,
        LocalDate endDate) {
        int daysCount = Period.between(startDate, endDate).getDays() + 1;

        List<RoomStock> roomStocks = roomQueryService.getFilteredRoomStocksByDate(room,
            startDate, endDate);

        if (roomStocks.size() != daysCount) {
            throw new NoSuchReservationRoomException();
        }

        List<RoomStock> modifiableRoomStocks = new ArrayList<>(roomStocks);
        modifiableRoomStocks.sort(Comparator.comparing(RoomStock::getDate));
        return modifiableRoomStocks;
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
