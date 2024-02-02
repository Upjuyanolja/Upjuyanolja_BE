package com.backoffice.upjuyanolja.domain.room.repository;

import static com.backoffice.upjuyanolja.domain.accommodation.entity.QAccommodation.accommodation;
import static com.backoffice.upjuyanolja.domain.room.entity.QRoom.room;
import static com.backoffice.upjuyanolja.domain.room.entity.QRoomImage.roomImage;
import static com.backoffice.upjuyanolja.domain.room.entity.QRoomOption.roomOption;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.global.util.QueryDslUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class RoomCustomRepositoryImpl implements RoomCustomRepository {

    private final JPAQueryFactory queryFactory;

    RoomCustomRepositoryImpl(
        JPAQueryFactory jpaQueryFactory
    ) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Room> findAllByAccommodation(long accommodationId, Pageable pageable) {
        JPAQuery<Room> query = queryFactory
            .select(room)
            .from(room)
            .leftJoin(room.accommodation, accommodation).fetchJoin()
            .leftJoin(room.images, roomImage).fetchJoin()
            .where(createSearchConditionsBuilder(accommodationId))
            .offset(pageable.getOffset())
            .orderBy(getAllOrderSpecifiers().toArray(OrderSpecifier[]::new))
            .limit(pageable.getPageSize());
        JPAQuery<Room> countQuery = queryFactory
            .select(room)
            .from(room)
            .leftJoin(room.accommodation, accommodation).fetchJoin()
            .leftJoin(room.images, roomImage).fetchJoin()
            .where(room.accommodation.id.eq(accommodationId));
        List<Room> content = query.fetch();
        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public boolean existsRoomByNameAndAccommodation(String name, Accommodation accommodation) {
        Integer fetchOne = queryFactory
            .selectOne()
            .from(room)
            .where(createExistsSearchConditionsBuilder(name, accommodation))
            .fetchFirst();

        return fetchOne != null;
    }

    private BooleanBuilder createSearchConditionsBuilder(long accommodationId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(room.accommodation.id.eq(accommodationId));
        booleanBuilder.and(room.deletedAt.isNull());
        return booleanBuilder;
    }

    private BooleanBuilder createExistsSearchConditionsBuilder(
        String name,
        Accommodation accommodation
    ) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(room.name.eq(name));
        booleanBuilder.and(room.accommodation.eq(accommodation));
        booleanBuilder.and(room.deletedAt.isNull());
        return booleanBuilder;
    }

    private List<OrderSpecifier<?>> getAllOrderSpecifiers() {
        List<OrderSpecifier<?>> orders = new LinkedList<>();
        orders.add(QueryDslUtil.getSortedColumn(Order.ASC, room, "status"));
        orders.add(QueryDslUtil.getSortedColumn(Order.ASC, room, "id"));
        return orders;
    }
}
