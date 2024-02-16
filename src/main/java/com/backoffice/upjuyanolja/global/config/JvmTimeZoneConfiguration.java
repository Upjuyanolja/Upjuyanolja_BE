package com.backoffice.upjuyanolja.global.config;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.context.annotation.Configuration;

/**
 * JVM 타임 존 설정 Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Configuration
public class JvmTimeZoneConfiguration {

    /**
     * 어플리케이션 실행 시 필수적으로 타임 존 설정을 하는 메서드
     *
     * @author JeongUijeong (jeong275117@gmail.com)
     */
    @PostConstruct
    public void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
