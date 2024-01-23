package com.backoffice.upjuyanolja.domain.openapi.controller;

import com.backoffice.upjuyanolja.domain.openapi.service.OpenApiService;
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
    public ResponseEntity<Void> getDataFromOpenApi(
        @RequestParam int pageSize,
        @RequestParam int pageNum
    ) {
        openApiService.getData(pageSize, pageNum);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
