package com.backoffice.upjuyanolja.domain.room.controller;

import com.backoffice.upjuyanolja.domain.room.dto.request.RoomPageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomCommandUseCase roomCommandUseCase;
    private final SecurityUtil securityUtil;

    @PostMapping("/{accommodationId}")
    public ResponseEntity<RoomInfoResponse> registerRoom(
        @PathVariable long accommodationId,
        @RequestBody RoomRegisterRequest roomRegisterRequest
    ) {
        log.info("POST /api/rooms/{}", accommodationId);

        RoomInfoResponse response = roomCommandUseCase.registerRoom(
            securityUtil.getCurrentMemberId(),
            accommodationId,
            roomRegisterRequest
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/list/{accommodationId}")
    public ResponseEntity<RoomPageResponse> getRooms(
        @PathVariable long accommodationId,
        @RequestParam(defaultValue = "0") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        log.info("GET /api/rooms/list/{}", accommodationId);

        RoomPageResponse response = roomCommandUseCase.getRooms(
            securityUtil.getCurrentMemberId(),
            accommodationId,
            RoomPageRequest.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build().of()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomInfoResponse> getRoom(@PathVariable long roomId) {
        log.info("GET /api/rooms/{}", roomId);

        RoomInfoResponse response = roomCommandUseCase
            .getRoom(securityUtil.getCurrentMemberId(), roomId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomInfoResponse> modifyRoom(
        @PathVariable long roomId,
        @RequestBody RoomUpdateRequest roomUpdateRequest
    ) {
        log.info("PUT /api/rooms/{}", roomId);

        RoomInfoResponse response = roomCommandUseCase
            .modifyRoom(securityUtil.getCurrentMemberId(), roomId, roomUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomInfoResponse> deleteRoom(@PathVariable long roomId) {
        log.info("DELETE /api/rooms/{}", roomId);

        RoomInfoResponse response = roomCommandUseCase
            .deleteRoom(securityUtil.getCurrentMemberId(), roomId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
