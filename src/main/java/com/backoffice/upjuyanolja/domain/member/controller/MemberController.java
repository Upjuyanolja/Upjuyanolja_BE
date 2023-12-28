package com.backoffice.upjuyanolja.domain.member.controller;

import com.backoffice.upjuyanolja.domain.member.dto.request.SingUpRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignUpResponse;
import com.backoffice.upjuyanolja.domain.member.service.MemberRegisterService;
import com.backoffice.upjuyanolja.global.common.ApiResponse;
import com.backoffice.upjuyanolja.global.common.ApiResponse.SuccessResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRegisterService memberRegisterService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<SignUpResponse>> signup(
        @Valid @RequestBody SingUpRequest request) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<SignUpResponse>builder()
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(memberRegisterService.signup(request))
                .build());
    }

    @GetMapping("/email/verify")
    public ResponseEntity<SuccessResponse<SignUpResponse>>verifyEmail(@NotNull String email) {
        memberRegisterService.validateDuplicatedEmail(email);
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<SignUpResponse>builder()
                .message("사용가능한 이메일입니다.")
                .build());
    }

}
