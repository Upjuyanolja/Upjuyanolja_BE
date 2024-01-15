package com.backoffice.upjuyanolja.domain.room.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.room.controller.RoomController;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageAddRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageDeleteRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomUpdateRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomImageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomOptionResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomPageResponse;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomCommandUseCase;
import com.backoffice.upjuyanolja.global.security.AuthenticationConfig;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = RoomController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class,
        AuthenticationConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class RoomControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private RoomCommandUseCase roomCommandUseCase;

    @MockBean
    private SecurityUtil securityUtil;

    @Nested
    @DisplayName("registerRoom()은")
    class Context_registerRoom {

        @Test
        @DisplayName("객실을 등록할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            RoomRegisterRequest request = RoomRegisterRequest.builder()
                .name("65m² 킹룸")
                .price(100000)
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .amount(858)
                .images(List.of(RoomImageRequest.builder()
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .option(RoomOptionRequest.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .build();

            RoomInfoResponse roomInfoResponse = RoomInfoResponse.builder()
                .id(1L)
                .name("65m² 킹룸")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .price(100000)
                .amount(858)
                .status("SELLING")
                .option(RoomOptionResponse.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .images(List.of(RoomImageResponse.builder()
                    .id(1L)
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(roomCommandUseCase
                .registerRoom(any(Long.TYPE), any(Long.TYPE), any(RoomRegisterRequest.class)))
                .willReturn(roomInfoResponse);

            // when then
            mockMvc.perform(post("/api/rooms/{accommodationId}", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").isString())
                .andExpect(jsonPath("$.data.defaultCapacity").isNumber())
                .andExpect(jsonPath("$.data.maxCapacity").isNumber())
                .andExpect(jsonPath("$.data.checkInTime").isString())
                .andExpect(jsonPath("$.data.checkOutTime").isString())
                .andExpect(jsonPath("$.data.price").isNumber())
                .andExpect(jsonPath("$.data.amount").isNumber())
                .andExpect(jsonPath("$.data.status").isString())
                .andExpect(jsonPath("$.data.images").isArray())
                .andExpect(jsonPath("$.data.images[0].id").isNumber())
                .andExpect(jsonPath("$.data.images[0].url").isString())
                .andExpect(jsonPath("$.data.option").isMap())
                .andExpect(jsonPath("$.data.option.airCondition").isBoolean())
                .andExpect(jsonPath("$.data.option.tv").isBoolean())
                .andExpect(jsonPath("$.data.option.internet").isBoolean())
                .andDo(print());

            verify(roomCommandUseCase, times(1))
                .registerRoom(any(Long.TYPE), any(Long.TYPE), any(RoomRegisterRequest.class));
        }
    }

    @Nested
    @DisplayName("getRooms()은")
    class Context_getRooms {

        @Test
        @DisplayName("객실 목록을 조회할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            RoomInfoResponse roomInfoResponse = RoomInfoResponse.builder()
                .id(1L)
                .name("65m² 킹룸")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .price(100000)
                .amount(858)
                .status("SELLING")
                .option(RoomOptionResponse.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .images(List.of(RoomImageResponse.builder()
                    .id(1L)
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .build();

            RoomPageResponse roomPageResponse = RoomPageResponse.builder()
                .pageNum(0)
                .pageSize(10)
                .totalPages(1)
                .totalElements(1)
                .isLast(true)
                .rooms(List.of(roomInfoResponse))
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(roomCommandUseCase
                .getRooms(any(Long.TYPE), any(Long.TYPE), any(Pageable.class)))
                .willReturn(roomPageResponse);

            // when then
            mockMvc.perform(get("/api/rooms/list/{accommodationId}", 1L)
                    .queryParam("pageNum", "0")
                    .queryParam("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.pageNum").isNumber())
                .andExpect(jsonPath("$.data.pageSize").isNumber())
                .andExpect(jsonPath("$.data.totalPages").isNumber())
                .andExpect(jsonPath("$.data.totalElements").isNumber())
                .andExpect(jsonPath("$.data.isLast").isBoolean())
                .andExpect(jsonPath("$.data.rooms").isArray())
                .andExpect(jsonPath("$.data.rooms[0].id").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].name").isString())
                .andExpect(jsonPath("$.data.rooms[0].defaultCapacity").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].maxCapacity").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].checkInTime").isString())
                .andExpect(jsonPath("$.data.rooms[0].checkOutTime").isString())
                .andExpect(jsonPath("$.data.rooms[0].price").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].amount").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].status").isString())
                .andExpect(jsonPath("$.data.rooms[0].images").isArray())
                .andExpect(jsonPath("$.data.rooms[0].images[0].id").isNumber())
                .andExpect(jsonPath("$.data.rooms[0].images[0].url").isString())
                .andExpect(jsonPath("$.data.rooms[0].option").isMap())
                .andExpect(jsonPath("$.data.rooms[0].option.airCondition").isBoolean())
                .andExpect(jsonPath("$.data.rooms[0].option.tv").isBoolean())
                .andExpect(jsonPath("$.data.rooms[0].option.internet").isBoolean())
                .andDo(print());

            verify(roomCommandUseCase, times(1))
                .getRooms(any(Long.TYPE), any(Long.TYPE), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getRoom()은")
    class Context_getRoom {

        @Test
        @DisplayName("객실을 조회할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            RoomInfoResponse roomInfoResponse = RoomInfoResponse.builder()
                .id(1L)
                .name("65m² 킹룸")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .price(100000)
                .amount(858)
                .status("SELLING")
                .option(RoomOptionResponse.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .images(List.of(RoomImageResponse.builder()
                    .id(1L)
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(roomCommandUseCase.getRoom(any(Long.TYPE), any(Long.TYPE)))
                .willReturn(roomInfoResponse);

            // when then
            mockMvc.perform(get("/api/rooms/{accommodationId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").isString())
                .andExpect(jsonPath("$.data.defaultCapacity").isNumber())
                .andExpect(jsonPath("$.data.maxCapacity").isNumber())
                .andExpect(jsonPath("$.data.checkInTime").isString())
                .andExpect(jsonPath("$.data.checkOutTime").isString())
                .andExpect(jsonPath("$.data.price").isNumber())
                .andExpect(jsonPath("$.data.amount").isNumber())
                .andExpect(jsonPath("$.data.status").isString())
                .andExpect(jsonPath("$.data.images").isArray())
                .andExpect(jsonPath("$.data.images[0].id").isNumber())
                .andExpect(jsonPath("$.data.images[0].url").isString())
                .andExpect(jsonPath("$.data.option").isMap())
                .andExpect(jsonPath("$.data.option.airCondition").isBoolean())
                .andExpect(jsonPath("$.data.option.tv").isBoolean())
                .andExpect(jsonPath("$.data.option.internet").isBoolean())
                .andDo(print());

            verify(roomCommandUseCase, times(1)).getRoom(any(Long.TYPE), any(Long.TYPE));
        }
    }

    @Nested
    @DisplayName("modifyRoom()은")
    class Context_modifyRoom {

        @Test
        @DisplayName("객실을 수정할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            RoomUpdateRequest request = RoomUpdateRequest.builder()
                .name("65m² 킹룸")
                .price(200000)
                .status("STOP_SELLING")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .amount(858)
                .addImages(List.of(RoomImageAddRequest.builder()
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .deleteImages(List.of(RoomImageDeleteRequest.builder()
                    .id(1L)
                    .build()))
                .option(RoomOptionRequest.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .build();

            RoomInfoResponse roomInfoResponse = RoomInfoResponse.builder()
                .id(1L)
                .name("65m² 킹룸")
                .defaultCapacity(2)
                .maxCapacity(3)
                .checkInTime("15:00")
                .checkOutTime("11:00")
                .price(200000)
                .amount(858)
                .status("STOP_SELLING")
                .option(RoomOptionResponse.builder()
                    .airCondition(true)
                    .tv(true)
                    .internet(true)
                    .build())
                .images(List.of(RoomImageResponse.builder()
                    .id(1L)
                    .url("http://tong.visitkorea.or.kr/cms/resource/77/2876777_image2_1.jpg")
                    .build()))
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(roomCommandUseCase
                .modifyRoom(any(Long.TYPE), any(Long.TYPE), any(RoomUpdateRequest.class)))
                .willReturn(roomInfoResponse);

            // when then
            mockMvc.perform(put("/api/rooms/{roomId}", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").isString())
                .andExpect(jsonPath("$.data.defaultCapacity").isNumber())
                .andExpect(jsonPath("$.data.maxCapacity").isNumber())
                .andExpect(jsonPath("$.data.checkInTime").isString())
                .andExpect(jsonPath("$.data.checkOutTime").isString())
                .andExpect(jsonPath("$.data.price").isNumber())
                .andExpect(jsonPath("$.data.amount").isNumber())
                .andExpect(jsonPath("$.data.status").isString())
                .andExpect(jsonPath("$.data.images").isArray())
                .andExpect(jsonPath("$.data.images[0].id").isNumber())
                .andExpect(jsonPath("$.data.images[0].url").isString())
                .andExpect(jsonPath("$.data.option").isMap())
                .andExpect(jsonPath("$.data.option.airCondition").isBoolean())
                .andExpect(jsonPath("$.data.option.tv").isBoolean())
                .andExpect(jsonPath("$.data.option.internet").isBoolean())
                .andDo(print());

            verify(roomCommandUseCase, times(1))
                .modifyRoom(any(Long.TYPE), any(Long.TYPE), any(RoomUpdateRequest.class));
        }
    }
}
