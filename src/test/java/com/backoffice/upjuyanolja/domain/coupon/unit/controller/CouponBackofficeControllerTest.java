package com.backoffice.upjuyanolja.domain.coupon.unit.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backoffice.upjuyanolja.domain.coupon.controller.CouponBackofficeController;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponMakeViewResponse;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomsResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficePrincipalService;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponBackofficeService;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponValidationService;
import com.backoffice.upjuyanolja.global.security.AuthenticationConfig;
import com.backoffice.upjuyanolja.global.security.SecurityUtil;
import java.util.ArrayList;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(value = CouponBackofficeController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        SecurityConfig.class,
        AuthenticationConfig.class})},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CouponBackofficeControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private CouponBackofficeService couponBackofficeService;

  @MockBean
  private CouponValidationService couponValidationService;

  @MockBean
  private CouponBackofficePrincipalService couponPrincipalService;

  @MockBean
  private SecurityUtil securityUtil;

  @Nested
  @DisplayName("makeCouponViewResponse 는")
  class Context_makeCouponViewResponse {

    @Test
    @DisplayName(("숙소 id와 숙소 이름 + List 형태로 객실 id, 객실명, 객실 가격을 응답한다."))
    public void _willSuccess() throws Exception {
      // given
      Long accommodationId = 1L;
      given(couponBackofficeService.getRoomsByAccommodation(accommodationId))
          .willReturn(makeCouponViewResponse());

      // when & then
      mockMvc.perform(get("/api/coupons/backoffice/buy/{accommodationId}", accommodationId))
          .andExpect(status().is(HttpStatus.OK.value()))
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.message").isString())
          .andExpect(jsonPath("$.data.accommodationId").isNumber())
          .andExpect(jsonPath("$.data.accommodationName").isString())
          .andExpect(jsonPath("$.data.rooms").isArray())
          .andExpect(jsonPath("$.data.rooms[0].roomId").isNumber())
          .andExpect(jsonPath("$.data.rooms[0].roomName").isString())
          .andExpect(jsonPath("$.data.rooms[0].roomPrice").isNumber())
          .andDo(print());
    }
  }

  private CouponMakeViewResponse makeCouponViewResponse() {
    List<CouponRoomsResponse> roomResponses = new ArrayList<>();
    roomResponses.add(CouponRoomsResponse.of(1L, "스탠다드", 100000));
    roomResponses.add(CouponRoomsResponse.of(2L, "디럭스", 200000));
    roomResponses.add(CouponRoomsResponse.of(3L, "스위트", 300000));

    return new CouponMakeViewResponse(
        1L,
        "대박 호텔",
        roomResponses);
  }
}
