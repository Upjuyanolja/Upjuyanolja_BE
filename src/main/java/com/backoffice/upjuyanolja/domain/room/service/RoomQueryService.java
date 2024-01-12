package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomImageRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomQueryService implements RoomQueryUseCase {

    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;

    @Override
    public Room saveRoom(Accommodation accommodation, Room room) {
        return roomRepository.save(room);
    }

    @Override
    public List<RoomImage> saveRoomImages(List<RoomImage> requests) {
        return roomImageRepository.saveAll(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(long roomId) {
        return roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsRoomByName(String name) {
        return roomRepository.existsRoomByName(name);
    }
}
