package com.backoffice.upjuyanolja.domain.room.controller;

import com.backoffice.upjuyanolja.domain.room.dto.request.RoomPageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
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

/**
 * B2B Backoffice 객실 관리 API Controller Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Slf4j
@RestController
@RequestMapping("/backoffice-api/accommodations/{accommodationId}/rooms")
@RequiredArgsConstructor
public class RoomBackofficeController {

    /**
     * 객실 생성, 수정, 삭제 UseCase Interface
     */
    private final RoomCommandUseCase roomCommandUseCase;

    /**
     * 객실 조회 UseCase Interface
     */
    private final RoomQueryUseCase roomQueryUseCase;

    /**
     * Security Utility Class
     * <p>
     * JWT에서 업주 회원 식별자를 추출하기 위해 사용합니다.
     */
    private final SecurityUtil securityUtil;

    /**
     * 객실 등록 API Controller
     * <p>
     * 업주 회원은 보유한 숙소에 객실을 추가 등록할 수 있습니다.<br> 객실 등록을 위해서는 숙소 식별자, 객실 정보(기본 정보, 객실 옵션, 객실이미지 1개 이상)이
     * 필요합니다.
     *
     * @param accommodationId     객실을 추가 등록할 숙소 식별자
     * @param roomRegisterRequest 객실 등록 요청 DTO
     * @return 등록한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @PostMapping
    public ResponseEntity<RoomInfoResponse> registerRoom(
        @PathVariable long accommodationId,
        @RequestBody RoomRegisterRequest roomRegisterRequest
    ) {
        log.info("[POST] /backoffice-api/accommodations/{}/rooms", accommodationId);

        RoomInfoResponse response = roomCommandUseCase.registerRoom(
            securityUtil.getCurrentMemberId(),
            accommodationId,
            roomRegisterRequest
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 숙소의 객실 목록 조회 API Controller
     * <p>
     * 업주 회원은 보유한 숙소의 객실 목록을 조회할 수 있습니다.
     *
     * @param accommodationId 객실 목록을 조회할 숙소 식별자
     * @param pageNum         조회할 페이지 번호 (Default: 0)
     * @param pageSize        한 페이지에 조회할 객실 건수 (Default: 10)
     * @return 객실 정보 목록
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @GetMapping
    public ResponseEntity<RoomPageResponse> getRooms(
        @PathVariable long accommodationId,
        @RequestParam(defaultValue = "0") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        log.info(
            "[GET] /backoffice-api/accommodations/{}/rooms?pageNum={}&pageSize={}",
            accommodationId,
            pageNum,
            pageSize
        );

        RoomPageResponse response = roomQueryUseCase.getRooms(
            securityUtil.getCurrentMemberId(),
            accommodationId,
            RoomPageRequest.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build().of()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 특정 객실 상세 조회 API Controller
     * <p>
     * 업주 회원은 보유한 숙소의 특정 객실 상세 정보를 조회할 수 있습니다.
     *
     * @param accommodationId 조회하려는 객실이 속한 숙소 식별자
     * @param roomId          조회하려는 객실 식별자
     * @return 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomInfoResponse> getRoom(
        @PathVariable long accommodationId,
        @PathVariable long roomId
    ) {
        log.info("[GET] /backoffice-api/accommodations/{}/rooms/{}", accommodationId, roomId);

        RoomInfoResponse response = roomQueryUseCase
            .getRoom(securityUtil.getCurrentMemberId(), roomId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 객실 수정 API Controller
     * <p>
     * 업주 회원은 보유한 숙소의 객실 정보를 수정할 수 있습니다.
     *
     * @param accommodationId   수정하려는 객실이 속한 숙소 식별자
     * @param roomId            수정하려는 객실 식별자
     * @param roomUpdateRequest 객실 수정 요청 DTO
     * @return 수정한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomInfoResponse> modifyRoom(
        @PathVariable long accommodationId,
        @PathVariable long roomId,
        @RequestBody RoomUpdateRequest roomUpdateRequest
    ) {
        log.info("[PUT] /backoffice-api/accommodations/{}/rooms/{}", accommodationId, roomId);

        RoomInfoResponse response = roomCommandUseCase
            .modifyRoom(securityUtil.getCurrentMemberId(), roomId, roomUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 객실 삭제 API Controller
     * <p>
     * 업주 회원은 보유한 숙소의 객실을 삭제할 수 있습니다.
     *
     * @param accommodationId 삭세하려는 객실이 속한 숙소 식별자
     * @param roomId          삭제하려는 객실 식별자
     * @return 삭제한 객실 정보
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomInfoResponse> deleteRoom(
        @PathVariable long accommodationId,
        @PathVariable long roomId
    ) {
        log.info("[DELETE] /backoffice-api/accommodations/{}/rooms/{}", accommodationId, roomId);

        RoomInfoResponse response = roomCommandUseCase
            .deleteRoom(securityUtil.getCurrentMemberId(), roomId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
