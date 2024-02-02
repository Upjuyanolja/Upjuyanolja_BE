package com.backoffice.upjuyanolja.domain.room.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomPageRequest;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalTime;
import java.util.ArrayList;
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

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(QueryDslConfig.class)
public class RoomRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RoomRepository roomRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void reset() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        roomRepository.deleteAll();
        categoryRepository.deleteAll();
        accommodationRepository.deleteAll();
        entityManager.createNativeQuery("TRUNCATE TABLE room").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE category").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE accommodation").executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE room ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE category ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE accommodation ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
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

    private void saveRoom(Accommodation accommodation) {
        roomRepository.save(Room.builder()
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

    @Nested
    @DisplayName("findAllByAccommodation()는")
    class Context_findAllByAccommodation {

        @Test
        @DisplayName("객실 목록을 페이지네이션하여 조회할 수 있다.")
        void _willSuccess() {
            // given
            Accommodation accommodation = saveAccommodation();
            saveRoom(accommodation);

            // when
            Page<Room> result = roomRepository.findAllByAccommodation(1L,
                RoomPageRequest.builder()
                    .pageNum(0)
                    .pageSize(10)
                    .build().of());

            // then
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.isLast()).isEqualTo(true);
            assertThat(result.get().findFirst().isPresent()).isEqualTo(true);
            assertThat(result.get().findFirst().get().getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("existsRoomByNameAndAccommodation()는")
    class Context_existsRoomByNameAndAccommodation {

        @Test
        @DisplayName("해당 객실 이름을 가진 객실의 존재 여부를 확인할 수 있다.")
        void _willSuccess() {
            // given
            Accommodation accommodation = saveAccommodation();
            saveRoom(accommodation);

            // when
            boolean result = roomRepository
                .existsRoomByNameAndAccommodation("65m² 킹룸", accommodation);

            // then
            assertThat(result).isTrue();
        }
    }
}
