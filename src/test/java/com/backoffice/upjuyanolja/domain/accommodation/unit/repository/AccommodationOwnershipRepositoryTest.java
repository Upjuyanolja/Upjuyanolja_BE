package com.backoffice.upjuyanolja.domain.accommodation.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOptionRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.repository.RoomOptionRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(QueryDslConfig.class)
public class AccommodationOwnershipRepositoryTest {

    @Autowired
    private AccommodationOwnershipRepository accommodationOwnershipRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private AccommodationOptionRepository accommodationOptionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomOptionRepository roomOptionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void reset() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        roomRepository.deleteAll();
        categoryRepository.deleteAll();
        accommodationRepository.deleteAll();
        accommodationOwnershipRepository.deleteAll();
        accommodationOptionRepository.deleteAll();
        memberRepository.deleteAll();
        entityManager.createNativeQuery("TRUNCATE TABLE room").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE category").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE accommodation").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE accommodation_ownership").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE accommodation_option").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE member").executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE room ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE category ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE accommodation ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager
            .createNativeQuery(
                "ALTER TABLE accommodation_ownership ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager
            .createNativeQuery(
                "ALTER TABLE accommodation_option ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE member ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private AccommodationOwnership saveAccommodationOwnership(Accommodation accommodation,
        Member member) {
        return accommodationOwnershipRepository.save(AccommodationOwnership.builder()
            .accommodation(accommodation)
            .member(member)
            .build()
        );
    }

    private Category saveCategory() {
        return categoryRepository.save(Category.builder()
            .id(5L)
            .name("TOURIST_HOTEL")
            .build()
        );
    }

    private Accommodation saveAccommodation() {
        return accommodationRepository.save(Accommodation.builder()
            .name("그랜드 하얏트 제주")
            .address("제주특별자치도 제주시 노형동 925")
            .detailAddress("")
            .zipCode("63082")
            .category(saveCategory())
            .description(
                "63빌딩의 1.8배 규모인 연면적 30만 3737m2, 높이 169m(38층)를 자랑하는 제주 최대 높이, 최대 규모의 랜드마크이다. 제주 고도제한선(55m)보다 높이 위치한 1,600 올스위트 객실, 월드클래스 셰프들이 포진해 있는 14개의 글로벌 레스토랑 & 바, 인피니티 풀을 포함한 8층 야외풀데크, 38층 스카이데크를 비롯해 HAN컬렉션 K패션 쇼핑몰, 2개의 프리미엄 스파, 8개의 연회장 등 라스베이거스, 싱가포르, 마카오에서나 볼 수 있는 세계적인 수준의 복합리조트이다. 제주국제공항에서 차량으로 10분거리(5km)이며 제주의 강남이라고 불리는 신제주 관광 중심지에 위치하고 있다.")
            .thumbnail("http://tong.visitkorea.or.kr/cms/resource/83/2876783_image2_1.jpg")
            .images(new ArrayList<>())
            .rooms(new ArrayList<>())
            .build());
    }

    private AccommodationOption saveAccommodationOption(Accommodation accommodation) {
        return accommodationOptionRepository.save(AccommodationOption.builder()
            .accommodation(accommodation)
            .cooking(false)
            .parking(true)
            .pickup(false)
            .barbecue(false)
            .fitness(true)
            .karaoke(false)
            .sauna(false)
            .sports(true)
            .seminar(true)
            .build());
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder()
            .email("test@mail.com")
            .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
            .name("test")
            .phone("010-1234-1234")
            .imageUrl(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
            .authority(Authority.ROLE_ADMIN)
            .build());
    }

    private Room saveRoom(Accommodation accommodation) {
        return roomRepository.save(Room.builder()
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
            .images(new ArrayList<>())
            .build());
    }


    private RoomOption saveRoomOption(Room room) {
        return roomOptionRepository.save(RoomOption.builder()
            .room(room)
            .airCondition(true)
            .tv(true)
            .internet(true)
            .build());
    }

    @Nested
    @DisplayName("findAllByMember()는")
    class Context_findAllByMember {

        @Test
        @DisplayName("")
        void _willSuccess() {
            // given
            Accommodation accommodation = saveAccommodation();
            saveRoom(accommodation);
            Member member = saveMember();
            saveAccommodationOwnership(accommodation, member);

            // when
            List<AccommodationOwnership> result = accommodationOwnershipRepository
                .findAllByMember(member);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(0).getAccommodation().getId()).isEqualTo(accommodation.getId());
            assertThat(result.get(0).getMember().getId()).isEqualTo(member.getId());
        }
    }

    @Nested
    @DisplayName("existsAccommodationOwnershipByMemberAndAccommodation()는")
    class Context_existsAccommodationOwnershipByMemberAndAccommodation {

        @Test
        @DisplayName("업주의 소유권 여부를 조회할 수 있다.")
        void _willSuccess() {
            // given
            Accommodation accommodation = saveAccommodation();
            saveRoom(accommodation);
            Member member = saveMember();
            saveAccommodationOwnership(accommodation, member);

            // when
            boolean result = accommodationOwnershipRepository
                .existsAccommodationOwnershipByMemberAndAccommodation(member, accommodation);

            // then
            assertThat(result).isTrue();
        }
    }
}
