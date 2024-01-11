package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomImageRequest;
import com.backoffice.upjuyanolja.domain.room.dto.request.RoomRegisterRequest;
import com.backoffice.upjuyanolja.domain.room.dto.response.RoomInfoResponse;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.exception.DuplicateRoomNameException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomImageRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;

    public Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new RoomNotFoundException());
    }

    public RoomInfoResponse saveRoom(Accommodation accommodation, RoomRegisterRequest request) {
        roomNameValidate(request.name());
        Room room = roomRepository.save(RoomRegisterRequest.toEntity(accommodation, request));
        roomImageRepository.saveAll(RoomImageRequest.toEntity(room, request.images()));
        room = roomRepository.findById(room.getId()).orElseThrow(RoomNotFoundException::new);
        return RoomInfoResponse.of(room);
    }

    private void roomNameValidate(String name) {
        if (isDuplicatedRoomName(name)) {
            throw new DuplicateRoomNameException();
        }
    }

    public boolean isDuplicatedRoomName(String name) {
        return roomRepository.existsRoomByName(name);
    }
}
