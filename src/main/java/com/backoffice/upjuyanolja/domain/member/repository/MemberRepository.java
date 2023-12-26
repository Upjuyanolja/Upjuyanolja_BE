package com.backoffice.upjuyanolja.domain.member.repository;

import com.backoffice.upjuyanolja.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
