package com.backoffice.upjuyanolja.domain.accommodation.service;

import static com.backoffice.upjuyanolja.global.exception.ErrorCode.ROOM_NOT_FOUND;

import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationDetailResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationPageResponse;
import com.backoffice.upjuyanolja.domain.accommodation.dto.response.AccommodationSummaryResponse;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Room;
import com.backoffice.upjuyanolja.domain.accommodation.exception.AccommodationException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.coupon.dto.response.CouponRoomDetailResponse;
import com.backoffice.upjuyanolja.domain.coupon.service.CouponService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    public Room findRoomById(Long roomId){
        return roomRepository.findById(roomId)
            .orElseThrow(()->new AccommodationException(ROOM_NOT_FOUND));
    }
}
