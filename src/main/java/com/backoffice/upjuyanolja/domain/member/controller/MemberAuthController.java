package com.backoffice.upjuyanolja.domain.member.controller;

import com.backoffice.upjuyanolja.domain.member.dto.request.EmailRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignInRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignUpRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.TokenRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.CheckEmailDuplicateResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.MemberInfoResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignInResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignUpResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.TokenResponse;
import com.backoffice.upjuyanolja.domain.member.service.MemberAuthService;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class MemberAuthController {

    private final MemberAuthService memberAuthService;
    private final MemberGetService memberGetService;
    private final SecurityUtil securityUtil;

    @PostMapping("members/signup")
    public ResponseEntity<SignUpResponse> signup(
        @Valid @RequestBody SignUpRequest request
    ) {
        SignUpResponse response = memberAuthService.signup(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("members/email")
    public ResponseEntity<CheckEmailDuplicateResponse> checkEmailDuplicate(
        @RequestBody EmailRequest request
    ) {
        CheckEmailDuplicateResponse response = memberAuthService.checkEmailDuplicate(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("members/signin")
    public ResponseEntity<SignInResponse> signin(
        @Valid @RequestBody SignInRequest request
    ) {
        SignInResponse response = memberAuthService.signin(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("members")
    public ResponseEntity<MemberInfoResponse> getMember() {
        MemberInfoResponse response = memberGetService.getMember(
            securityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
        @Valid @RequestBody TokenRequest request
    ) {
        TokenResponse response = memberAuthService.refresh(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout() {
        memberAuthService.logout(securityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
