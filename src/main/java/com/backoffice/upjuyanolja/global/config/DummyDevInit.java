package com.backoffice.upjuyanolja.global.config;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.coupon.entity.Coupon;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponIssuance;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponStatus;
import com.backoffice.upjuyanolja.domain.coupon.entity.CouponType;
import com.backoffice.upjuyanolja.domain.coupon.entity.DiscountType;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponIssuanceRepository;
import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.point.entity.Point;
import com.backoffice.upjuyanolja.domain.point.entity.PointType;
import com.backoffice.upjuyanolja.domain.point.repository.PointRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional
public class DummyDevInit {

    private final MemberRepository memberRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;
    private final CouponRepository couponRepository;
    private final RoomRepository roomRepository;
    private final CategoryRepository categoryRepository;
    private final PointRepository pointRepository;
    private final CouponIssuanceRepository couponIssuanceRepository;
    private final BCryptPasswordEncoder encoder;
    private final MemberGetService memberGetService;

    @Profile("dev")
    @Bean
    ApplicationRunner init() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                createMember(1L);
                Member member = memberGetService.getMemberById(1L);
                Accommodation accommodation = createAccommodation(1L);
                createAccommodationOwnership(accommodation, member);
                createRooms(accommodation);
                createPoint(1L, member, 50000);
            }
        };
    }

    private void createMember(Long id) {
        Member member = Member.builder()
            .id(id)
            .email("test1@tester.com")
            .password(encoder.encode("abcd@1234Z"))
            .name("test")
            .phone("010-1234-1234")
            .imageUrl(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
            .authority(Authority.ROLE_USER)
            .build();
        memberRepository.save(member);
    }

    private Accommodation createAccommodation(Long accommodationId) {
        Accommodation accommodation = Accommodation.builder()
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

    private AccommodationOwnership createAccommodationOwnership(
        Accommodation accommodation, Member member) {
        AccommodationOwnership ownership = AccommodationOwnership.builder()
            .id(1L)
            .accommodation(accommodation)
            .member(member)
            .build();
        accommodationOwnershipRepository.save(ownership);
        return ownership;
    }

    private List<Room> createRooms (Accommodation accommodation) {
        List<Room> rooms = List.of(
            createRoom(1L, "스탠다드", accommodation),
            createRoom(2L, "디럭스", accommodation),
            createRoom(3L, "스위트", accommodation)
        );
        roomRepository.saveAll(rooms);
        return rooms;
    }

    private Room createRoom(Long roomId, String roomName, Accommodation accommodation) {
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

    private Room saveRoom(Long roomId, String roomName, Accommodation accommodation) {
        Room room = createRoom(roomId, roomName, accommodation);
        roomRepository.save(room);
        return room;
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

    private CouponIssuance saveCouponIssuance(Coupon coupon, Room room, Point point) {
        CouponIssuance couponIssuance = CouponIssuance.builder()
            .coupon(coupon)
            .room(room)
            .point(point)
            .build();
        couponIssuanceRepository.save(couponIssuance);
        return couponIssuance;
    }

    private Point createPoint(Long pointId, Member member, int balance) {
        Point point = Point.builder()
            .id(pointId)
            .member(member)
            .pointBalance(balance)
            .pointType(PointType.USE)
            .build();
        pointRepository.save(point);
        return point;
    }
}
