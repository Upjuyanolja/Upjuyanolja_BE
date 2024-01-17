package com.backoffice.upjuyanolja.domain.member.service;

import com.backoffice.upjuyanolja.domain.member.dto.request.SignInRequest;
import com.backoffice.upjuyanolja.domain.member.dto.response.SignInResponse;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AuthServiceProvider<R, T> {

    R signup(T parameters);

    SignInResponse signin(SignInRequest request);
}
