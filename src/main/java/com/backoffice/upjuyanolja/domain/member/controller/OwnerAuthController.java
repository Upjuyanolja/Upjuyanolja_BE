package com.backoffice.upjuyanolja.domain.member.controller;

import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerEmailRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerSignupRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignInRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerEmailResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerSignupResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignInResponse;
import com.backoffice.upjuyanolja.domain.member.service.OwnerAuthService;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class OwnerAuthController {

    private final OwnerAuthService ownerAuthService;

    @PostMapping("owners/request-email")
    public ResponseEntity<SuccessResponse<OwnerEmailResponse>> sendMessage(
        @Valid @RequestBody OwnerEmailRequest request) {
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
    ) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<String>builder()
                .message("이메일 인증이 성공적으로 완료되었습니다.")
                .data(ownerAuthService.verifyCode(email, authCode))
                .build());
    }

    @PostMapping("owners/signup")
    public ResponseEntity<SuccessResponse<OwnerSignupResponse>> ownerSignup(
        @Valid @RequestBody OwnerSignupRequest request
    ) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<OwnerSignupResponse>builder()
                .message("업주 회원가입이 성공적으로 완료되었습니다.")
                .data(ownerAuthService.signup(request))
                .build());
    }

    @PostMapping("owners/signin")
    public ResponseEntity<SuccessResponse<SignInResponse>> ownerSignup(
        @Valid @RequestBody SignInRequest request
    ) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<SignInResponse>builder()
                .message("업주 로그인이 성공적으로 완료되었습니다.")
                .data(ownerAuthService.signin(request))
                .build());
    }
}
