package com.backoffice.upjuyanolja.domain.member.controller;

import com.backoffice.upjuyanolja.domain.member.dto.request.SignInRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignUpRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.TokenRequestDto;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.MemberInfoResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.RefreshTokenDto;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignInResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignUpResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.TokenResponseDto;
import com.backoffice.upjuyanolja.domain.member.service.MemberAuthService;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.global.common.ApiResponse;
import com.backoffice.upjuyanolja.global.common.ApiResponse.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/members")
@RequiredArgsConstructor
public class MemberAuthController {

    private final MemberAuthService memberAuthService;
    private final MemberGetService memberGetService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<SignUpResponse>> signup(
        @Valid @RequestBody SignUpRequest request) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<SignUpResponse>builder()
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(memberAuthService.signup(request))
                .build());
    }

    @GetMapping("/email")
    public ResponseEntity<SuccessResponse<CheckEmailDuplicateResponse>> checkEmailDuplicate(
        @RequestParam(name = "email") String email
    ) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<CheckEmailDuplicateResponse>builder()
                .message("성공적으로 이메일 중복 여부를 검사했습니다.")
                .data(memberAuthService.checkEmailDuplicate(email))
                .build());
    }

    @PostMapping("/signin")
    public ResponseEntity<SuccessResponse<SignInResponse>> signin(
        @Valid @RequestBody SignInRequest request) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<SignInResponse>builder()
                .message("로그인이 성공적으로 완료되었습니다.")
                .data(memberAuthService.signin(request))
                .build());
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<MemberInfoResponse>> getMember(
        // TODO 시큐리티 로그인 적용 이후 토큰에서 memberId 받아오도록 수정
        @PathVariable(name = "memberId") long memberId) {
        return ApiResponse.success(HttpStatus.OK, SuccessResponse.<MemberInfoResponse>builder()
            .message("성공적으로 회원 정보를 조회했습니다.")
            .data(memberGetService.getMember(memberId))
            .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<RefreshTokenDto>> refresh(
        @Valid @RequestBody TokenRequestDto request) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<RefreshTokenDto>builder()
                .message("리프레쉬 토큰 재발급이 성공적으로 완료되었습니다.")
                .data(memberAuthService.refresh(request))
                .build());
    }
}
