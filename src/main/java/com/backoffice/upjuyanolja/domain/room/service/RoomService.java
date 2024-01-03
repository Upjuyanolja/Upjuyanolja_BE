package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new RoomNotFoundException());
    }
}
