package com.backoffice.upjuyanolja.domain.member.dto.response;

import lombok.Builder;

@Builder
public record CheckEmailDuplicateResponse(
    boolean isExists
) {

}
