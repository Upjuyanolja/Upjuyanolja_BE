package com.backoffice.upjuyanolja.domain.member.service;

import static com.backoffice.upjuyanolja.domain.member.entity.Authority.ROLE_ADMIN;

import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerEmailRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerSignupRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignInRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerEmailResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerSignupResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignInResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.TokenResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.entity.Owner;
import com.backoffice.upjuyanolja.domain.member.exception.CreateVerificationCodeException;
import com.backoffice.upjuyanolja.domain.member.exception.IncorrectPasswordException;
import com.backoffice.upjuyanolja.domain.member.exception.IncorrectVerificationCodeException;
import com.backoffice.upjuyanolja.domain.member.exception.InvalidRoleException;
import com.backoffice.upjuyanolja.domain.member.exception.InvalidSignupProcessException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberEmailDuplicationException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.exception.NotRegisteredEmailException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.member.repository.OwnerRepository;
import com.backoffice.upjuyanolja.global.redis.RedisService;
import com.backoffice.upjuyanolja.global.security.jwt.JwtTokenProvider;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OwnerAuthService implements
    AuthServiceProvider<OwnerSignupResponse, OwnerSignupRequest> {

    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    private final OwnerRepository ownerRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final RedisService redisService;
    private final BCryptPasswordEncoder encoder;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    private final String EMAIL_VERIFY_DONE_FLAG = "_isVerified";

    public OwnerEmailResponse sendVerificationCodeToEmail(OwnerEmailRequest request) {

        //이미 회원가입 된 상태인지 검증 (존재한다면 예외 발생)
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberEmailDuplicationException();
        }

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

        //이메일 인증을 마치면, key: 이메일, value: authCode를 레디스에 저장
        redisService.setValues(email+EMAIL_VERIFY_DONE_FLAG, "true");

        return "SUCCESS";
    }

    @Override
    public OwnerSignupResponse signup(OwnerSignupRequest request) {
        Owner ownerInfo = ownerRepository.findByEmail(request.getEmail()).orElseThrow(
            MemberNotFoundException::new
        );

        Member member = memberRepository.save(Member.builder()
            .name(ownerInfo.getName())
            .phone(ownerInfo.getPhone())
            .email(request.getEmail())
            .password(encoder.encode(request.getPassword()))
            .authority(ROLE_ADMIN)
            .build());

        if (!redisService.getValues(request.getEmail()+EMAIL_VERIFY_DONE_FLAG).equals("true")){
            throw new InvalidSignupProcessException();
        }
        redisService.deleteValues(request.getEmail()+EMAIL_VERIFY_DONE_FLAG);

        return OwnerSignupResponse.fromEntity(member);
    }

    @Override
    public SignInResponse signin(SignInRequest request) {
        //회원가입 여부 체크
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(MemberNotFoundException::new);

        //비밀번호 체크
        if (!encoder.matches(request.getPassword(), member.getPassword())) {
            throw new IncorrectPasswordException();
        }

        Authentication authentication = authenticationManager.authenticate(
            request.toUsernamePasswordAuthenticationToken());

        //토큰 발급
        TokenResponse tokenResponse = jwtTokenProvider.generateToken(authentication);

        //회원정보 취득
        Member memberInfo = memberRepository.findByEmail(authentication.getName())
            .orElseThrow(MemberNotFoundException::new);
        log.info("authentication get name is :{}", authentication.getName());

        if (!memberInfo.getAuthority().name().equals("ROLE_ADMIN")) {
            throw new InvalidRoleException();
        }

        //Redis에 RefreshToken 저장
        redisService.setValues(authentication.getName(), tokenResponse.getRefreshToken());

        return SignInResponse.builder()
            .accessToken(tokenResponse.getAccessToken())
            .refreshToken(tokenResponse.getRefreshToken())
            .id(memberInfo.getId())
            .email(memberInfo.getEmail())
            .name(memberInfo.getName())
            .phone(memberInfo.getPhone())
            .build();
    }
}
