package com.backoffice.upjuyanolja.domain.room.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.exception.RoomImageNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomNotFoundException;
import com.backoffice.upjuyanolja.domain.room.exception.RoomStockNotFoundException;
import com.backoffice.upjuyanolja.domain.room.repository.RoomImageRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import com.backoffice.upjuyanolja.domain.room.service.usecase.RoomQueryUseCase;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomQueryService implements RoomQueryUseCase {

    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final RoomStockRepository roomStockRepository;

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
    public List<Room> findByAccommodationId(long accommodationId) {
        return roomRepository.findByAccommodationId(accommodationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Room> findAllByAccommodationId(long accommodationId, Pageable pageable) {
        return roomRepository.findAllByAccommodation(accommodationId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Room findRoomById(long roomId) {
        return roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsRoomByNameAndAccommodation(String name, Accommodation accommodation) {
        return roomRepository.existsRoomByNameAndAccommodation(name, accommodation);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomImage findRoomImage(long roomImageId) {
        return roomImageRepository.findById(roomImageId)
            .orElseThrow(RoomImageNotFoundException::new);
    }

    @Override
    public void deleteRoomImages(List<RoomImage> requests) {
        roomImageRepository.deleteAll(requests);
    }

    @Override
    public void saveRoomStock(RoomStock roomStock) {
        roomStockRepository.save(roomStock);
    }

    @Override
    public List<RoomStock> findStockByRoom(Room room) {
        return roomStockRepository.findByRoom(room)
            .orElseThrow(RoomStockNotFoundException::new);
    }

    @Override
    public List<RoomStock> findStocksByRoomAndDateAfter(Room room, LocalDate date) {
        return roomStockRepository.findAllByRoomAndDateAfter(room, date);
    }
}
