package com.backoffice.upjuyanolja.domain.accommodation.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
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
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Nested
    @DisplayName("findCategoryByName()는")
    class Context_findCategoryByName {

        @Test
        @DisplayName("카테고리를 조회할 수 있다.")
        void _willSuccess() {
            // given
            saveCategory();

            // when
            Optional<Category> result = categoryRepository.findByName("TOURIST_HOTEL");

            // then
            assertThat(result.isPresent()).isTrue();
            assertThat(result.get().getName()).isEqualTo("TOURIST_HOTEL");
        }
    }

    @BeforeEach
    public void reset() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        categoryRepository.deleteAll();
        entityManager.createNativeQuery("TRUNCATE TABLE category").executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE category ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private void saveCategory() {
        Category hotelResort = categoryRepository.save(Category.builder()
            .id(1L)
            .name("HOTEL_RESORT")
            .build());
        Category motel = categoryRepository.save(Category.builder()
            .id(2L)
            .name("MOTEL")
            .build());
        Category pensionPoolVilla = categoryRepository.save(Category.builder()
            .id(3L)
            .name("PENSION_POOL_VILLA")
            .build());
        Category guestHouse = categoryRepository.save(Category.builder()
            .id(4L)
            .name("GUEST_HOUSE")
            .build());
        categoryRepository.save(Category.builder()
            .id(5L)
            .name("HOTEL")
            .parent(hotelResort)
            .build());
        categoryRepository.save(Category.builder()
            .id(6L)
            .name("RESORT")
            .parent(hotelResort)
            .build());
        categoryRepository.save(Category.builder()
            .id(7L)
            .name("TOURIST_HOTEL")
            .parent(hotelResort)
            .build());
        categoryRepository.save(Category.builder()
            .id(8L)
            .name("CONDO")
            .parent(hotelResort)
            .build());
        categoryRepository.save(Category.builder()
            .id(9L)
            .name("RESIDENCE")
            .parent(hotelResort)
            .build());
        categoryRepository.save(Category.builder()
            .id(10L)
            .name("MOTEL")
            .parent(motel)
            .build());
        categoryRepository.save(Category.builder()
            .id(11L)
            .name("PENSION_POOL_VILLA")
            .parent(pensionPoolVilla)
            .build());
        categoryRepository.save(Category.builder()
            .id(12L)
            .name("GUEST_HOUSE")
            .parent(guestHouse)
            .build());
        categoryRepository.save(Category.builder()
            .id(13L)
            .name("HANOK")
            .parent(guestHouse)
            .build());
    }
}
