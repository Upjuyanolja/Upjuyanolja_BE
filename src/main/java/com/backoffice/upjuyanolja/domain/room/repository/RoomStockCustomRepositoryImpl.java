package com.backoffice.upjuyanolja.domain.room.repository;

import com.backoffice.upjuyanolja.domain.room.entity.QRoom;
import com.backoffice.upjuyanolja.domain.room.entity.QRoomStock;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 객실 재고 Custom Repository Implementation
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Repository
@RequiredArgsConstructor
public class RoomStockCustomRepositoryImpl implements RoomStockCustomRepository {

    /**
     * JPA Query Factory
     */
    private final JPAQueryFactory queryFactory;

    /**
     * 객실 QClass
     */
    private final QRoom qRoom = QRoom.room;

    /**
     * 객실 재고 QClass
     */
    private final QRoomStock qRoomStock = QRoomStock.roomStock;

    /**
     * 특정 객실에 대한 오늘을 포함한 이후 객실 재고를 조회하는 메서드
     *
     * @param room 객실 재고를 조회할 객실 Entity
     * @return 객실 재고 Entity 리스트
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @Override
    public List<RoomStock> findAllByRoomAndAfterToday(Room room) {
        JPAQuery<RoomStock> query = queryFactory
            .selectFrom(qRoomStock)
            .leftJoin(qRoomStock.room, qRoom).fetchJoin()
            .where(createSearchConditionsBuilder(room));
        return query.fetch();
    }

    /**
     * 객실 재고 목록 조회 조건 Builder 생성 메서드
     *
     * @param room 객실 재고 목록 조회할 객실 Entity
     * @return 객실 재고 목록 조회 조건을 담은 Boolean Builder
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    private BooleanBuilder createSearchConditionsBuilder(Room room) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qRoomStock.room.eq(room));
        booleanBuilder.and(qRoomStock.date.goe(LocalDate.now()));
        return booleanBuilder;
    }
}
