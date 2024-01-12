package com.backoffice.upjuyanolja.domain.accommodation.service.usecase;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import java.util.List;
import lombok.Builder;

public interface AccommodationQueryUseCase {

    Accommodation saveAccommodation(AccommodationSaveRequest accommodationSaveRequest);

    Accommodation getAccommodationById(long id);

    AccommodationOwnership saveOwnership(Member member, Accommodation accommodation);

    List<AccommodationOwnership> getOwnershipByMember(Member member);

    boolean existsOwnershipByMemberAndAccommodation(Member member, Accommodation accommodation);

    Category getCategoryByName(String name);

    List<AccommodationImage> saveAllImages(List<AccommodationImage> images);

    @Builder
    record AccommodationSaveRequest(
        String name,
        Address address,
        String description,
        Category category,
        String thumbnail,
        AccommodationOption option
    ) {

    }
}
