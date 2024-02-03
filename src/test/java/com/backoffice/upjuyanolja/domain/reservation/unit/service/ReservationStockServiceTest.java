package com.backoffice.upjuyanolja.domain.reservation.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.reservation.exception.InvalidCouponException;
import com.backoffice.upjuyanolja.domain.reservation.exception.InvalidReservationInfoException;
import com.backoffice.upjuyanolja.domain.reservation.service.ReservationStockService;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@Transactional
@SpringBootTest
@Disabled
@DisplayName("ReservationStockService 단위 테스트")
class ReservationStockServiceTest {

    @Autowired
    ReservationStockService stockService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    RoomStockRepository roomStockRepository;

    @Autowired
    CouponRepository couponRepository;

    @PersistenceContext
    private EntityManager entityManager;

    static Room room;

    @BeforeEach
    public void initTest() {
        clear("category");
        clear("accommodation");
        clear("room");
        clear("room_stock");
        clear("coupon");

        room = createRoom();
    }

    private void clear(String tableName) {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM " + tableName).executeUpdate();
        entityManager.createNativeQuery(
            "ALTER TABLE " + tableName + " ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private Room createRoom() {
        Category category = Category.builder()
            .id(5L)
            .name("TOURIST_HOTEL")
            .build();
        categoryRepository.save(category);

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
            .rooms(new ArrayList<>())
            .build();
        accommodationRepository.save(accommodation);

        Room room = Room.builder()
            .id(1L)
            .accommodation(accommodation)
            .name("65m² 킹룸")
            .defaultCapacity(2)
            .maxCapacity(3)
            .checkInTime(LocalTime.of(15, 0, 0))
            .checkOutTime(LocalTime.of(11, 0, 0))

            .amount(858)
            .status(RoomStatus.SELLING)
            .images(new ArrayList<>())
            .build();
        roomRepository.save(room);

        return room;
    }

    @Nested
    @DisplayName("객실 재고 동시성 테스트")
    class RoomStockConcurrency {

        private RoomStock createRoomStock(int count) {
            return roomStockRepository.save(RoomStock.builder()
                .id(1L)
                .room(room)
                .count(count)
                .date(LocalDate.now())
                .build());
        }

        @Test
        @DisplayName("재고100 일때 100명이 요청 하면 재고는 0이어야 한다")
        void success_100_stock_100_request_100() throws InterruptedException {
            // given
            int numberOfStock = 100;
            int numberOfThreads = 100;

            RoomStock roomStock = createRoomStock(numberOfStock);
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            // when
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        stockService.decreaseRoomStock(roomStock.getId());
                    } catch (InvalidReservationInfoException e) {
                        log.info(e.getClass().getName());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            // then
            RoomStock actual = roomStockRepository.findById(roomStock.getId()).get();
            assertEquals(0, actual.getCount(), "재고는 0이다");
        }
    }

    @Nested
    @DisplayName("쿠폰 재고 동시성 테스트")
    class CouponStockConcurrency {

        private Coupon createCoupon(int count) {
            return couponRepository.save(Coupon.builder()
                .room(room)
                .couponType(CouponType.ALL_DAYS)
                .discountType(DiscountType.FLAT)
                .couponStatus(CouponStatus.ENABLE)
                .discount(1000)
                .endDate(LocalDate.now().plusMonths(1))
                .dayLimit(10)
                .stock(5)
                .build());
        }

        @Test
        @DisplayName("재고100 일때 100명이 요청 하면 재고는 0이어야 한다")
        void success_100_stock_100_request_100() throws InterruptedException {
            // given
            int numberOfStock = 100;
            int numberOfThreads = 100;

            Coupon coupon = createCoupon(numberOfStock);
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            // when
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        stockService.decreaseCouponStock(coupon.getId());
                    } catch (InvalidCouponException e) {
                        log.info(e.getClass().getName());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            // then
            Coupon actual = couponRepository.findById(coupon.getId()).get();
            assertEquals(0, actual.getStock(), "재고는 0이다");
        }
    }
}