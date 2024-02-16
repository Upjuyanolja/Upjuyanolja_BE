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

/**
 * 객실 재고 생성을 위한 스케줄러
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@EnableScheduling
@RequiredArgsConstructor
public class RoomStockScheduler {

    /**
     * 객실 Repository Interface
     */
    private final RoomRepository roomRepository;

    /**
     * 객실 재고 Repository Interface
     */
    private final RoomStockRepository roomStockRepository;

    /**
     * 매일 새벽 3시에 29일 뒤 재고를 생성하는 메서드
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
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
