package com.sprint.findex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling // 배치에 의한 지수 데이터 연동 자동화(자동 연동 설정)에 필요
@SpringBootApplication
public class FindexApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindexApplication.class, args);
    }
}
