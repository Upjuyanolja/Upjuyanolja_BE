package com.backoffice.upjuyanolja.domain.member.controller;

import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerEmailRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerSignupRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignInRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignUpRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.TokenRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.MemberInfoResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerEmailResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerSignupResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.RefreshTokenResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignInResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignUpResponse;
import com.backoffice.upjuyanolja.domain.member.service.MemberAuthService;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.member.service.OwnerAuthService;
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
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class MemberAuthController {

    private final MemberAuthService memberAuthService;
    private final MemberGetService memberGetService;
    private final OwnerAuthService ownerAuthService;

    @PostMapping("members/signup")
    public ResponseEntity<SuccessResponse<SignUpResponse>> signup(
        @Valid @RequestBody SignUpRequest request) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<SignUpResponse>builder()
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(memberAuthService.signup(request))
                .build());
    }

    @PostMapping("owners/request-email")
    public ResponseEntity<SuccessResponse<OwnerEmailResponse>> sendMessage(
        @Valid @RequestBody OwnerEmailRequest request){
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<OwnerEmailResponse>builder()
                .message("이메일 인증요청이 성공적으로 완료되었습니다")
                .data(ownerAuthService.sendVerificationCodeToEmail(request))
                .build());
    }

    @GetMapping("owners/verify")
    public ResponseEntity<SuccessResponse<String>> verifyMessage(
        @Valid @RequestParam(name = "email") String email,
        @RequestParam(name = "verification-code") String authCode
    ){
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<String>builder()
                .message("이메일 인증이 성공적으로 완료되었습니다.")
                .data(ownerAuthService.verifyCode(email, authCode))
                .build());
    }

    @PostMapping("owners/signup")
    public ResponseEntity<SuccessResponse<OwnerSignupResponse>> ownerSingup(
        @Valid @RequestBody OwnerEmailRequest request
    ){
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<OwnerSignupResponse>builder()
                .message("업주 회원가입이 성공적으로 완료되었습니다.")
                .data(ownerAuthService.ownerSignup(request))
                .build());
    }

    @GetMapping("members/email")
    public ResponseEntity<SuccessResponse<CheckEmailDuplicateResponse>> checkEmailDuplicate(
        @RequestParam(name = "email") String email
    ) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<CheckEmailDuplicateResponse>builder()
                .message("성공적으로 이메일 중복 여부를 검사했습니다.")
                .data(memberAuthService.checkEmailDuplicate(email))
                .build());
    }

    @PostMapping("members/signin")
    public ResponseEntity<SuccessResponse<SignInResponse>> signin(
        @Valid @RequestBody SignInRequest request) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<SignInResponse>builder()
                .message("로그인이 성공적으로 완료되었습니다.")
                .data(memberAuthService.signin(request))
                .build());
    }

    @GetMapping("members/{memberId}")
    public ResponseEntity<SuccessResponse<MemberInfoResponse>> getMember(
        // TODO 시큐리티 로그인 적용 이후 토큰에서 memberId 받아오도록 수정
        @PathVariable(name = "memberId") long memberId) {
        return ApiResponse.success(HttpStatus.OK, SuccessResponse.<MemberInfoResponse>builder()
            .message("성공적으로 회원 정보를 조회했습니다.")
            .data(memberGetService.getMember(memberId))
            .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<RefreshTokenResponse>> refresh(
        @Valid @RequestBody TokenRequest request) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<RefreshTokenResponse>builder()
                .message("리프레쉬 토큰 재발급이 성공적으로 완료되었습니다.")
                .data(memberAuthService.refresh(request))
                .build());
    }
}
