package com.backoffice.upjuyanolja.domain.accommodation.dto.request;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Category;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record AccommodationRegisterRequest(
    @NotBlank(message = "숙소 이름을 입력하세요.")
    @Size(min = 1, max = 30, message = "숙소 이름을 1~30자로 입력하세요.")
    String name,

    @NotBlank(message = "숙소 주소를 입력하세요.")
    String address,

    String detailAddress,

    @NotBlank(message = "숙소 설명을 입력하세요.")
    String description,

    @NotBlank(message = "숙소 유형을 입력하세요.")
    String category,

    @NotBlank(message = "숙소 대표 이미지를 입력하세요.")
    String thumbnail,

    @NotNull(message = "숙소 이미지를 한 개 이상 입력하세요.")
    List<AccommodationImageRequest> images,

    @NotNull(message = "숙소 옵션을 입력하세요.")
    AccommodationOptionRequest option,

    @NotNull(message = "객실을 하나 이상 입력하세요.")
    List<RoomRegisterRequest> rooms
) {

    public static Accommodation toEntity(
        AccommodationRegisterRequest request,
        Category category
    ) {
        return Accommodation.builder()
            .name(request.name())
            .address(Address.builder()
                .address(request.address())
                .detailAddress(request.detailAddress())
                .build())
            .category(category)
            .description(request.description())
            .thumbnail(request.thumbnail())
            .images(new ArrayList<>())
            .option(AccommodationOptionRequest.toEntity(request.option()))
            .rooms(new ArrayList<>())
            .build();
    }
}
