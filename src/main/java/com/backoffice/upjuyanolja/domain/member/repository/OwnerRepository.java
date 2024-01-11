package com.backoffice.upjuyanolja.domain.member.repository;

import com.backoffice.upjuyanolja.domain.member.entity.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {


    Optional<Owner> findByEmail(String email);
    boolean existsByEmail(String email);
}
