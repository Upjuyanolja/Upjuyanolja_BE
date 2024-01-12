package com.backoffice.upjuyanolja.domain.room.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.room.controller.RoomController;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomOptionRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomImageResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomOptionResponse;
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
    @DisplayName("registerAccommodation()은")
    class Context_registerAccommodation {

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
}