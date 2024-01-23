package com.backoffice.upjuyanolja.domain.member.controller;

import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerEmailRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.OwnerSignupRequest;
import com.backoffice.upjuyanolja.domain.member.dto.request.SignInRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerEmailResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerSignupResponse;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignInResponse;
import com.backoffice.upjuyanolja.domain.member.service.OwnerAuthService;
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
    public ResponseEntity<OwnerEmailResponse> sendMessage(
        @Valid @RequestBody OwnerEmailRequest request
    ) {
        OwnerEmailResponse response = ownerAuthService.sendVerificationCodeToEmail(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("owners/verify")
    public ResponseEntity<String> verifyMessage(
        @Valid @RequestParam(name = "email") String email,
        @RequestParam(name = "verification-code") String authCode
    ) {
        String response = ownerAuthService.verifyCode(email, authCode);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("owners/signup")
    public ResponseEntity<OwnerSignupResponse> ownerSignup(
        @Valid @RequestBody OwnerSignupRequest request
    ) {
        OwnerSignupResponse response = ownerAuthService.signup(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("owners/signin")
    public ResponseEntity<SignInResponse> ownerSignIn(
        @Valid @RequestBody SignInRequest request
    ) {
        SignInResponse response = ownerAuthService.signin(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
