package com.backoffice.upjuyanolja.domain.member.service;

import com.backoffice.upjuyanolja.domain.member.dto.response.OwnerInfoResponse;
import com.backoffice.upjuyanolja.domain.member.entity.Owner;
import com.backoffice.upjuyanolja.domain.member.exception.MemberNotFoundException;
import com.backoffice.upjuyanolja.domain.member.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerGetService {

    private final OwnerRepository ownerRepository;

    public OwnerInfoResponse getMember(long ownerId) {
        return OwnerInfoResponse.of(getOwnerById(ownerId));
    }

    public Owner getOwnerById(long ownerId) {
        return ownerRepository.findById(ownerId).orElseThrow(MemberNotFoundException::new);
    }
}
