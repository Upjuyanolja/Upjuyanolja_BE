package com.backoffice.upjuyanolja.domain.member.service;

import static com.backoffice.upjuyanolja.domain.member.entity.Authority.ROLE_USER;

import com.backoffice.upjuyanolja.domain.member.dto.request.SignInRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignUpRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.TokenRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.RefreshTokenResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignInResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignUpResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.TokenResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.entity.RefreshToken;
import com.backoffice.upjuyanolja.domain.member.exception.IncorrectPasswordException;
import com.backoffice.upjuyanolja.domain.member.exception.InvalidRefreshTokenException;
import com.backoffice.upjuyanolja.domain.member.exception.LoggedOutMemberException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberEmailDuplicationException;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.MemberRepository;
import com.backoffice.upjuyanolja.domain.member.repository.RefreshTokenRepository;
import com.backoffice.upjuyanolja.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SignUpResponse signup(SignUpRequest request) {
        validateDuplicatedEmail(request.email());

        Member member = memberRepository.save(Member.builder()
            .name(request.name())
            .phone(request.phone())
            .email(request.email())
            .password(encoder.encode(request.password()))
            .imageUrl(request.imageUrl())
            .authority(ROLE_USER)
            .build());

        return SignUpResponse.fromEntity(member);
    }

    @Transactional
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

        //토큰 객체 생성 및 저장
        RefreshToken refreshToken = RefreshToken.builder()
            .id(authentication.getName())
            .token(tokenResponse.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);

        return SignInResponse.builder()
            .accessToken(tokenResponse.getAccessToken())
            .refreshToken(tokenResponse.getRefreshToken())
            .id(memberInfo.getId())
            .email(memberInfo.getEmail())
            .name(memberInfo.getName())
            .phone(memberInfo.getPhone())
            .build();
    }

    public void validateDuplicatedEmail(String email) {
        if (isDuplicatedEmail(email)) {
            throw new MemberEmailDuplicationException();
        }
    }

    public CheckEmailDuplicateResponse checkEmailDuplicate(String email) {
        return CheckEmailDuplicateResponse.builder()
            .isExists(isDuplicatedEmail(email))
            .build();
    }

    public boolean isDuplicatedEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public RefreshTokenResponse refresh(TokenRequest request) {

        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(
            request.getAccessToken());
        log.info("멤버 아이디: {}", authentication.getName());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findById(authentication.getName())
            .orElseThrow(LoggedOutMemberException::new);

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getToken().equals(request.getRefreshToken())) {
            throw new InvalidRefreshTokenException();
        }
        log.info("요청한 리프레쉬 토큰: {} ", request.getRefreshToken());
        log.info("디비에 저장된 리프레쉬 토큰: {} ", refreshToken.getToken());

        // 5. 새로운 토큰 생성
        TokenResponse newToken = jwtTokenProvider.generateToken(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateRefreshToken(newToken.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return new RefreshTokenResponse(newRefreshToken.getToken());
    }
}
