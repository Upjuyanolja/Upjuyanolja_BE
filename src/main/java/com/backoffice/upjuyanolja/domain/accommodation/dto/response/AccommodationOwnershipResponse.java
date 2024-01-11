package com.backoffice.upjuyanolja.domain.accommodation.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record AccommodationOwnershipResponse(
    List<AccommodationNameResponse> accommodations
) {

}
