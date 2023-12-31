package com.backoffice.upjuyanolja.domain.member.controller;

import com.backoffice.upjuyanolja.domain.member.dto.request.SignUpRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.MemberInfoResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignUpResponse;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.member.service.MemberRegisterService;
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
public class MemberController {

    private final MemberRegisterService memberRegisterService;
    private final MemberGetService memberGetService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<SignUpResponse>> signup(
        @Valid @RequestBody SignUpRequest request) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<SignUpResponse>builder()
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(memberRegisterService.signup(request))
                .build());
    }

    @GetMapping("/email")
    public ResponseEntity<SuccessResponse<CheckEmailDuplicateResponse>> checkEmailDuplicate(
        @RequestParam(name = "email") String email
    ) {
        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<CheckEmailDuplicateResponse>builder()
                .message("성공적으로 이메일 중복 여부를 검사했습니다.")
                .data(memberRegisterService.checkEmailDuplicate(email))
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
}
