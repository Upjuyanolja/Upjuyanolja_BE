package com.backoffice.upjuyanolja.domain.accommodation.service;

import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationImageRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationOptionRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.request.AccommodationRegisterRequest;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationInfoResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationOptionResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOwnership;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationImageNotExistsException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationNotFoundException;
import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongCategoryException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationImageRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOptionRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationOwnershipRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.CategoryRepository;
import com.backoffice.upjuyanolja.domain.accommodation.service.usecase.AccommodationCommandUseCase;
import com.backoffice.upjuyanolja.domain.member.entity.Member;
import com.backoffice.upjuyanolja.domain.member.service.MemberGetService;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.service.RoomCommandService;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationCommandService implements AccommodationCommandUseCase {

    private final MemberGetService memberGetService;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationOptionRepository accommodationOptionRepository;
    private final CategoryRepository categoryRepository;
    private final AccommodationOwnershipRepository accommodationOwnershipRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final RoomCommandService roomCommandService;
    private final EntityManager em;

    @Override
    public AccommodationInfoResponse createAccommodation(
        long memberId,
        AccommodationRegisterRequest request
    ) {
        Member member = memberGetService.getMemberById(memberId);

        Category category = getCategory(request.category());

        Accommodation accommodation = accommodationRepository.save(Accommodation.builder()
            .name(request.name())
            .address(request.address())
            .detailAddress(request.detailAddress())
            .zipCode(request.zipCode())
            .description(request.description())
            .category(category)
            .thumbnail(request.thumbnail())
            .rooms(new ArrayList<>())
            .images(new ArrayList<>())
            .build());

        imageValidate(request.images());
        accommodationImageRepository.saveAll(AccommodationImageRequest
            .toEntity(accommodation, request.images()));

        accommodationOwnershipRepository.save(AccommodationOwnership.builder()
            .accommodation(accommodation)
            .member(member)
            .build());

        roomValidate(request.rooms());
        request.rooms().forEach(
            roomRegisterRequest -> roomCommandService.saveRoom(accommodation, roomRegisterRequest));

        AccommodationOption option = createAccommodationOption(accommodation, request.option());

        em.refresh(accommodation);
        em.refresh(option);

        List<RoomInfoResponse> room = roomCommandService.getRoomsInAccommodation(accommodation);

        return AccommodationInfoResponse.of(accommodation, option, room);
    }

    public AccommodationOption createAccommodationOption(
        Accommodation accommodation, AccommodationOptionRequest request
    ) {
        AccommodationOption accommodationOption = accommodationOptionRepository.save(
            AccommodationOption.builder()
                .accommodation(accommodation)
                .cooking(request.cooking())
                .parking(request.parking())
                .pickup(request.pickup())
                .barbecue(request.barbecue())
                .fitness(request.fitness())
                .karaoke(request.karaoke())
                .sauna(request.sauna())
                .sports(request.sports())
                .seminar(request.seminar())
                .build()
        );

        return accommodationOption;
    }

    private Category getCategory(String category) {
        return categoryRepository.findCategoryByNameAndIdGreaterThan(category, 4L)
            .orElseThrow(WrongCategoryException::new);
    }

    private void imageValidate(List<AccommodationImageRequest> accommodationImageRequests) {
        if (accommodationImageRequests.isEmpty()) {
            throw new AccommodationImageNotExistsException();
        }
    }

    private void roomValidate(List<RoomRegisterRequest> roomRegisterRequests) {
        if (roomRegisterRequests.isEmpty()) {
            throw new RoomNotFoundException();
        }
    }
}
