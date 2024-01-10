package com.backoffice.upjuyanolja.domain.member.service;

import static com.backoffice.upjuyanolja.domain.member.entity.Authority.ROLE_ADMIN;

import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerEmailRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerEmailResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerSignupResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.entity.Owner;
import com.backoffice.upjuyanolja.domain.member.exception.CreateVerificationCodeException;
import com.backoffice.upjuyanolja.domain.member.exception.IncorrectVerificationCodeException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.exception.NotRegisteredEmailException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.member.repository.OwnerRepository;
import com.backoffice.upjuyanolja.global.redis.RedisService;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OwnerAuthService {

    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    private final OwnerRepository ownerRepository;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final RedisService redisService;
    private final BCryptPasswordEncoder encoder;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public OwnerEmailResponse sendVerificationCodeToEmail(OwnerEmailRequest request) {

        //DB에 이미 존재하는 이메일인지 검증 (존재 해야 회원가입 가능)
        if (!ownerRepository.existsByEmail(request.getEmail())) {
            throw new NotRegisteredEmailException();
        }

        String title = "UPJUYANOLJA 이메일 인증번호";
        String verificationCode = createCode();

        //인증번호를 담은 이메일 전송
        mailService.sendMail(request.getEmail(), title, verificationCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setValues(AUTH_CODE_PREFIX + request.getEmail(),
            verificationCode, Duration.ofMillis(this.authCodeExpirationMillis));
        String values = redisService.getValues(AUTH_CODE_PREFIX + request.getEmail());

        log.info("생성된 인증코드: {}", verificationCode);
        log.info("redis에 저장 된 비밀번호: {}", values);

        return OwnerEmailResponse.builder()
            .verificationCode(verificationCode)
            .build();
    }

    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CreateVerificationCodeException();
        }
    }

    public String verifyCode(String email, String authCode) {

        String values = redisService.getValues(AUTH_CODE_PREFIX + email);

        if (!values.equals(authCode)) {
            throw new IncorrectVerificationCodeException();
        }
        return "SUCCESS";
    }

    public OwnerSignupResponse ownerSignup(OwnerEmailRequest request) {
        Owner ownerInfo = ownerRepository.findByEmail(request.getEmail()).orElseThrow(
            MemberNotFoundException::new
        );

        Member member = memberRepository.save(Member.builder()
            .name(ownerInfo.getName())
            .phone(ownerInfo.getPhone())
            .email(request.getEmail())
            .password(encoder.encode(request.getPassword()))
            .imageUrl(ownerInfo.getImageUrl())
            .authority(ROLE_ADMIN)
            .build());

        return OwnerSignupResponse.fromEntity(member);
    }
}
