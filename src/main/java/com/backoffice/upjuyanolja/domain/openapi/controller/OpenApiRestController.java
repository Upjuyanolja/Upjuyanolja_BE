package com.backoffice.upjuyanolja.domain.openapi.controller;

import com.backoffice.upjuyanolja.domain.openapi.service.OpenApiService;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/open-api")
@RequiredArgsConstructor
public class OpenApiRestController {

    private final OpenApiService openApiService;

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> getDataFromOpenApi(
        @RequestParam int pageSize,
        @RequestParam int pageNum
    ) {
        openApiService.getData(pageSize, pageNum);
        return ApiResponse.success(HttpStatus.OK, SuccessResponse.<Void>builder()
            .message("성공적으로 오픈 API에서 데이터를 수집하고 저장했습니다.")
            .build()
        );
    }
}
