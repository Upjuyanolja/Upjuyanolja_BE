package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongCategoryException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationImageRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationQueryUseCase;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationQueryService implements AccommodationQueryUseCase {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Accommodation saveAccommodation(AccommodationSaveRequest request) {
        return accommodationRepository.save(Accommodation.builder()
            .name(request.name())
            .address(request.address())
            .description(request.description())
            .category(request.category())
            .thumbnail(request.thumbnail())
            .option(request.option())
            .rooms(new ArrayList<>())
            .images(new ArrayList<>())
            .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Accommodation getAccommodationById(long accommodationId) {
        return accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);
    }

    @Override
    public AccommodationOwnership saveOwnership(Member member, Accommodation accommodation) {
        return accommodationOwnershipRepository.save(AccommodationOwnership.builder()
            .accommodation(accommodation)
            .member(member)
            .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccommodationOwnership> getOwnershipByMember(Member member) {
        return accommodationOwnershipRepository.findAllByMember(member);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsOwnershipByMemberAndAccommodation(Member member,
        Accommodation accommodation) {
        return accommodationOwnershipRepository
            .existsAccommodationOwnershipByMemberAndAccommodation(member, accommodation);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) {
        return categoryRepository.findCategoryByName(name)
            .orElseThrow(WrongCategoryException::new);
    }

    @Override
    public List<AccommodationImage> saveAllImages(List<AccommodationImage> images) {
        return accommodationImageRepository.saveAll(images);
    }
}
