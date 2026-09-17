package com.sprint.findex.domain.openapi.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** 서비스 키 누락은 호출 시점에 실패한다. URI 누락이나 유효하지 않은 타임아웃은 빈 생성 시 기동을 실패시킨다. */
@Getter
@Setter
@ConfigurationProperties(prefix = "findex.openapi")
public class OpenApiProperties {

    /** 주가지수시세 오퍼레이션 전체 URL. */
    private String uri;

    /** 공공데이터포털 디코딩 서비스 키. 인코딩은 클라이언트가 수행한다. */
    private String serviceKey = "";

    private int numOfRows = 100;

    private Duration connectTimeout = Duration.ofSeconds(3);

    /** 페이지 1회 요청의 타임아웃. 전체 페이지를 합친 호출 시간 제한은 없다. */
    private Duration callTimeout = Duration.ofSeconds(15);
}
