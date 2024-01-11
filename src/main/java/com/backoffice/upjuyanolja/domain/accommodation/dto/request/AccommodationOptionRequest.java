package com.backoffice.upjuyanolja.domain.accommodation.dto.request;

import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AccommodationOptionRequest(
    @NotNull(message = "객실 내 취사 여부를 입력하세요.")
    boolean cooking,

    @NotNull(message = "주차 시설 여부를 입력하세요.")
    boolean parking,

    @NotNull(message = "픽업 서비스 여부를 입력하세요.")
    boolean pickup,

    @NotNull(message = "바베큐장 여부를 입력하세요.")
    boolean barbecue,

    @NotNull(message = "피트니스 센터 여부를 입력하세요.")
    boolean fitness,

    @NotNull(message = "노래방 여부를 입력하세요.")
    boolean karaoke,

    @NotNull(message = "사우나실 여부를 입력하세요.")
    boolean sauna,

    @NotNull(message = "스포츠 시설 여부를 입력하세요.")
    boolean sports,

    @NotNull(message = "세미나실 여부를 입력하세요.")
    boolean seminar
) {

    public static AccommodationOption toEntity(AccommodationOptionRequest request) {
        return AccommodationOption.builder()
            .cooking(request.cooking)
            .parking(request.parking)
            .pickup(request.pickup)
            .barbecue(request.barbecue)
            .fitness(request.fitness)
            .sauna(request.sauna)
            .sports(request.sports)
            .seminar(request.seminar)
            .build();
    }
}
