package com.backoffice.upjuyanolja.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfiguration {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(em);
    }
}

