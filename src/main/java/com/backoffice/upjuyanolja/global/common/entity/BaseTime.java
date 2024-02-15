package com.backoffice.upjuyanolja.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 생성, 수정, 삭제 일시가 포함된 기본 Entity
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTime {

    /**
     * 생성 일시
     */
    @CreatedDate
    @Comment("생성 일시")
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @Column(insertable = false)
    @LastModifiedDate
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    /**
     * 삭제 일시
     */
    @Column(insertable = false)
    @Comment("삭제 일시")
    private LocalDateTime deletedAt;

    /**
     * 논리 삭제 메서드
     * <p>
     * 상속 시 재정의를 통해 사용할 수 있습니다.
     *
     * @param currentTime 삭제하려는 시점의 현재 일시
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    protected void delete(LocalDateTime currentTime) {
        if (deletedAt == null) {
            deletedAt = currentTime;
        }
    }

    /**
     * 논리 삭제 여부 확인 메서드
     *
     * @return 해당 엔터티 논리 삭제 여부
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * 논리 삭제를 취소하고 복구하는 메서드
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    protected void restore() {
        deletedAt = null;
    }
}
