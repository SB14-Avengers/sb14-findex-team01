package com.sprint.findex.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 쿼리 고도화(필터+정렬+커서 페이지네이션)용 JPAQueryFactory 빈 등록.
@Configuration
public class QueryDSLConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
