package com.backoffice.upjuyanolja.domain.member.repository;

import com.backoffice.upjuyanolja.domain.member.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findById(String id);

}
