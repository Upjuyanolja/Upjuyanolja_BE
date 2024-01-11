package com.backoffice.upjuyanolja.domain.member.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.backoffice.upjuyanolja.domain.member.entity.Authority;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
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
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void reset() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        memberRepository.deleteAll();
        entityManager.createNativeQuery("TRUNCATE TABLE member").executeUpdate();
        entityManager
            .createNativeQuery("ALTER TABLE member ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private void saveMember() {
        Member member = Member.builder()
            .email("test@mail.com")
            .password("$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm")
            .name("test")
            .phone("010-1234-1234")
            .imageUrl(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI")
            .authority(Authority.ROLE_USER)
            .build();
        memberRepository.save(member);
    }

    @Nested
    @DisplayName("findByEmail()는")
    class Context_findByEmail {

        @Test
        @DisplayName("Email 로 회원 정보를 조회할 수 있다.")
        void _willSuccess() {
            // given
            saveMember();

            // when
            Optional<Member> result = memberRepository.findByEmail("test@mail.com");

            // then
            assertThat(result.isPresent()).isTrue();
            assertThat(result.get().getId()).isNotNull();
            assertThat(result.get().getEmail()).isEqualTo("test@mail.com");
            assertThat(result.get().getPassword()).isEqualTo(
                "$10$ygrAExVYmFTkZn2d0.Pk3Ot5CNZwIBjZH5f.WW0AnUq4w4PtBi9Nm");
            assertThat(result.get().getName()).isEqualTo("test");
            assertThat(result.get().getPhone()).isEqualTo("010-1234-1234");
            assertThat(result.get().getImageUrl()).isEqualTo(
                "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI");
            assertThat(result.get().getAuthority()).isEqualTo(Authority.ROLE_USER);
        }
    }

    @Nested
    @DisplayName("existsByEmail()는")
    class Context_existsByEmail {

        @Test
        @DisplayName("Email 에 해당하는 회원 정보 존재 여부를 확인할 수 있다.")
        void _willSuccess() {
            // given
            saveMember();

            // when
            boolean result = memberRepository.existsByEmail("test@mail.com");

            // then
            assertThat(result).isTrue();
        }
    }
}
