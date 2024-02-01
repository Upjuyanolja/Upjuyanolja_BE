package com.backoffice.upjuyanolja.domain.reservation.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.reservation.entity.Reservation;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationRoom;
import com.backoffice.upjuyanolja.domain.reservation.entity.ReservationStatus;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRepository;
import com.backoffice.upjuyanolja.domain.reservation.repository.ReservationRoomRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(QueryDslConfig.class)
@DisplayName("ReservationRepository 단위 테스트")
class ReservationRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRoomRepository reservationRoomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    static Member member;

    static Room room;

    @BeforeEach
    public void initTest() {
        clear("member");
        clear("accommodation");
        clear("room");
        clear("reservation");

        member = Member.builder()
            .email("test@mail.com")
            .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
            .name("test")
            .phone("010-1234-1234")
            .imageUrl(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
            .authority(Authority.ROLE_USER)
            .build();
        memberRepository.save(member);

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
            .name("그랜드 하얏트 제주")
            .address("제주특별자치도 제주시 노형동 925")
            .detailAddress("")
            .zipCode("63082")
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

        accommodationRepository.save(accommodation);

        Room room = Room.builder()
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

        roomRepository.save(room);

        return room;
    }

    private void setupDummy() {
        saveReservation(LocalDate.now(), LocalDate.now().plusDays(1),
            false, ReservationStatus.RESERVED);
        saveReservation(LocalDate.now(), LocalDate.now().plusDays(1),
            true, ReservationStatus.RESERVED);
        saveReservation(LocalDate.now(), LocalDate.now().plusDays(1),
            false, ReservationStatus.SERVICED);
        saveReservation(LocalDate.now(), LocalDate.now().plusDays(1),
            false, ReservationStatus.CANCELLED);
    }

    private void saveReservation(
        LocalDate startDate,
        LocalDate endDate,
        boolean isCouponUsed,
        ReservationStatus status
    ) {
        ReservationRoom reservationRoom = ReservationRoom.builder()
            .room(room)
            .startDate(startDate)
            .endDate(endDate)
            .price(room.getPrice().getOffWeekDaysMinFee())
            .build();
        reservationRoomRepository.save(reservationRoom);

        Reservation reservation = Reservation.builder()
            .member(member)
            .reservationRoom(reservationRoom)
            .visitorName("홍길동")
            .visitorPhone("010-1234-5678")
            .isCouponUsed(isCouponUsed)
            .status(status)
            .build();
        reservationRepository.save(reservation);
    }

    @Nested
    @DisplayName("예약 조회 시 member와 status값으로 필터링 한다")
    class SearchReservation {

        @Test
        @DisplayName("member 일치 검사")
        void member_getReservedList() {
            // given
            setupDummy();

            int pageNumber = 0;
            int pageSize = 4;
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize, sort);

            // when
            Page<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
                member,
                Arrays.asList(ReservationStatus.RESERVED),
                pageable
            );

            // then
            reservations.forEach(reservation -> {
                assertEquals(member, reservation.getMember());
            });
        }

        @Test
        @DisplayName("예약 완료 || 이용 완료 두 상태 값을 전달 했을때 검사")
        void statusRESERVEDorSERVICED_getReservedList() {
            // given
            setupDummy();

            int pageNumber = 0;
            int pageSize = 4;
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize, sort);

            // when
            Page<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
                member,
                Arrays.asList(ReservationStatus.RESERVED, ReservationStatus.SERVICED),
                pageable
            );

            // then
            reservations.forEach(reservation -> {
                assertTrue(reservation.getStatus().equals(ReservationStatus.RESERVED) ||
                    reservation.getStatus().equals(ReservationStatus.SERVICED));
            });
        }

        @Test
        @DisplayName("예약 취소 상태 값을 전달 했을때 검사")
        void statusCANCELLED_getReservedList() {
            // given
            setupDummy();

            int pageNumber = 0;
            int pageSize = 4;
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize, sort);

            // when
            Page<Reservation> reservations = reservationRepository.findAllByMemberAndStatusIn(
                member,
                Arrays.asList(ReservationStatus.CANCELLED),
                pageable
            );

            // then
            reservations.forEach(reservation -> {
                assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
            });
        }
    }


}