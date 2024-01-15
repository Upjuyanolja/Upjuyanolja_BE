package com.backoffice.upjuyanolja.domain.room.controller;

import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse;
import com.backoffice.upjuyanolja.global.common.response.ApiResponse.SuccessResponse;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomCommandUseCase roomCommandUseCase;
    private final SecurityUtil securityUtil;

    @PostMapping("/{accommodationId}")
    public ResponseEntity<SuccessResponse<RoomInfoResponse>> registerRoom(
        @PathVariable long accommodationId,
        @RequestBody RoomRegisterRequest roomRegisterRequest
    ) {
        log.info("POST /api/rooms/{}", accommodationId);

        return ApiResponse.success(HttpStatus.CREATED,
            SuccessResponse.<RoomInfoResponse>builder()
                .message("성공적으로 객실을 등록했습니다.")
                .data(roomCommandUseCase.registerRoom(
                    securityUtil.getCurrentMemberId(),
                    accommodationId,
                    roomRegisterRequest)
                )
                .build()
        );
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<SuccessResponse<RoomInfoResponse>> modifyRoom(
        @PathVariable long roomId,
        @RequestBody RoomUpdateRequest roomUpdateRequest
    ) {
        log.info("PUT /api/rooms/{}", roomId);

        return ApiResponse.success(HttpStatus.OK,
            SuccessResponse.<RoomInfoResponse>builder()
                .message("성공적으로 객실을 수정했습니다.")
                .data(roomCommandUseCase.modifyRoom(
                    securityUtil.getCurrentMemberId(),
                    roomId,
                    roomUpdateRequest)
                )
                .build()
        );
    }
}
