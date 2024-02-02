package com.backoffice.upjuyanolja.domain.coupon.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponMakeQueryDto;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.backoffice.CouponManageQueryDto;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(QueryDslConfig.class)
@DisplayName("CouponRepository 단위 테스트")
@Transactional
class CouponRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AccommodationOwnershipRepository ownershipRepository;

    @PersistenceContext
    private EntityManager entityManager;

    Member mockMember;
    Accommodation mockAccommodation;
    AccommodationOwnership mockOwnership;
    Room mockRoom;
    Coupon mockCoupon;
    Point mockPoint;
    List<Room> mockRooms;

    @BeforeEach
    public void initTest() {
        clearTable("member");
        clearTable("accommodation");
        clearTable("room");
        clearTable("coupon");
        clearTable("point");

        mockMember = createMember(1L);
        mockAccommodation = createAccommodation(1L);

        List<Long> roomIdSet = List.of(1L, 2L, 3L);
        List<String> roomNameSet = List.of("스탠다드", "디럭스", "스위트");
        mockRooms = createRooms(mockAccommodation, roomIdSet, roomNameSet);

        List<Long> couponIds1 = List.of(1L, 2L, 3L, 4L);
        List<Long> couponIds2 = List.of(5L, 6L, 7L, 8L);
        List<Long> couponIds3 = List.of(9L, 10L, 11L, 12L);
        createCoupons(couponIds1, mockRooms.get(0));
        createCoupons(couponIds2, mockRooms.get(1));
        createCoupons(couponIds3, mockRooms.get(2));

        createPoint(1L, mockMember, 50000L);
        mockPoint = createPoint(1L, mockMember, 50000L);
    }

    @Nested
    @DisplayName("쿠폰 등록시 ")
    class MakeCoupon {

        @Test
        @DisplayName("View에 응답해줄 데이터를 테스트한다.")
        public void makeCouponTest() throws Exception {
            // given
            List<CouponMakeQueryDto> response = couponRepository
                .findRoomsByAccommodationId(1L);

            // when& Then
            assertThat(response.get(0).accommodationName()).isEqualTo("그랜드 하얏트 제주");
            assertThat(response.size()).isEqualTo(3);
        }

        @DisplayName("업주의 id로 등록되어 있는 숙소가 있는지 검증한다.")
        @Test
        public void isExistsAccommodationIdByMemberId_Test() throws Exception {
            // given
            Long accommodationId = mockAccommodation.getId();
            Long memberId = mockMember.getId();
            mockOwnership = createAccommodationOwnership();

            // when & Then
            boolean result = couponRepository.existsAccommodationIdByMemberId(
                accommodationId, memberId);
            assertThat(result).isTrue();

            // 업주의 id와 등록된 숙소의 id가 다르다면 false
            boolean result2 = couponRepository.existsAccommodationIdByMemberId(
                2L, memberId);
            assertThat(result2).isFalse();
        }

        @DisplayName("객실 id와 discount, discount_type 으로 발행된 쿠폰이 있는지 검색할 수 있다.")
        @Test
        public void findCouponsByRoomIdAndDiscountAndDiscountType_Test() throws Exception {
            // given
            mockRoom = saveRoom(1L, "막가는 호텔", mockAccommodation);
            mockCoupon = saveCoupon(1L, mockRoom, DiscountType.FLAT, CouponStatus.ENABLE, 2000, 10);

            // when & Then
            assertThat(couponRepository.findByRoomIdAndDiscount(1L, 2000).get())
                .isInstanceOf(Coupon.class);
        }
    }

    @Nested
    @DisplayName("쿠폰 관리 테스트 ")
    class ManageCoupon {

        @DisplayName("쿠폰 관리를 위한 화면 데이터 응답을 테스트한다.")
        @Test
        public void couponManageResponseQueryTest() throws Exception {
            // given
            Long accommodationId = mockAccommodation.getId();
            List<CouponManageQueryDto> result = couponRepository.findCouponsByAccommodationId(
                accommodationId);

            // when & Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).accommodationId()).isEqualTo(accommodationId);
            assertThat(result.size()).isEqualTo(9);
            result.stream().forEach(System.out::println);
        }
    }

    private Member createMember(Long id) {
        Member member = Member.builder()
            .id(id)
            .email("test@mail.com")
            .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
            .name("test")
            .phone("010-1234-1234")
            .imageUrl(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
            .authority(Authority.ROLE_USER)
            .build();
        memberRepository.save(member);
        return member;
    }

    private Accommodation createAccommodation(Long accommodationId) {
        Accommodation accommodation = Accommodation.builder()
            .id(accommodationId)
            .name("그랜드 하얏트 제주")
            .address("제주특별자치도 제주시 노형동 925")
            .detailAddress("")
            .zipCode("63082")
            .category(createCategory())
            .description(
                "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
            .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
            .images(new ArrayList<>())
            .rooms(new ArrayList<>())
            .build();
        accommodationRepository.save(accommodation);
        return accommodation;
    }

    private Category createCategory() {
        Category category = Category.builder()
            .id(5L)
            .name("TOURIST_HOTEL")
            .build();
        categoryRepository.save(category);
        return category;
    }

    private AccommodationOwnership createAccommodationOwnership() {
        AccommodationOwnership ownership = AccommodationOwnership.builder()
            .id(1L)
            .accommodation(mockAccommodation)
            .member(mockMember)
            .build();
        ownershipRepository.save(ownership);
        return ownership;
    }

    private List<Room> createRooms(Accommodation mockAccommodation, List<Long> roomIds, List<String> roomNames) {
        List<Room> rooms = List.of(
            createRoom(roomIds.get(0), roomNames.get(0), mockAccommodation),
            createRoom(roomIds.get(1), roomNames.get(1), mockAccommodation),
            createRoom(roomIds.get(2), roomNames.get(2), mockAccommodation)
        );
        roomRepository.saveAll(rooms);
        return rooms;
    }

    private Room createRoom(Long roomId, String roomName, Accommodation mockAccommodation) {
        return Room.builder()
            .id(roomId)
            .accommodation(mockAccommodation)
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
            .images(new ArrayList<>())
            .build();
    }

    private Room saveRoom(Long roomId, String roomName, Accommodation mockAccommodation) {
        Room room = createRoom(roomId, roomName, mockAccommodation);
        roomRepository.save(room);
        return room;
    }

    private List<Coupon> createCoupons(List<Long> couponIds, Room room) {
        List<Coupon> coupons = List.of(
            createCoupon(
                couponIds.get(0), room, DiscountType.FLAT, CouponStatus.ENABLE, 5000, 20),
            createCoupon(
                couponIds.get(1), room, DiscountType.RATE, CouponStatus.ENABLE, 10, 20),
            createCoupon(
                couponIds.get(2), room, DiscountType.FLAT, CouponStatus.SOLD_OUT, 1000, 0)
        );
        couponRepository.saveAll(coupons);
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

    private Coupon saveCoupon(
        Long couponId,
        Room room,
        DiscountType discountType,
        CouponStatus status,
        int discount,
        int stock
    ) {
        Coupon coupon = Coupon.builder()
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
        couponRepository.save(coupon);
        return coupon;
    }

    private Point createPoint(Long pointId, Member member, long amount) {
        Point point = Point.builder()
            .id(pointId)
            .member(member)
            .totalPointBalance(amount)
            .build();
        pointRepository.save(point);
        return point;
    }


    private void clearTable(String tableName) {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM " + tableName).executeUpdate();
        entityManager.createNativeQuery(
            "ALTER TABLE " + tableName + " ALTER COLUMN `id` RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}

