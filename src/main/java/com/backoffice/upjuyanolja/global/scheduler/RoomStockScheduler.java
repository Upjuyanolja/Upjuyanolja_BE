package com.backoffice.upjuyanolja.global.scheduler;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@RequiredArgsConstructor
public class RoomStockScheduler {

    private final RoomRepository roomRepository;
    private final RoomStockRepository roomStockRepository;

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    private void addStockAfter30Days() {
        List<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            roomStockRepository.save(RoomStock.builder()
                .room(room)
                .count(room.getAmount())
                .date(LocalDate.now().plusDays(29))
                .build());
        }
    }
}
