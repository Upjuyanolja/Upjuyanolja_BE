package com.backoffice.upjuyanolja.domain.room.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class RoomStockCustomRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomStockRepository roomStockRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Nested
    @DisplayName("findAllByRoomAndAfterToday()는")
    class Context_findAllByRoomAndAfterToday {

        @Test
        @DisplayName("슥소에 속한 객실 개수를 조회할 수 있다.")
        void _willSuccess() {
            // given
            Accommodation accommodation = saveAccommodation();
            Room room = saveRoom(accommodation);
            saveRoomStock(room);

            // when
            List<RoomStock> result = roomStockRepository.findAllByRoomAndAfterToday(room);

            // then
            assertThat(result.size()).isEqualTo(30);
        }
    }

    @BeforeEach
    public void reset() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        roomStockRepository.deleteAll();
        roomRepository.deleteAll();
        categoryRepository.deleteAll();
        accommodationRepository.deleteAll();
        entityManager.createNativeQuery("TRUNCATE TABLE room_stock").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE room").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE category").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE accommodation").executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE room_stock ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
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
            .amount(858)
            .status(RoomStatus.SELLING)
            .build());
    }

    private void saveRoomStock(Room room) {
        for (int i = 0; i < 30; i++) {
            roomStockRepository.save(RoomStock.builder()
                .room(room)
                .count(room.getAmount())
                .date(LocalDate.now().plusDays(i))
                .build());
        }
    }
}
