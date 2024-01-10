package com.backoffice.upjuyanolja.domain.reservation.entity;

import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.global.common.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ReservationRoom extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("예약 객실 식별자")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  @Comment("객실 식별자")
  private Room room;

  @Column(nullable = false, name = "start_date")
  @Comment("입실 일자")
  private LocalDate startDate;

  @Column(nullable = false, name = "end_date")
  @Comment("퇴실 일자")
  private LocalDate endDate;

  @Column(nullable = false, name = "price")
  @Comment("객실 가격") // 예약 결제 할 당시의 객실 가격
  private Integer price;

  @Builder
  public ReservationRoom(
      Long id,
      Room room,
      LocalDate startDate,
      LocalDate endDate,
      Integer price
  ) {
    this.id = id;
    this.room = room;
    this.startDate = startDate;
    this.endDate = endDate;
    this.price = price;
  }
}
