package com.backoffice.upjuyanolja.domain.coupon.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backoffice.upjuyanolja.domain.coupon.repository.CouponRepository;
import com.backoffice.upjuyanolja.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(QueryDslConfig.class)
@DisplayName("CouponRepository 단위 테스트")
@Transactional
class CouponRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void initTest() {
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("쿠폰 등록시 ")
    @Sql(scripts = {"classpath:clear_table.sql", "classpath:create_data.sql"})
    class MakeCoupon {

        @Test
        @DisplayName("View에 응답해줄 데이터를 테스트한다.")
        public void makeCouponTest() throws Exception {
            // given
            CouponMakeViewResponse response = couponRepository
                .findRoomsByAccommodationId(1L);

            // when & Then
            assertThat(response.accommodationName()).isEqualTo("양평 관광호텔");
            assertThat(response.rooms().size()).isEqualTo(3);
            response.rooms().forEach(room -> {
                assertThat(room.roomId()).isGreaterThan(0);
                assertThat(room.roomName()).isNotBlank();
                assertThat(room.roomPrice()).isNotNull();
            });
        }
    }
}